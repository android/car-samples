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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log

/** Simple renderer for the surface templates.  */
class DefaultRenderer : Renderer {
    private val mLeftInsetPaint = Paint()
    private val mRightInsetPaint = Paint()
    private val mCenterPaint = Paint()

    init {
        mLeftInsetPaint.color = Color.RED
        mLeftInsetPaint.isAntiAlias = true
        mLeftInsetPaint.style = Paint.Style.STROKE

        mRightInsetPaint.color = Color.RED
        mRightInsetPaint.isAntiAlias = true
        mRightInsetPaint.style = Paint.Style.STROKE
        mRightInsetPaint.textAlign = Paint.Align.RIGHT

        mCenterPaint.color = Color.BLUE
        mCenterPaint.isAntiAlias = true
        mCenterPaint.style = Paint.Style.STROKE
    }

    override fun enable(onChangeListener: Runnable) {
        // Don't need to do anything here since renderFrame doesn't require any setup.
    }

    override fun disable() {
        // Don't need to do anything here since renderFrame doesn't require any setup.
    }

    override fun renderFrame(
        canvas: Canvas, visibleArea: Rect?,
        stableArea: Rect?
    ) {
        // Draw a rectangle showing the inset.

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
                (visibleArea.left + HORIZONTAL_TEXT_MARGIN).toFloat(),
                (visibleArea.top + VERTICAL_TEXT_MARGIN_FROM_TOP).toFloat(),
                mLeftInsetPaint
            )
            canvas.drawText(
                "(" + visibleArea.right + " , " + visibleArea.bottom + ")",
                (visibleArea.right - HORIZONTAL_TEXT_MARGIN).toFloat(),
                (visibleArea.bottom - VERTICAL_TEXT_MARGIN_FROM_BOTTOM).toFloat(),
                mRightInsetPaint
            )
        } else {
            Log.d(TAG, "Visible area not available.")
        }

        if (stableArea != null) {
            // Draw a cross-hairs at the stable center.
            val lengthPx = 15
            val centerX = stableArea.centerX()
            val centerY = stableArea.centerY()
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
                (centerX + HORIZONTAL_TEXT_MARGIN).toFloat(),
                centerY.toFloat(),
                mCenterPaint
            )
        } else {
            Log.d(TAG, "Stable area not available.")
        }
    }

    companion object {
        private const val TAG = "showcase"

        private const val HORIZONTAL_TEXT_MARGIN = 10
        private const val VERTICAL_TEXT_MARGIN_FROM_TOP = 20
        private const val VERTICAL_TEXT_MARGIN_FROM_BOTTOM = 10
    }
}
