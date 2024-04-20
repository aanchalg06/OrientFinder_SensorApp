package com.aanchal.sensorapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable

class OrientationSensor(
    private val context: Context,
    private val callback: (Float, Float, Float) -> Unit
) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationMatrix = FloatArray(9)
    private val orientationValues = FloatArray(3)

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationValues)
            val roll = Math.toDegrees(orientationValues[2].toDouble()).toFloat() // Roll
            val pitch = Math.toDegrees(((orientationValues[0]+orientationValues[2])/2).toDouble()).toFloat() // Corrected pitch
            val yaw = Math.toDegrees(orientationValues[0].toDouble()).toFloat() // Yaw
            callback(roll, pitch, yaw)
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
