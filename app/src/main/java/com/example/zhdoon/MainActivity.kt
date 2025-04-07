package com.example.zhdoon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var waitingButton: Button
    private var minutes: Int = 0
    private var timeReceiver: TimeReceiver? = null
    private val toast_text = "Больше не ждём"
    private val batteryReceiver = BatteryReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTextView = findViewById(R.id.timeTextView)
        waitingButton = findViewById(R.id.waitingButton)

        waitingButton.setOnClickListener {
            unregisterTimeReceiver()
            Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        registerTimeReceiver()
        registerBatteryReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterTimeReceiver()
        unregisterBatteryReceiver()
    }

    private fun registerTimeReceiver() {
        timeReceiver = TimeReceiver()
        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        registerReceiver(timeReceiver, filter)
    }

    private fun unregisterTimeReceiver() {
        timeReceiver?.let {
            unregisterReceiver(it)
            timeReceiver = null
        }
    }

    private fun registerBatteryReceiver() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    private fun unregisterBatteryReceiver() {
        unregisterReceiver(batteryReceiver)
    }

    inner class TimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            minutes++
            timeTextView.text = "Время созерцания: $minutes мин."
        }
    }

    inner class BatteryReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct: Float = level * 100 / scale.toFloat()
            val plugged: Int = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0

            if (plugged == 0) {
                timeTextView.text = "накормите Ждуна, силы на исходе!"
            }
        }
    }
}
