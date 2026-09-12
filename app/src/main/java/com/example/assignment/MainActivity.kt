package com.example.assignment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var reminderCard: CardView
    private lateinit var reminderWork: android.widget.TextView
    private lateinit var reminderTime: android.widget.TextView
    private lateinit var cancelButton: android.widget.Button
    private lateinit var addReminderButton: android.widget.Button

    private var selectedReminder = ""

    companion object {
        const val SERVICE_KEY = "Service1"
        const val START_VAL = "start"
        const val STOP_VAL = "stop"
        const val REMINDER_TEXT = "reminderText"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reminderCard = findViewById(R.id.reminderCard)
        reminderWork = findViewById(R.id.reminderWork)
        reminderTime = findViewById(R.id.reminderTime)
        cancelButton = findViewById(R.id.cancelButton)
        addReminderButton = findViewById(R.id.addReminderButton)

        addReminderButton.setOnClickListener {
            showReminderDialog()
        }

        cancelButton.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun showReminderDialog() {

        val editText = EditText(this)

        editText.hint = "Enter your work"
        editText.setSingleLine(true)

        val container = LinearLayout(this)
        container.setPadding(40, 10, 40, 10)
        container.addView(editText)

        AlertDialog.Builder(this)
            .setTitle("Add Reminder")
            .setMessage("What do you want to do?")
            .setView(container)
            .setPositiveButton("Next") { _, _ ->

                val work = editText.text.toString()

                if (work.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please enter your work",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    selectedReminder = work
                    showTimeDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTimeDialog() {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->

                setReminder(
                    selectedHour,
                    selectedMinute
                )

            },
            hour,
            minute,
            false
        )

        timePicker.show()
    }

    private fun setReminder(hour: Int, minute: Int) {

        val calendar = Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            hour
        )

        calendar.set(
            Calendar.MINUTE,
            minute
        )

        calendar.set(
            Calendar.SECOND,
            0
        )

        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        reminderWork.text = selectedReminder

        reminderTime.text = String.format(
            "%02d:%02d",
            hour,
            minute
        )

        reminderCard.visibility = View.VISIBLE

        setAlarm(
            calendar.timeInMillis,
            START_VAL,
            selectedReminder
        )

        Toast.makeText(
            this,
            "Reminder saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setAlarm(
        millisTime: Long,
        action: String,
        reminder: String
    ) {

        val intent = Intent(
            this,
            AlarmBroadcastReceiver::class.java
        )

        intent.putExtra(
            SERVICE_KEY,
            action
        )

        intent.putExtra(
            REMINDER_TEXT,
            reminder
        )

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (action == START_VAL) {

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                millisTime,
                pendingIntent
            )

        } else {

            alarmManager.cancel(pendingIntent)

            sendBroadcast(intent)
        }
    }

    private fun cancelAlarm() {

        val intent = Intent(
            this,
            AlarmBroadcastReceiver::class.java
        )

        intent.putExtra(
            SERVICE_KEY,
            STOP_VAL
        )

        intent.putExtra(
            REMINDER_TEXT,
            selectedReminder
        )

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        sendBroadcast(intent)

        reminderCard.visibility = View.GONE

        Toast.makeText(
            this,
            "Reminder cancelled",
            Toast.LENGTH_SHORT
        ).show()
    }
}