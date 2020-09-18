/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.google.android.libraries.car.app.samples.navigation.model;

import android.os.Handler;
import java.util.List;

/** Represents an instruction sequence and parameters for a executing a script. */
public class Script {

  private final Handler mHandler = new Handler();
  private final List<Instruction> mInstructions;
  private final Runnable mRunnable;
  private int mCurrentInstruction;

  public interface Runnable {
    void run(Instruction instruction);
  }

  public static Script execute(List<Instruction> instructions, Runnable callback) {
    return new Script(instructions, callback);
  }

  public void stop() {
    mHandler.removeCallbacksAndMessages(null);
    mCurrentInstruction = mInstructions.size();
  }

  private Script(List<Instruction> instructions, Runnable runnable) {
    mInstructions = instructions;
    mRunnable = runnable;
    mCurrentInstruction = 0;
    mHandler.post(this::nextInstruction);
  }

  private void nextInstruction() {
    if (mCurrentInstruction >= mInstructions.size()) {
      // Script is finished.
      return;
    }
    Instruction instruction = mInstructions.get(mCurrentInstruction);
    mRunnable.run(instruction);
    mCurrentInstruction++;
    mHandler.postDelayed(this::nextInstruction, instruction.getDurationMillis());
  }
}
