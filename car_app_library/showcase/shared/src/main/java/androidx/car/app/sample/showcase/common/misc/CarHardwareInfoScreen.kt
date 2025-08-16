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
package androidx.car.app.sample.showcase.common.misc

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.CarValue
import androidx.car.app.hardware.common.OnCarDataAvailableListener
import androidx.car.app.hardware.info.EnergyProfile
import androidx.car.app.hardware.info.Model
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor

/**
 * Creates a screen that show the static information (such as model and energy profile) available
 * via CarHardware interfaces.
 */
class CarHardwareInfoScreen(carContext: CarContext) : Screen(carContext) {
    var mHasModelPermission: Boolean = false
    var mHasEnergyProfilePermission: Boolean = false
    val mCarHardwareExecutor: Executor = ContextCompat.getMainExecutor(getCarContext())

    /**
     * Value fetched from CarHardwareManager containing model information.
     *
     *
     * It is requested asynchronously and can be `null` until the response is
     * received.
     */
    var mModel: Model? = null

    /**
     * Value fetched from CarHardwareManager containing what type of fuel/ports the car has.
     *
     *
     * It is requested asynchronously and can be `null` until the response is
     * received.
     */
    var mEnergyProfile: EnergyProfile? = null

    var mModelListener: OnCarDataAvailableListener<Model> =
        OnCarDataAvailableListener { data: Model ->
            synchronized(
                this
            ) {
                Log.i(TAG, "Received model information: $data")
                mModel = data
                invalidate()
            }
        }

    var mEnergyProfileListener: OnCarDataAvailableListener<EnergyProfile> =
        OnCarDataAvailableListener { data: EnergyProfile ->
            synchronized(
                this
            ) {
                Log.i(TAG, "Received energy profile information: $data")
                mEnergyProfile = data
                invalidate()
            }
        }

