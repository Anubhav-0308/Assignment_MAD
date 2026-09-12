package com.example.assignment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val serviceValue =
            intent.getStringExtra(MainActivity.SERVICE_KEY)

        val serviceIntent =
            Intent(context, AlarmService::class.java)

        if (serviceValue == MainActivity.START_VAL) {

            serviceIntent.putExtra(
                MainActivity.REMINDER_TEXT,
                intent.getStringExtra(
                    MainActivity.REMINDER_TEXT
                )
            )

            context.startService(serviceIntent)

        } else {

            context.stopService(serviceIntent)
        }
    }
}