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
package androidx.car.app.sample.showcase.common.templates

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * An activity for use by the car app library to perform actions such as requesting permissions.
 */
class SignInWithGoogleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //        OnSignInComplete signInCompleteCallback =
//                (OnSignInComplete) getIntent().getExtras().getBinder(BINDER_KEY);
//
//        ActivityResultLauncher<Intent> activityResultLauncher =
//                registerForActivityResult(
//                        new ActivityResultContracts.StartActivityForResult(),
//                        result -> {
//                            GoogleSignInAccount account =
//                                    GoogleSignIn.getSignedInAccountFromIntent(
//                                            result.getData()).getResult();
//                            signInCompleteCallback.onSignInComplete(account);
//                            finish();
//                        });
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            signInCompleteCallback.onSignInComplete(account);
//            finish();
//        }
//
//        GoogleSignInOptions gso =
//                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestEmail()
//                        .requestProfile()
//                        .build();
//        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
//        activityResultLauncher.launch(signInClient.getSignInIntent());
    } //    /**
    //     * Binder callback to provide to the sign in activity.
    //     */
    //    abstract static class OnSignInComplete extends Binder implements IBinder {
    //        /**
    //         * Notifies that sign in flow completed.
    //         *
    //         * @param account the account signed in or {@code null} if there were issues signing in.
    //         */
    //        public abstract void onSignInComplete(@Nullable GoogleSignInAccount account);
    //    }


    companion object {
        const val BINDER_KEY: String = "binder"
    }
}
