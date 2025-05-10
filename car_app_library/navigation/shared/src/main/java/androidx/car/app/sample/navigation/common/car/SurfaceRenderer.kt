/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.car.app.sample.navigation.common.car

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import androidx.annotation.MainThread
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.sample.navigation.common.R
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** A very simple implementation of a renderer for the app's background surface.  */
class SurfaceRenderer(private val mCarContext: CarContext, lifecycle: Lifecycle) :
    DefaultLifecycleObserver {
    var mSurface: Surface? = null
    var mVisibleArea: Rect? = null
    var mStableArea: Rect? = null

    private val mLeftInsetPaint = Paint()
    private val mRightInsetPaint = Paint()
    private val mCenterPaint = Paint()
    private val mMarkerPaint = Paint()

    private var mShowMarkers = false
    private var mNumMarkers = 0
    private var mActiveMarker = 0
    private var mLocationString = "unknown"

    /** The bitmap that contains the background map image.  */
    private val mBackgroundMap: Bitmap

    /**
     * The transformation matrix for the background map image, to reflect the result of the user's
     * pan and zoom actions.
     */
    val mBackgroundMapMatrix: Matrix = Matrix()

    /** The cumulative scale factor for the background map image.  */
    var mCumulativeScaleFactor: Float = 1f

    /**
     * The current horizontal center point of the background map, used to prevent the map from
     * disappearing from pan actions.
     */
    var mBackgroundMapCenterX: Float = 0f

    /**
     * The current vertical center point of the background map, used to prevent the map from
     * disappearing from pan actions.
     */
    var mBackgroundMapCenterY: Float = 0f

    /**
     * The original clip bounds of the background map, used to prevent the map from disappearing
     * from pan actions.
     */
    var mBackgroundMapClipBounds: Rect = Rect()

    val mSurfaceCallback: SurfaceCallback = object : SurfaceCallback {
        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            synchronized(this@SurfaceRenderer) {
                Log.i(TAG, "Surface available $surfaceContainer")
                if (mSurface != null) {
                    mSurface!!.release()
                }
                mSurface = surfaceContainer.surface
                renderFrame()
            }
        }

        override fun onVisibleAreaChanged(visibleArea: Rect) {
            synchronized(this@SurfaceRenderer) {
                Log.i(
                    TAG, "Visible area changed " + mSurface + ". stableArea: "
                            + mStableArea + " visibleArea:" + visibleArea
                )
                mVisibleArea = visibleArea
                renderFrame()
            }
        }

        override fun onStableAreaChanged(stableArea: Rect) {
            synchronized(this@SurfaceRenderer) {
                Log.i(
                    TAG, "Stable area changed " + mSurface + ". stableArea: "
                            + mStableArea + " visibleArea:" + mVisibleArea
                )
                mStableArea = stableArea
                renderFrame()
            }
        }

        override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
            synchronized(this@SurfaceRenderer) {
                Log.i(TAG, "Surface destroyed")
                if (mSurface != null) {
                    mSurface!!.release()
                    mSurface = null
                }
            }
        }

        override fun onScroll(distanceX: Float, distanceY: Float) {
            synchronized(this@SurfaceRenderer) {
                val newBackgroundCenterX = mBackgroundMapCenterX - distanceX
                val newBackgroundCenterY = mBackgroundMapCenterY - distanceY

                // If the map stays within the clip bounds, pan the map.
                if (mBackgroundMapClipBounds.contains(
                        newBackgroundCenterX.toInt(),
                        newBackgroundCenterY.toInt()
                    )
                ) {
                    mBackgroundMapCenterX = newBackgroundCenterX
                    mBackgroundMapCenterY = newBackgroundCenterY
                    mBackgroundMapMatrix.postTranslate(-distanceX, -distanceY)
                    renderFrame()
                }
            }
        }

        override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) {
            handleScale(focusX, focusY, scaleFactor)
        }
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RENDER_FRAME -> doRenderFrame()
            }
        }
    }

    init {
        mLeftInsetPaint.color = Color.RED
        mLeftInsetPaint.isAntiAlias = true
        mLeftInsetPaint.style = Paint.Style.STROKE

        mRightInsetPaint.color = Color.RED
        mRightInsetPaint.isAntiAlias = true
        mRightInsetPaint.style = Paint.Style.STROKE
        mRightInsetPaint.textAlign = Align.RIGHT

        mCenterPaint.color = Color.BLUE
        mCenterPaint.isAntiAlias = true
        mCenterPaint.style = Paint.Style.STROKE

        mMarkerPaint.color = Color.GREEN
        mMarkerPaint.isAntiAlias = true
        mMarkerPaint.style = Paint.Style.STROKE
        mMarkerPaint.strokeWidth = 3f

        mBackgroundMap = BitmapFactory.decodeResource(
            mCarContext.resources,
            R.drawable.map
        )
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.i(TAG, "SurfaceRenderer created")
        mCarContext.getCarService(AppManager::class.java).setSurfaceCallback(mSurfaceCallback)
    }

    /** Callback called when the car configuration changes.  */
    fun onCarConfigurationChanged() {
        renderFrame()
    }

    /** Handles the map zoom-in and zoom-out events.  */
    fun handleScale(focusX: Float, focusY: Float, scaleFactor: Float) {
        synchronized(this) {
            var x = focusX
            var y = focusY

            val visibleArea = mVisibleArea
            if (visibleArea != null) {
                // If a focal point value is negative, use the center point of the visible area.
                if (x < 0) {
                    x = visibleArea.centerX().toFloat()
                }
                if (y < 0) {
                    y = visibleArea.centerY().toFloat()
                }
            }

            // If the map stays between the maximum and minimum scale factors, scale the map.
            val newCumulativeScaleFactor = mCumulativeScaleFactor * scaleFactor
            if (newCumulativeScaleFactor > MIN_SCALE_FACTOR
                && newCumulativeScaleFactor < MAX_SCALE_FACTOR
            ) {
                mCumulativeScaleFactor = newCumulativeScaleFactor
                mBackgroundMapMatrix.postScale(scaleFactor, scaleFactor, x, y)
                renderFrame()
            }
        }
    }

    /** Handles the map re-centering events.  */
    fun handleRecenter() {
        // Resetting the map matrix will trigger the initialization logic in renderFrame().
        mBackgroundMapMatrix.reset()
        renderFrame()
    }

    /** Updates the markers drawn on the surface.  */
    fun updateMarkerVisibility(showMarkers: Boolean, numMarkers: Int, activeMarker: Int) {
        mShowMarkers = showMarkers
        mNumMarkers = numMarkers
        mActiveMarker = activeMarker
        renderFrame()
    }

    /** Updates the location coordinate string drawn on the surface.  */
    fun updateLocationString(locationString: String) {
        mLocationString = locationString
        renderFrame()
    }

    fun renderFrame() {
        mHandler.sendEmptyMessage(MSG_RENDER_FRAME)
    }

    @MainThread
    fun doRenderFrame() {
        if (mSurface == null || !mSurface!!.isValid) {
            // Surface is not available, or has been destroyed, skip this frame.
            return
        }
        val canvas = mSurface!!.lockCanvas(null)

        // Clear the background.
        canvas.drawColor(if (mCarContext.isDarkMode) Color.DKGRAY else Color.LTGRAY)

        // Initialize the background map.
        if (mBackgroundMapMatrix.isIdentity) {
            // Enlarge the original image.
            val backgroundRect = RectF(
                0f, 0f, mBackgroundMap.width.toFloat(),
                mBackgroundMap.height.toFloat()
            )
            val scaledBackgroundRect = RectF(
                0f, 0f,
                backgroundRect.width() * MAP_ENLARGE_FACTOR,
                backgroundRect.height() * MAP_ENLARGE_FACTOR
            )

            // Initialize the cumulative scale factor and map center points.
            mCumulativeScaleFactor = 1f
            mBackgroundMapCenterX = scaledBackgroundRect.centerX()
            mBackgroundMapCenterY = scaledBackgroundRect.centerY()

            // Move to the center of the enlarged map.
            mBackgroundMapMatrix.setRectToRect(
                backgroundRect, scaledBackgroundRect,
                Matrix.ScaleToFit.FILL
            )
            mBackgroundMapMatrix.postTranslate(
                -mBackgroundMapCenterX + canvas.clipBounds.centerX(),
                -mBackgroundMapCenterY + canvas.clipBounds.centerY()
            )
            scaledBackgroundRect.round(mBackgroundMapClipBounds)
        }
        canvas.drawBitmap(mBackgroundMap, mBackgroundMapMatrix, null)


        val horizontalTextMargin = 10
        val verticalTextMarginFromTop = 20
        val verticalTextMarginFromBottom = 10

        // Draw a rectangle showing the inset.
        val visibleArea = mVisibleArea
        if (visibleArea != null) {
            if (visibleArea.isEmpty) {
                // No inset set. The entire area is considered safe to draw.
                visibleArea[0, 0, canvas.width - 1] = canvas.height - 1
            }

            canvas.drawRect(visibleArea, mLeftInsetPaint)
            canvas.drawLine(
                visibleArea.left.toFloat(),
                visibleArea.top.toFloat(),
                visibleArea.right.toFloat(),
                visibleArea.bottom.toFloat(),
                mLeftInsetPaint
            )
            canvas.drawLine(
                visibleArea.right.toFloat(),
                visibleArea.top.toFloat(),
                visibleArea.left.toFloat(),
                visibleArea.bottom.toFloat(),
                mLeftInsetPaint
            )
            canvas.drawText(
                "(" + visibleArea.left + " , " + visibleArea.top + ")",
                (visibleArea.left + horizontalTextMargin).toFloat(),
                (visibleArea.top + verticalTextMarginFromTop).toFloat(),
                mLeftInsetPaint
            )
            canvas.drawText(
                "(" + visibleArea.right + " , " + visibleArea.bottom + ")",
                (visibleArea.right - horizontalTextMargin).toFloat(),
                (visibleArea.bottom - verticalTextMarginFromBottom).toFloat(),
                mRightInsetPaint
            )

            // Draw location on the top right corner of the screen.
            canvas.drawText(
                "($mLocationString)",
                (visibleArea.right - horizontalTextMargin).toFloat(),
                (visibleArea.top + verticalTextMarginFromTop).toFloat(),
                mRightInsetPaint
            )
        } else {
            Log.d(TAG, "Visible area not available.")
        }

        if (mStableArea != null) {
            // Draw a cross-hairs at the stable center.
            val lengthPx = 15
            val centerX = mStableArea!!.centerX()
            val centerY = mStableArea!!.centerY()
            canvas.drawLine(
                (centerX - lengthPx).toFloat(),
                centerY.toFloat(),
                (centerX + lengthPx).toFloat(),
                centerY.toFloat(),
                mCenterPaint
            )
            canvas.drawLine(
                centerX.toFloat(),
                (centerY - lengthPx).toFloat(),
                centerX.toFloat(),
                (centerY + lengthPx).toFloat(),
                mCenterPaint
            )
            canvas.drawText(
                "($centerX, $centerY)",
                (centerX + horizontalTextMargin).toFloat(),
                centerY.toFloat(),
                mCenterPaint
            )
        } else {
            Log.d(TAG, "Stable area not available.")
        }

        if (mShowMarkers) {
            // Show a set number of markers centered around the midpoint of the stable area. If no
            // stable area, then use visible area or canvas dimensions. If an active marker is set
            // draw
            // a line from the center to that marker.
            val markerArea =
                if (mStableArea != null
                ) mStableArea!!
                else ((if (mVisibleArea != null
                ) mVisibleArea
                else Rect(0, 0, canvas.width - 1, canvas.height))!!)
            val centerX = markerArea.centerX()
            val centerY = markerArea.centerY()
            val radius = min((centerX / 2).toDouble(), (centerY / 2).toDouble())

            val circleAngle = 2.0 * Math.PI
            val markerpiece = circleAngle / mNumMarkers
            for (i in 0 until mNumMarkers) {
                val markerX = centerX + (radius * cos(markerpiece * i)).toInt()
                val markerY = centerY + (radius * sin(markerpiece * i)).toInt()
                canvas.drawCircle(markerX.toFloat(), markerY.toFloat(), 5f, mMarkerPaint)
                if (i == mActiveMarker) {
                    canvas.drawLine(
                        centerX.toFloat(),
                        centerY.toFloat(),
                        markerX.toFloat(),
                        markerY.toFloat(),
                        mMarkerPaint
                    )
                }
            }
        }

        mSurface!!.unlockCanvasAndPost(canvas)
    }

    companion object {
        private const val TAG = "SurfaceRenderer"

        /** The maximum scale factor of the background map.  */
        private const val MAX_SCALE_FACTOR = 5f

        /** The minimum scale factor of the background map.  */
        private const val MIN_SCALE_FACTOR = 0.5f

        /** The scale factor to apply when initializing the background map.  */
        private const val MAP_ENLARGE_FACTOR = 5f

        /** Looper msg to trigger a frame rendering  */
        private const val MSG_RENDER_FRAME = 1
    }
}
