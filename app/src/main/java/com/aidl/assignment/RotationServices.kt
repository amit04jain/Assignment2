package com.aidl.assignment

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData

class RotationServices  : LifecycleService(), SensorEventListener {
    companion object {
        val sensorData = MutableLiveData<FloatArray>()
    }

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private fun createSensorManager() {
        if (sensorManager == null) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            sensor?.let {
                addRotationSensorListener()
            }
        }
    }

    private fun addRotationSensorListener() {
        sensorManager?.registerListener(
            this,
            sensor,
            8
        )
    }

    private val myBinder: RemoteServicesInterface.Stub = object : RemoteServicesInterface.Stub() {
        override fun orientation(): String {
            createSensorManager()
            return sensorData.value?.contentToString() ?: "Hello Bhai"
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return myBinder
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                sensorData.value = it.values
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}