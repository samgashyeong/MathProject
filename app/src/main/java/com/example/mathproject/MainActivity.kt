package com.example.mathproject

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.mathproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    //주요 객체들 정의
    private lateinit var mainSensorManager: SensorManager
    private lateinit var mainGyroLis : SensorEventListener
    private lateinit var mainSensor : Sensor

    private var pitch : Double = 0.0
    private var roll  : Double = 0.0
    private var yaw : Double = 0.0

    private var timestamp = 0.0
    private var dt = 0.0

    companion object {
        private const val NANOSECOND_TO_SECOND = 1.0f / 1000000000.0f
        private const val RADIAN_TO_DEGREE = 180 / Math.PI
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_main)
        mainSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mainGyroLis = GyroscopeListener()

        mainSensor = mainSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mainSensorManager.registerListener(
                    mainGyroLis,
                    mainSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
    }

    public override fun onPause() {
        super.onPause()
        Log.e("LOG", "onPause()")
        mainSensorManager.unregisterListener(mainGyroLis)
    }

    public override fun onDestroy() {
        super.onDestroy()
        Log.e("LOG", "onDestroy()")
        mainSensorManager.unregisterListener(mainGyroLis)
    }

    private inner class GyroscopeListener : SensorEventListener {
        @SuppressLint("SetTextI18n")
        override fun onSensorChanged(event: SensorEvent) {
            val gyroX = event.values[0].toDouble()
            val gyroY = event.values[1].toDouble()
            val gyroZ = event.values[2].toDouble()

            //dt = 센서가 현재 상태를 감지하는 시간 간격(단위시간)
            dt = (event.timestamp - timestamp) * NANOSECOND_TO_SECOND
            timestamp = event.timestamp.toDouble()

            if (dt - timestamp * NANOSECOND_TO_SECOND != 0.0) {

                //pitch = (X축 회전각) roll = (Y축 회전각) yaw = (Z축 회전각)
                pitch += gyroX * dt * RADIAN_TO_DEGREE
                roll += gyroY * dt * RADIAN_TO_DEGREE
                yaw += gyroZ * dt * RADIAN_TO_DEGREE


                binding.textX.text = "X축 각속도 : ${String.format("%.3f", gyroX)}"
                binding.textY.text = "Y축 각속도 : ${String.format("%.3f", gyroY)}"
                binding.textZ.text = "Z축 각속도 : ${String.format("%.3f", gyroZ)}"
                binding.textPitch.text = "X축 회전각 : ${String.format("%.3f", pitch)}"
                binding.textRoll.text = "Y축 회전각 : ${String.format("%.3f", roll)}"
                binding.textYaw.text = "Z축 회전각 : ${String.format("%.3f", yaw)}"
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
}