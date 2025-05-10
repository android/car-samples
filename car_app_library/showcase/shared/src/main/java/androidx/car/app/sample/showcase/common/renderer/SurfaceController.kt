/*
 * Copyright 2021 The Android Open Source Project
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
package androidx.car.app.sample.showcase.common.renderer

import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.Surface
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/** A very simple implementation of a renderer for the app's background surface.  */
class SurfaceController(private val mCarContext: CarContext, lifecycle: Lifecycle) :
    DefaultLifecycleObserver {
    private val mDefaultRenderer = DefaultRenderer()
    private var mOverrideRenderer: Renderer? = null

    var mSurface: Surface? = null
    var mVisibleArea: Rect? = null
    var mStableArea: Rect? = null
    private val mSurfaceCallback: SurfaceCallback = object : SurfaceCallback {
        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            synchronized(this@SurfaceController) {
                Log.i(TAG, "Surface available $surfaceContainer")
                mSurface = surfaceContainer.surface
                renderFrame()
            }
        }

        override fun onVisibleAreaChanged(visibleArea: Rect) {
            synchronized(this@SurfaceController) {
                Log.i(
                    TAG, "Visible area changed " + mSurface + ". stableArea: "
                            + mStableArea + " visibleArea:" + visibleArea
                )
                mVisibleArea = visibleArea
                renderFrame()
            }
        }

        override fun onStableAreaChanged(stableArea: Rect) {
            synchronized(this@SurfaceController) {
                Log.i(
                    TAG, "Stable area changed " + mSurface + ". stableArea: "
                            + mStableArea + " visibleArea:" + mVisibleArea
                )
                mStableArea = stableArea
                renderFrame()
            }
        }

        override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
            synchronized(this@SurfaceController) {
                mSurface = null
            }
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    /** Callback called when the car configuration changes.  */
    fun onCarConfigurationChanged() {
        renderFrame()
    }

    /** Tells the controller whether to override the default renderer.  */
    fun overrideRenderer(renderer: Renderer?) {
        if (mOverrideRenderer === renderer) {
            return
        }

        if (mOverrideRenderer != null) {
            mOverrideRenderer!!.disable()
        } else {
            mDefaultRenderer.disable()
        }

        mOverrideRenderer = renderer

        if (mOverrideRenderer != null) {
            mOverrideRenderer!!.enable { this.renderFrame() }
        } else {
            mDefaultRenderer.enable { this.renderFrame() }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.i(TAG, "SurfaceController created")
        mCarContext.getCarService(AppManager::class.java).setSurfaceCallback(mSurfaceCallback)
    }

    fun renderFrame() {
        if (mSurface == null || !mSurface!!.isValid) {
            // Surface is not available, or has been destroyed, skip this frame.
            return
        }
        val canvas = mSurface!!.lockCanvas(null)

        // Clear the background.
        canvas.drawColor(if (mCarContext.isDarkMode) Color.DKGRAY else Color.LTGRAY)

        if (mOverrideRenderer != null) {
            mOverrideRenderer!!.renderFrame(canvas, mVisibleArea, mStableArea)
        } else {
            mDefaultRenderer.renderFrame(canvas, mVisibleArea, mStableArea)
        }
        mSurface!!.unlockCanvasAndPost(canvas)
    }

    companion object {
        private const val TAG = "showcase"
    }
}
