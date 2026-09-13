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

        val alarmId =
            intent.getIntExtra(MainActivity.ALARM_ID, -1)

        val serviceIntent =
            Intent(context, AlarmService::class.java)

        if (serviceValue == MainActivity.START_VAL) {

            serviceIntent.putExtra(
                MainActivity.REMINDER_TEXT,
                intent.getStringExtra(MainActivity.REMINDER_TEXT)
            )

            context.startService(serviceIntent)

            // 1. Notify MainActivity immediately (if it is in the foreground)
            val firedIntent =
                Intent(MainActivity.ALARM_FIRED_ACTION)
            firedIntent.putExtra(MainActivity.ALARM_ID, alarmId)
            context.sendBroadcast(firedIntent)

            // 2. Persist fired alarm ID so the card is also removed on next resume
            //    in case the app was in the background when the alarm fired
            if (alarmId != -1) {
                val prefs =
                    context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
                val fired =
                    prefs.getStringSet("fired_alarms", mutableSetOf())!!.toMutableSet()
                fired.add(alarmId.toString())
                prefs.edit().putStringSet("fired_alarms", fired).apply()
            }

        } else {

            context.stopService(serviceIntent)
        }
    }
}