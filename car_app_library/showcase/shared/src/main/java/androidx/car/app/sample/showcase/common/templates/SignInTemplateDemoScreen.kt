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

import android.graphics.Color
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.InputCallback
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import androidx.car.app.model.signin.InputSignInMethod
import androidx.car.app.model.signin.PinSignInMethod
import androidx.car.app.model.signin.ProviderSignInMethod
import androidx.car.app.model.signin.QRCodeSignInMethod
import androidx.car.app.model.signin.SignInTemplate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.common.Utils
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat

/** A screen that demonstrates the sign-in template.  */
class SignInTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    private val mAdditionalText: CharSequence
    private val mProviderSignInAction: Action
    private val mPinSignInAction: Action
    private val mQRCodeSignInAction: Action

    // package private to avoid synthetic accessor
    var mState: State = State.USERNAME
    var mLastErrorMessage: String = "" // last displayed error message
    var mErrorMessage: String? = ""
    var mUsername: String? = null

    init {
        // Handle back pressed events manually, as we use them to navigate between templates within
        // the same screen.
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mErrorMessage = ""
                if (mState == State.USERNAME || mState == State.SIGNED_IN) {
                    screenManager.pop()
                } else {
                    mState = State.USERNAME
                    invalidate()
                }
            }
        }
        carContext.onBackPressedDispatcher.addCallback(this, callback)

        mAdditionalText = Utils.clickable(
            getCarContext().getString(R.string.additional_text), 18,
            16
        ) { screenManager.push(LongMessageTemplateDemoScreen(getCarContext())) }

        mProviderSignInAction = Action.Builder()
            .setTitle(getCarContext().getString(R.string.google_sign_in))
            .setOnClickListener(ParkedOnlyOnClickListener.create {
                mState = State.PROVIDER
                invalidate()
            })
            .build()

        mPinSignInAction = Action.Builder()
            .setTitle(getCarContext().getString(R.string.use_pin))
            .setOnClickListener(ParkedOnlyOnClickListener.create {
                mState = State.PIN
                invalidate()
            })
            .build()

        mQRCodeSignInAction = Action.Builder()
            .setTitle(getCarContext().getString(R.string.qr_code))
            .setOnClickListener(ParkedOnlyOnClickListener.create {
                mState = State.QR_CODE
                invalidate()
            })
            .build()
    }

    override fun onGetTemplate(): Template {
        if (carContext.carAppApiLevel < CarAppApiLevels.LEVEL_2) {
            return MessageTemplate.Builder(
                carContext.getString(R.string.sign_in_template_not_supported_text)
            )
                .setTitle(
                    carContext.getString(
                        R.string.sign_in_template_not_supported_title
                    )
                )
                .setHeaderAction(Action.BACK)
                .build()
        }
        return when (mState) {
            State.USERNAME -> usernameSignInTemplate
            State.PASSWORD -> passwordSignInTemplate
            State.PIN -> pinSignInTemplate
            State.PROVIDER -> providerSignInTemplate
            State.QR_CODE -> qRCodeSignInTemplate
            State.SIGNED_IN -> signInCompletedMessageTemplate
        }
//        throw IllegalStateException("Invalid state: $mState")
    }

    private val usernameSignInTemplate: Template
        get() {
            val listener: InputCallback = object : InputCallback {
                override fun onInputSubmitted(text: String) {
                    if (mState == State.USERNAME) {
                        mUsername = text
                        submitUsername()
                    }
                }

                override fun onInputTextChanged(text: String) {
                    // This callback demonstrates how to use handle the text changed event.
                    // In this case, we check that the user name doesn't exceed a certain length.
                    if (mState == State.USERNAME) {
                        mUsername = text
                        mErrorMessage = validateUsername()

                        // Invalidate the template (and hence possibly update the error message) only
                        // if clearing up the error string, or if the error is changing.
                        if (!mLastErrorMessage.isEmpty()
                            && (mErrorMessage!!.isEmpty()
                                    || mLastErrorMessage != mErrorMessage)
                        ) {
                            invalidate()
                        }
                    }
                }
            }

            val builder = InputSignInMethod.Builder(listener)
                .setHint(carContext.getString(R.string.email_hint))
                .setKeyboardType(InputSignInMethod.KEYBOARD_EMAIL)
            if (mErrorMessage != null) {
                builder.setErrorMessage(mErrorMessage!!)
                mLastErrorMessage = mErrorMessage!!
            }
            if (mUsername != null) {
                builder.setDefaultValue(mUsername!!)
            }
            val signInMethod = builder.build()

            return SignInTemplate.Builder(signInMethod)
                .addAction(mProviderSignInAction)
                .addAction(
                    if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_3
                    ) mQRCodeSignInAction else mPinSignInAction
                )
                .setTitle(carContext.getString(R.string.sign_in_title))
                .setInstructions(carContext.getString(R.string.sign_in_instructions))
                .setHeaderAction(Action.BACK)
                .setAdditionalText(mAdditionalText)
                .build()
        }

    /**
     * Validates the currently entered user name and returns an error message string if invalid,
     * or an empty string otherwise.
     */
    fun validateUsername(): String {
        return if (mUsername == null || mUsername!!.length < MIN_USERNAME_LENGTH) {
            carContext.getString(
                R.string.invalid_length_error_msg,
                MIN_USERNAME_LENGTH.toString()
            )
        } else if (!mUsername!!.matches(EMAIL_REGEXP.toRegex())) {
            carContext.getString(R.string.invalid_email_error_msg)
        } else {
            ""
        }
    }

    /**
     * Moves to the password screen if the user name currently entered is valid, or displays
     * an error message otherwise.
     */
    fun submitUsername() {
        mErrorMessage = validateUsername()

        val isError = !mErrorMessage!!.isEmpty()
        if (!isError) {
            // If there's no error, go to the password screen.
            mState = State.PASSWORD
        }

        // Invalidate the template so that we either display an error, or go to the password screen.
        invalidate()
    }

    private val passwordSignInTemplate: Template
        get() {
            val callback: InputCallback = object : InputCallback {
                override fun onInputSubmitted(text: String) {
                    // Mocked password validation
                    if (EXPECTED_PASSWORD != text) {
                        mErrorMessage = carContext.getString(R.string.invalid_password_error_msg)
                    } else {
                        mErrorMessage = ""
                        mState = State.SIGNED_IN
                    }
                    invalidate()
                }
            }
            val builder = InputSignInMethod.Builder(callback)
                .setHint(carContext.getString(R.string.password_hint))
                .setInputType(InputSignInMethod.INPUT_TYPE_PASSWORD)
            if (mErrorMessage != null) {
                builder.setErrorMessage(mErrorMessage!!)
            }
            val signInMethod = builder.build()

            return SignInTemplate.Builder(signInMethod)
                .addAction(mProviderSignInAction)
                .addAction(
                    if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_3
                    ) mQRCodeSignInAction else mPinSignInAction
                )
                .setTitle(carContext.getString(R.string.sign_in_title))
                .setInstructions(
                    carContext.getString(R.string.password_sign_in_instruction_prefix)
                            + ": " + mUsername
                )
                .setHeaderAction(Action.BACK)
                .setAdditionalText(mAdditionalText)
                .build()
        }

    private val pinSignInTemplate: Template
        get() {
            val pinSignInMethod = PinSignInMethod("123456789ABC")
            return SignInTemplate.Builder(pinSignInMethod)
                .setTitle(carContext.getString(R.string.sign_in_title))
                .setInstructions(carContext.getString(R.string.pin_sign_in_instruction))
                .setHeaderAction(Action.BACK)
                .setAdditionalText(mAdditionalText)
                .build()
        }

    private val qRCodeSignInTemplate: Template
        get() {
            val qrCodeSignInMethod = QRCodeSignInMethod(
                Uri.parse(
                    "https://www"
                            + ".youtube.com/watch?v=dQw4w9WgXcQ"
                )
            )
            return SignInTemplate.Builder(qrCodeSignInMethod)
                .setTitle(carContext.getString(R.string.qr_code_sign_in_title))
                .setHeaderAction(Action.BACK)
                .setAdditionalText(mAdditionalText)
                .addAction(mPinSignInAction)
                .addAction(mProviderSignInAction)
                .build()
        }

    private val providerSignInTemplate: Template
        get() {
            val providerIcon = IconCompat.createWithResource(
                carContext,
                R.drawable.ic_googleg
            )
            val noTint = CarColor.createCustom(Color.TRANSPARENT, Color.TRANSPARENT)

            val providerSignInMethod = ProviderSignInMethod(
                Action.Builder()
                    .setTitle(
                        Utils.colorize(
                            carContext.getString(R.string.sign_in_with_google_title),
                            CarColor.createCustom(Color.BLACK, Color.BLACK), 0, 19
                        )
                    )
                    .setBackgroundColor(CarColor.createCustom(Color.WHITE, Color.WHITE))
                    .setIcon(
                        CarIcon.Builder(providerIcon)
                            .setTint(noTint)
                            .build()
                    )
                    .setOnClickListener(ParkedOnlyOnClickListener.create { this.performSignInWithGoogleFlow() })
                    .build()
            )

            return SignInTemplate.Builder(providerSignInMethod)
                .setTitle(carContext.getString(R.string.sign_in_title))
                .setInstructions(carContext.getString(R.string.provider_sign_in_instruction))
                .setHeaderAction(Action.BACK)
                .setAdditionalText(mAdditionalText)
                .build()
        }

    private fun performSignInWithGoogleFlow() {
        // This is here for demonstration purposes, if the APK is not signed with a signature
        // that has been registered for sign in with Google flow, the sign in will fail at runtime.

//        Bundle extras = new Bundle(1);
//        extras.putBinder(BINDER_KEY, new SignInWithGoogleActivity.OnSignInComplete() {
//            @Override
//            public void onSignInComplete(@Nullable GoogleSignInAccount account) {
//                if (account == null) {
//                    CarToast.makeText(getCarContext(), "Error signing in", LENGTH_LONG).show();
//                    return;
//                }
//
//                // Use the account
//                CarToast.makeText(getCarContext(),
//                        account.getGivenName() + " signed in", LENGTH_LONG).show();
//            }
//        });
//        getCarContext().startActivity(
//                new Intent()
//                        .setClass(getCarContext(), SignInWithGoogleActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .putExtras(extras));

        CarToast.makeText(
            carContext,
            carContext.getString(R.string.sign_in_with_google_toast_msg),
            CarToast.LENGTH_LONG
        )
            .show()
    }

    private val signInCompletedMessageTemplate: MessageTemplate
        get() = MessageTemplate.Builder(
            carContext.getString(R.string.sign_in_complete_text)
        )
            .setTitle(carContext.getString(R.string.sign_in_complete_title))
            .setHeaderAction(Action.BACK)
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.sign_out_action_title))
                    .setOnClickListener {
                        mState = State.USERNAME
                        invalidate()
                    }
                    .build())
            .build()

    enum class State {
        USERNAME,
        PASSWORD,
        PIN,
        PROVIDER,
        QR_CODE,
        SIGNED_IN,
    }

    companion object {
        private const val EMAIL_REGEXP = "^(.+)@(.+)$"
        private const val EXPECTED_PASSWORD = "password"
        private const val MIN_USERNAME_LENGTH = 5
    }
}
