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
package androidx.car.app.sample.navigation.common.model

import android.os.Handler
import android.os.Looper

/** Represents an instruction sequence and parameters for a executing a script.  */
class Script private constructor(
    private val mInstructions: List<Instruction>,
    private val mProcessor: Processor
) {
    private val mHandler = Handler(Looper.getMainLooper())
    private var mCurrentInstruction = 0

    /** An interface for a block of code that processes an instruction.  */
    interface Processor {
        /** A block of code that processes an instruction.  */
        fun process(instruction: Instruction, nextInstruction: Instruction?)
    }

    /** Stops executing the instructions.  */
    fun stop() {
        mHandler.removeCallbacksAndMessages(null)
        mCurrentInstruction = mInstructions.size
    }

    init {
        // Execute the first instruction right away to start navigation and avoid flicker.
        nextInstruction()
    }

    private fun nextInstruction() {
        if (mCurrentInstruction >= mInstructions.size) {
            // Script is finished.
            return
        }
        val instruction = mInstructions[mCurrentInstruction]
        var nextInstruction: Instruction? = null
        val nextPosition = mCurrentInstruction + 1
        if (nextPosition < mInstructions.size) {
            nextInstruction = mInstructions[nextPosition]
        }
        mProcessor.process(instruction, nextInstruction)
        mCurrentInstruction++
        mHandler.postDelayed({ this.nextInstruction() }, instruction.durationMillis)
    }

    companion object {
        /** Executes the given list of instructions.  */
        fun execute(
            instructions: List<Instruction>,
            processor: Processor
        ): Script {
            return Script(instructions, processor)
        }
    }
}
