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
import android.graphics.Rect

/** A renderer for use on templates with a surface.  */
interface Renderer {
    /**
     * Informs the renderer that it will receive [.renderFrame] calls.
     *
     * @param onChangeListener a runnable that will initiate a render pass in the controller
     */
    fun enable(onChangeListener: Runnable)

    /** Informs the renderer that it will no longer receive [.renderFrame] calls.  */
    fun disable()

    /** Request that a frame should be drawn to the supplied canvas.  */
    fun renderFrame(canvas: Canvas, visibleArea: Rect?, stableArea: Rect?)
}