    init {
        val lifecycle = lifecycle
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                val carHardwareManager =
                    getCarContext().getCarService(CarHardwareManager::class.java)
                val carInfo = carHardwareManager.carInfo

                // Request any single shot values.
                mModel = null
                try {
                    carInfo.fetchModel(mCarHardwareExecutor, mModelListener)
                    mHasModelPermission = true
                } catch (e: SecurityException) {
                    mHasModelPermission = false
                }

                mEnergyProfile = null
                try {
                    carInfo.fetchEnergyProfile(mCarHardwareExecutor, mEnergyProfileListener)
                    mHasEnergyProfilePermission = true
                } catch (e: SecurityException) {
                    mHasEnergyProfilePermission = false
                }
            }
        })
    }

    override fun onGetTemplate(): Template {
        val paneBuilder = Pane.Builder()
        if (allInfoAvailable()) {
            val modelRowBuilder = Row.Builder()
                .setTitle(carContext.getString(R.string.model_info))
            if (!mHasModelPermission) {
                modelRowBuilder.addText(carContext.getString(R.string.no_model_permission))
            } else {
                val info = StringBuilder()
                if (mModel!!.manufacturer.status != CarValue.STATUS_SUCCESS) {
                    info.append(carContext.getString(R.string.manufacturer_unavailable))
                    info.append(", ")
                } else {
                    info.append(mModel!!.manufacturer.value)
                    info.append(", ")
                }
                if (mModel!!.name.status != CarValue.STATUS_SUCCESS) {
                    info.append(carContext.getString(R.string.model_unavailable))
                    info.append(", ")
                } else {
                    info.append(mModel!!.name.value)
                    info.append(", ")
                }
                if (mModel!!.year.status != CarValue.STATUS_SUCCESS) {
                    info.append(carContext.getString(R.string.year_unavailable))
                } else {
                    info.append(mModel!!.year.value)
                }
                modelRowBuilder.addText(info)
            }
            paneBuilder.addRow(modelRowBuilder.build())

            val energyProfileRowBuilder = Row.Builder()
                .setTitle(carContext.getString(R.string.energy_profile))
            if (!mHasEnergyProfilePermission) {
                energyProfileRowBuilder.addText(
                    carContext
                        .getString(R.string.no_energy_profile_permission)
                )
            } else {
                val fuelInfo = StringBuilder()
                if (mEnergyProfile!!.fuelTypes.status != CarValue.STATUS_SUCCESS) {
                    fuelInfo.append(carContext.getString(R.string.fuel_types))
                    fuelInfo.append(": ")
                    fuelInfo.append(carContext.getString(R.string.unavailable))
                } else {
                    fuelInfo.append(carContext.getString(R.string.fuel_types))
                    fuelInfo.append(": ")
                    for (fuelType in mEnergyProfile!!.fuelTypes.value!!) {
                        fuelInfo.append(fuelTypeAsString(fuelType))
                        fuelInfo.append(" ")
                    }
                }
                energyProfileRowBuilder.addText(fuelInfo)
                val evInfo = StringBuilder()
                if (mEnergyProfile!!.evConnectorTypes.status != CarValue.STATUS_SUCCESS) {
                    evInfo.append(" ")
                    evInfo.append(carContext.getString(R.string.ev_connector_types))
                    evInfo.append(": ")
                    evInfo.append(carContext.getString(R.string.unavailable))
                } else {
                    evInfo.append(carContext.getString(R.string.ev_connector_types))
                    evInfo.append(": ")
                    for (connectorType in mEnergyProfile!!.evConnectorTypes.value!!) {
                        evInfo.append(evConnectorAsString(connectorType))
                        evInfo.append(" ")
                    }
                }
                energyProfileRowBuilder.addText(evInfo)
            }
            paneBuilder.addRow(energyProfileRowBuilder.build())
        } else {
            paneBuilder.setLoading(true)
        }
        return PaneTemplate.Builder(paneBuilder.build())
            .setHeaderAction(Action.BACK)
            .setTitle(carContext.getString(R.string.car_hardware_info))
            .build()
    }

    private fun allInfoAvailable(): Boolean {
        if (mHasModelPermission && mModel == null) {
            return false
        }
        if (mHasEnergyProfilePermission && mEnergyProfile == null) {
            return false
        }
        return true
    }

    private fun fuelTypeAsString(fuelType: Int): String {
        return when (fuelType) {
            EnergyProfile.FUEL_TYPE_UNLEADED -> "UNLEADED"
            EnergyProfile.FUEL_TYPE_LEADED -> "LEADED"
            EnergyProfile.FUEL_TYPE_DIESEL_1 -> "DIESEL_1"
            EnergyProfile.FUEL_TYPE_DIESEL_2 -> "DIESEL_2"
            EnergyProfile.FUEL_TYPE_BIODIESEL -> "BIODIESEL"
            EnergyProfile.FUEL_TYPE_E85 -> "E85"
            EnergyProfile.FUEL_TYPE_LPG -> "LPG"
            EnergyProfile.FUEL_TYPE_CNG -> "CNG"
            EnergyProfile.FUEL_TYPE_LNG -> "LNG"
            EnergyProfile.FUEL_TYPE_ELECTRIC -> "ELECTRIC"
            EnergyProfile.FUEL_TYPE_HYDROGEN -> "HYDROGEN"
            EnergyProfile.FUEL_TYPE_OTHER -> "OTHER"
            EnergyProfile.FUEL_TYPE_UNKNOWN -> "UNKNOWN"
            else -> "UNKNOWN"
        }
    }

    private fun evConnectorAsString(evConnectorType: Int): String {
        return when (evConnectorType) {
            EnergyProfile.EVCONNECTOR_TYPE_J1772 -> "J1772"
            EnergyProfile.EVCONNECTOR_TYPE_MENNEKES -> "MENNEKES"
            EnergyProfile.EVCONNECTOR_TYPE_CHADEMO -> "CHADEMO"
            EnergyProfile.EVCONNECTOR_TYPE_COMBO_1 -> "COMBO_1"
            EnergyProfile.EVCONNECTOR_TYPE_COMBO_2 -> "COMBO_2"
            EnergyProfile.EVCONNECTOR_TYPE_TESLA_ROADSTER -> "TESLA_ROADSTER"
            EnergyProfile.EVCONNECTOR_TYPE_TESLA_HPWC -> "TESLA_HPWC"
            EnergyProfile.EVCONNECTOR_TYPE_TESLA_SUPERCHARGER -> "TESLA_SUPERCHARGER"
            EnergyProfile.EVCONNECTOR_TYPE_GBT -> "GBT"
            EnergyProfile.EVCONNECTOR_TYPE_GBT_DC -> "GBT_DC"
            EnergyProfile.EVCONNECTOR_TYPE_SCAME -> "SCAME"
            EnergyProfile.EVCONNECTOR_TYPE_OTHER -> "OTHER"
            EnergyProfile.EVCONNECTOR_TYPE_UNKNOWN -> "UNKNOWN"
            else -> "UNKNOWN"
        }
    }

    companion object {
        private const val TAG = "showcase"
    }
}
