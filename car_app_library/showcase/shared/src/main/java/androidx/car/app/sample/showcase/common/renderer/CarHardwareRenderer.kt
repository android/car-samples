
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

import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.CarValue
import androidx.car.app.hardware.common.OnCarDataAvailableListener
import androidx.car.app.hardware.info.Accelerometer
import androidx.car.app.hardware.info.CarHardwareLocation
import androidx.car.app.hardware.info.CarSensors
import androidx.car.app.hardware.info.Compass
import androidx.car.app.hardware.info.EnergyLevel
import androidx.car.app.hardware.info.EvStatus
import androidx.car.app.hardware.info.Gyroscope
import androidx.car.app.hardware.info.Mileage
import androidx.car.app.hardware.info.Speed
import androidx.car.app.hardware.info.TollCard
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import kotlin.math.min

/**
 * Renderer which aggregates information about the car hardware to be drawn on a surface.
 */
@ExperimentalCarApi
class CarHardwareRenderer(private val mCarContext: CarContext) : Renderer {
    private val mCarHardwareExecutor: Executor
    private val mCarInfoPaint = Paint()

    var mTollCard: TollCard? = null
    var mEnergyLevel: EnergyLevel? = null
    var mSpeed: Speed? = null
    var mMileage: Mileage? = null
    var mEvStatus: EvStatus? = null
    var mAccelerometer: Accelerometer? = null
    var mGyroscope: Gyroscope? = null
    var mCompass: Compass? = null
    var mCarHardwareLocation: CarHardwareLocation? = null
    private var mRequestRenderRunnable: Runnable? = null
    private val mTollListener = OnCarDataAvailableListener { data: TollCard ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received toll information:$data")
            mTollCard = data
            requestRenderFrame()
        }
    }
    private val mEnergyLevelListener = OnCarDataAvailableListener { data: EnergyLevel ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received energy level information: $data")
            mEnergyLevel = data
            requestRenderFrame()
        }
    }
    private val mSpeedListener = OnCarDataAvailableListener { data: Speed ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received speed information: $data")
            mSpeed = data
            requestRenderFrame()
        }
    }
    private val mMileageListener = OnCarDataAvailableListener { data: Mileage ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received mileage: $data")
            mMileage = data
            requestRenderFrame()
        }
    }
    private val mAccelerometerListener = OnCarDataAvailableListener { data: Accelerometer ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received accelerometer: $data")
            mAccelerometer = data
            requestRenderFrame()
        }
    }
    private val mGyroscopeListener = OnCarDataAvailableListener { data: Gyroscope ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received gyroscope: $data")
            mGyroscope = data
            requestRenderFrame()
        }
    }
    private val mCompassListener = OnCarDataAvailableListener { data: Compass ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received compass: $data")
            mCompass = data
            requestRenderFrame()
        }
    }
    private val mCarLocationListener = OnCarDataAvailableListener { data: CarHardwareLocation ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received car location: $data")
            mCarHardwareLocation = data
            requestRenderFrame()
        }
    }
    private val mEvStatusListener = OnCarDataAvailableListener { data: EvStatus ->
        synchronized(
            this
        ) {
            Log.i(TAG, "Received car EV status: $data")
            mEvStatus = data
            requestRenderFrame()
        }
    }
    private var mHasTollCardPermission = false
    private var mHasEnergyLevelPermission = false
    private var mHasSpeedPermission = false
    private var mHasMileagePermission = false
    private var mHasEvStatusPermission = false
    private var mHasAccelerometerPermission = false
    private var mHasGyroscopePermission = false
    private var mHasCompassPermission = false
    private var mHasCarHardwareLocationPermission = false

    init {
        mCarInfoPaint.color = Color.BLACK
        mCarInfoPaint.isAntiAlias = true
        mCarInfoPaint.style = Paint.Style.STROKE
        mCarHardwareExecutor = ContextCompat.getMainExecutor(mCarContext)
    }

    // TODO(b/216177515): Remove this annotation once EvStatus is ready.
    @OptIn(ExperimentalCarApi::class) override fun enable(onChangeListener: Runnable) {
        mRequestRenderRunnable = onChangeListener
        val carHardwareManager =
            mCarContext.getCarService(CarHardwareManager::class.java)
        val carInfo = carHardwareManager.carInfo
        val carSensors = carHardwareManager.carSensors

        // Request car info subscription items.
        mTollCard = null
        try {
            carInfo.addTollListener(mCarHardwareExecutor, mTollListener)
            mHasTollCardPermission = true
        } catch (e: SecurityException) {
            mHasTollCardPermission = false
        }

        mEnergyLevel = null
        try {
            carInfo.addEnergyLevelListener(mCarHardwareExecutor, mEnergyLevelListener)
            mHasEnergyLevelPermission = true
        } catch (e: SecurityException) {
            mHasEnergyLevelPermission = false
        }

        mSpeed = null
        try {
            carInfo.addSpeedListener(mCarHardwareExecutor, mSpeedListener)
            mHasSpeedPermission = true
        } catch (e: SecurityException) {
            mHasSpeedPermission = false
        }

        mMileage = null
        try {
            carInfo.addMileageListener(mCarHardwareExecutor, mMileageListener)
            mHasMileagePermission = true
        } catch (e: SecurityException) {
            mHasMileagePermission = false
        }

        if (mCarContext.packageManager.hasSystemFeature(
                PackageManager.FEATURE_AUTOMOTIVE
            )
        ) {
            mEvStatus = null
            try {
                carInfo.addEvStatusListener(mCarHardwareExecutor, mEvStatusListener)
                mHasEvStatusPermission = true
            } catch (e: SecurityException) {
                mHasEvStatusPermission = false
            }
        }


        // Request sensors
        mCompass = null
        try {
            carSensors.addCompassListener(
                CarSensors.UPDATE_RATE_NORMAL, mCarHardwareExecutor,
                mCompassListener
            )
            mHasCompassPermission = true
        } catch (e: SecurityException) {
            mHasCompassPermission = false
        }

        mGyroscope = null
        try {
            carSensors.addGyroscopeListener(
                CarSensors.UPDATE_RATE_NORMAL, mCarHardwareExecutor,
                mGyroscopeListener
            )
            mHasGyroscopePermission = true
        } catch (e: SecurityException) {
            mHasGyroscopePermission = false
        }

        mAccelerometer = null
        try {
            carSensors.addAccelerometerListener(
                CarSensors.UPDATE_RATE_NORMAL,
                mCarHardwareExecutor,
                mAccelerometerListener
            )
            mHasAccelerometerPermission = true
        } catch (e: SecurityException) {
            mHasAccelerometerPermission = false
        }

        mCarHardwareLocation = null
        try {
            carSensors.addCarHardwareLocationListener(
                CarSensors.UPDATE_RATE_NORMAL,
                mCarHardwareExecutor, mCarLocationListener
            )
            mHasCarHardwareLocationPermission = true
        } catch (e: SecurityException) {
            mHasCarHardwareLocationPermission = false
        }
    }

    @OptIn(ExperimentalCarApi::class) override fun disable() {
        mRequestRenderRunnable = null
        val carHardwareManager =
            mCarContext.getCarService(CarHardwareManager::class.java)
        val carInfo = carHardwareManager.carInfo
        val carSensors = carHardwareManager.carSensors

        try {
            // Unsubscribe carinfo
            carInfo.removeTollListener(mTollListener)
            mHasTollCardPermission = true
        } catch (e: SecurityException) {
            mHasTollCardPermission = false
        }
        mTollCard = null

        try {
            carInfo.removeEnergyLevelListener(mEnergyLevelListener)
            mHasEnergyLevelPermission = true
        } catch (e: SecurityException) {
            mHasEnergyLevelPermission = false
        }
        mEnergyLevel = null

        try {
            carInfo.removeSpeedListener(mSpeedListener)
            mHasSpeedPermission = true
        } catch (e: SecurityException) {
            mHasSpeedPermission = false
        }
        mSpeed = null

        try {
            carInfo.removeMileageListener(mMileageListener)
            mHasMileagePermission = true
        } catch (e: SecurityException) {
            mHasMileagePermission = false
        }
        mMileage = null

        try {
            // Unsubscribe sensors
            carSensors.removeCompassListener(mCompassListener)
            mHasCompassPermission = true
        } catch (e: SecurityException) {
            mHasCompassPermission = false
        }
        mCompass = null

        if (mCarContext.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            try {
                carInfo.removeEvStatusListener(mEvStatusListener)
                mHasEvStatusPermission = true
            } catch (e: SecurityException) {
                mHasEvStatusPermission = false
            }
            mEvStatus = null
        }

        try {
            carSensors.removeGyroscopeListener(mGyroscopeListener)
            mHasGyroscopePermission = true
        } catch (e: SecurityException) {
            mHasGyroscopePermission = false
        }
        mGyroscope = null

        try {
            carSensors.removeAccelerometerListener(mAccelerometerListener)
            mHasAccelerometerPermission = true
        } catch (e: SecurityException) {
            mHasAccelerometerPermission = false
        }
        mAccelerometer = null

        try {
            carSensors.removeCarHardwareLocationListener(mCarLocationListener)
            mHasCarHardwareLocationPermission = true
        } catch (e: SecurityException) {
            mHasCarHardwareLocationPermission = false
        }
        mCarHardwareLocation = null
    }

    @OptIn(ExperimentalCarApi::class) override fun renderFrame(
        canvas: Canvas, visibleArea: Rect?,
        stableArea: Rect?
    ) {
        if (stableArea != null) {
            if (stableArea.isEmpty) {
                // No inset set. The entire area is considered safe to draw.
                stableArea[0, 0, canvas.width - 1] = canvas.height - 1
            }

            val height = min(
                (stableArea.height() / 8).toDouble(),
                MAX_FONT_SIZE.toDouble()
            ).toInt()
            val updatedSize = height - ROW_SPACING
            mCarInfoPaint.textSize = updatedSize.toFloat()

            canvas.drawRect(stableArea, mCarInfoPaint)

            val fm = mCarInfoPaint.fontMetrics
            var verticalPos = stableArea.top - fm.ascent

            // Prepare text for Toll card status
            var info = StringBuilder()
            if (!mHasTollCardPermission) {
                info.append(mCarContext.getString(R.string.no_toll_card_permission))
            } else if (mTollCard == null) {
                info.append(mCarContext.getString(R.string.fetch_toll_info))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.toll_card_state),
                        mTollCard!!.cardState, ". "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
            verticalPos += height.toFloat()

            // Prepare text for Energy Level
            info = StringBuilder()
            if (!mHasEnergyLevelPermission) {
                info.append(mCarContext.getString(R.string.no_energy_level_permission))
            } else if (mEnergyLevel == null) {
                info.append(mCarContext.getString(R.string.fetch_energy_level))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.low_energy),
                        mEnergyLevel!!.energyIsLow, ". "
                    )
                )
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.range),
                        mEnergyLevel!!.rangeRemainingMeters,
                        " m. "
                    )
                )
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.fuel),
                        mEnergyLevel!!.fuelPercent, " %. "
                    )
                )
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.battery),
                        mEnergyLevel!!.batteryPercent, " %. "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
            verticalPos += height.toFloat()

            // Prepare text for Speed
            info = StringBuilder()
            if (!mHasSpeedPermission) {
                info.append(mCarContext.getString(R.string.no_speed_permission))
            } else if (mSpeed == null) {
                info.append(mCarContext.getString(R.string.fetch_speed))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.display_speed),
                        mSpeed!!.displaySpeedMetersPerSecond, " m/s. "
                    )
                )
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.raw_speed),
                        mSpeed!!.rawSpeedMetersPerSecond,
                        " m/s. "
                    )
                )
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.unit),
                        mSpeed!!.speedDisplayUnit, ". "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
            verticalPos += height.toFloat()

            // Prepare text for Odometer, skip for AAOS
            if (!mCarContext.packageManager.hasSystemFeature(
                    PackageManager.FEATURE_AUTOMOTIVE
                )
            ) {
                info = StringBuilder()
                if (!mHasMileagePermission) {
                    info.append(mCarContext.getString(R.string.no_mileage_permission))
                } else if (mMileage == null) {
                    info.append(mCarContext.getString(R.string.no_mileage_permission))
                } else {
                    info.append(
                        generateCarValueText(
                            mCarContext.getString(R.string.odometer),
                            mMileage!!.odometerMeters, " m. "
                        )
                    )
                    info.append(
                        generateCarValueText(
                            mCarContext.getString(R.string.unit),
                            mMileage!!.distanceDisplayUnit, ". "
                        )
                    )
                }
                canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
                verticalPos += height.toFloat()
            }

            if (mCarContext.carAppApiLevel >= CarAppApiLevels.LEVEL_4
                && mCarContext.packageManager.hasSystemFeature(
                    PackageManager.FEATURE_AUTOMOTIVE
                )
            ) {
                // Prepare text for EV status
                info = StringBuilder()
                if (!mHasEvStatusPermission) {
                    info.append(mCarContext.getString(R.string.no_ev_status_permission))
                } else if (mEvStatus == null) {
                    info.append(mCarContext.getString(R.string.fetch_ev_status))
                } else {
                    info.append(
                        generateCarValueText(
                            mCarContext.getString(R.string.ev_connected),
                            mEvStatus!!.evChargePortConnected, ". "
                        )
                    )
                    info.append(
                        generateCarValueText(
                            mCarContext.getString(R.string.ev_open),
                            mEvStatus!!.evChargePortOpen, ". "
                        )
                    )
                }
                canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
                verticalPos += height.toFloat()
            }

            // Prepare text for Accelerometer
            info = StringBuilder()
            if (!mHasAccelerometerPermission) {
                info.append(mCarContext.getString(R.string.no_accelerometer_permission))
            } else if (mAccelerometer == null) {
                info.append(mCarContext.getString(R.string.fetch_accelerometer))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.accelerometer),
                        mAccelerometer!!.forces, ". "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
            verticalPos += height.toFloat()

            // Prepare text for Gyroscope
            info = StringBuilder()
            if (!mHasGyroscopePermission) {
                info.append(mCarContext.getString(R.string.no_gyroscope_permission))
            } else if (mGyroscope == null) {
                info.append(mCarContext.getString(R.string.fetch_gyroscope))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.gyroscope),
                        mGyroscope!!.rotations, ". "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
            verticalPos += height.toFloat()

            // Prepare text for Compass
            info = StringBuilder()
            if (!mHasCompassPermission) {
                info.append(mCarContext.getString(R.string.no_compass_permission))
            } else if (mCompass == null) {
                info.append(mCarContext.getString(R.string.fetch_compass))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.compass),
                        mCompass!!.orientations, ". "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
            verticalPos += height.toFloat()

            // Prepare text for Location
            info = StringBuilder()
            if (!mHasCarHardwareLocationPermission) {
                info.append(mCarContext.getString(R.string.no_car_hardware_location))
            } else if (mCarHardwareLocation == null) {
                info.append(mCarContext.getString(R.string.fetch_location))
            } else {
                info.append(
                    generateCarValueText(
                        mCarContext.getString(R.string.car_hardware_location),
                        mCarHardwareLocation!!.location, ". "
                    )
                )
            }
            canvas.drawText(info.toString(), LEFT_MARGIN.toFloat(), verticalPos, mCarInfoPaint)
        }
    }

    private fun generateCarValueText(item: String, carValue: CarValue<*>, ending: String): String {
        val stringBuilder = StringBuilder(item)
        if (carValue.status != CarValue.STATUS_SUCCESS) {
            stringBuilder.append(" N/A. ")
        } else {
            stringBuilder.append(": ")
            if (carValue.value is List<*>) {
                val list = carValue.value as List<*>?
                appendList(stringBuilder, list)
            } else {
                stringBuilder.append(carValue.value)
            }
            stringBuilder.append(ending)
        }
        return stringBuilder.toString()
    }

    private fun requestRenderFrame() {
        if (mRequestRenderRunnable != null) {
            mRequestRenderRunnable!!.run()
        }
    }

    private fun appendList(builder: StringBuilder, values: List<*>?) {
        builder.append("[ ")
        for (value in values!!) {
            builder.append(value)
            builder.append(" ")
        }
        builder.append("]")
    }

    companion object {
        private const val TAG = "showcase"

        private const val ROW_SPACING = 10
        private const val LEFT_MARGIN = 15
        private const val MAX_FONT_SIZE = 32
    }
}
