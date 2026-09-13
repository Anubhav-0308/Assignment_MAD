package com.example.assignment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var addReminderButton: Button
    private lateinit var remindersContainer: LinearLayout

    // Maps alarmId → the inflated card View
    private val reminderCards = mutableMapOf<Int, android.view.View>()

    // Receives the "alarm just fired" signal from AlarmBroadcastReceiver
    private val alarmFiredReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val alarmId = intent?.getIntExtra(ALARM_ID, -1) ?: -1
            if (alarmId != -1) {
                removeReminderCard(alarmId)
                saveReminders()
            }
        }
    }

    companion object {
        const val SERVICE_KEY   = "Service1"
        const val START_VAL     = "start"
        const val STOP_VAL      = "stop"
        const val REMINDER_TEXT = "reminderText"
        const val ALARM_ID      = "alarmId"

        // Broadcast action sent by AlarmBroadcastReceiver when an alarm fires
        const val ALARM_FIRED_ACTION = "com.example.assignment.ALARM_FIRED"

        private const val PREFS_NAME  = "reminders_prefs"
        private const val PREFS_KEY   = "saved_reminders"
        private const val FIRED_KEY   = "fired_alarms"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addReminderButton  = findViewById(R.id.addReminderButton)
        remindersContainer = findViewById(R.id.remindersContainer)

        addReminderButton.setOnClickListener {
            showReminderDialog()
        }

        // Restore cards that were saved before the app was last closed
        loadReminders()
    }

    override fun onResume() {
        super.onResume()

        // Listen for alarm-fired events while the app is in the foreground
        registerReceiver(
            alarmFiredReceiver,
            IntentFilter(ALARM_FIRED_ACTION),
            RECEIVER_NOT_EXPORTED
        )

        // Also handle alarms that fired while the app was in the background
        removeFiredAlarms()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(alarmFiredReceiver)
    }

    // ─── Dialogs ───────────────────────────────────────────────────────────────

    private fun showReminderDialog() {
        val editText = EditText(this)
        editText.hint = "Enter your work"
        editText.setSingleLine(true)

        AlertDialog.Builder(this)
            .setTitle("Add Reminder")
            .setMessage("What work is this reminder for?")
            .setView(editText)
            .setPositiveButton("NEXT") { _, _ ->
                val work = editText.text.toString().trim()
                if (work.isEmpty()) {
                    Toast.makeText(this, "Please enter your work", Toast.LENGTH_SHORT).show()
                } else {
                    showTimeDialog(work)
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showTimeDialog(work: String) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                createReminder(work, selectedHour, selectedMinute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    // ─── Reminder creation ─────────────────────────────────────────────────────

    private fun createReminder(text: String, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE,      minute)
            set(Calendar.SECOND,      0)
            set(Calendar.MILLISECOND, 0)
            // If the chosen time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Safe alarm ID: avoids Int overflow from raw currentTimeMillis
        val alarmId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        addReminderCard(alarmId, text, hour, minute, calendar.timeInMillis)
        setAlarm(calendar.timeInMillis, START_VAL, text, alarmId)
        saveReminders()

        Toast.makeText(this, "Reminder added", Toast.LENGTH_SHORT).show()
    }

    // ─── Card management ───────────────────────────────────────────────────────

    /** Inflate item_reminder_card.xml, fill it with data, and append to container. */
    private fun addReminderCard(
        alarmId: Int,
        text: String,
        hour: Int,
        minute: Int,
        triggerMillis: Long
    ) {
        val cardView = LayoutInflater.from(this)
            .inflate(R.layout.item_reminder_card, remindersContainer, false)

        // Description
        cardView.findViewById<TextView>(R.id.cardReminderWork).text = "📌 $text"

        // Date  (derived from triggerMillis so "tomorrow" is shown correctly)
        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        val dateStr = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
            .format(triggerCal.time)
        cardView.findViewById<TextView>(R.id.cardReminderDate).text = "📅 $dateStr"

        // Time (convert 24-hour → 12-hour)
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else      -> hour
        }
        cardView.findViewById<TextView>(R.id.cardReminderTime).text =
            String.format("⏰ %02d:%02d %s", displayHour, minute, amPm)

        // Cancel button
        cardView.findViewById<Button>(R.id.cardCancelButton).setOnClickListener {
            cancelAlarm(alarmId)
            removeReminderCard(alarmId)
            saveReminders()
            Toast.makeText(this, "Reminder cancelled", Toast.LENGTH_SHORT).show()
        }

        // Tag the view with a pipe-delimited string so we can persist & restore it
        cardView.tag = "$alarmId|$text|$hour|$minute|$triggerMillis"

        reminderCards[alarmId] = cardView
        remindersContainer.addView(cardView)
    }

    /** Remove a card from the UI and from the tracking map. */
    private fun removeReminderCard(alarmId: Int) {
        val card = reminderCards.remove(alarmId)
        if (card != null) {
            remindersContainer.removeView(card)
        }
    }

    // ─── Persistence ───────────────────────────────────────────────────────────

    /** Save all current reminder cards to SharedPreferences. */
    private fun saveReminders() {
        val set = reminderCards.values
            .mapNotNull { it.tag as? String }
            .toSet()
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(PREFS_KEY, set)
            .apply()
    }

    /** Recreate cards for reminders that haven't expired yet. */
    private fun loadReminders() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getStringSet(PREFS_KEY, emptySet()) ?: return
        val now   = System.currentTimeMillis()

        for (entry in saved) {
            runCatching {
                val parts        = entry.split("|")
                val alarmId      = parts[0].toInt()
                val text         = parts[1]
                val hour         = parts[2].toInt()
                val minute       = parts[3].toInt()
                val triggerMillis = parts[4].toLong()

                if (triggerMillis > now) {      // still in the future → show card
                    addReminderCard(alarmId, text, hour, minute, triggerMillis)
                }
            }
        }
    }

    /**
     * AlarmBroadcastReceiver writes fired alarm IDs to SharedPreferences when the
     * app is in the background. Here we clear those stale cards on resume.
     */
    private fun removeFiredAlarms() {
        val prefs   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val firedIds = prefs.getStringSet(FIRED_KEY, emptySet()) ?: return

        if (firedIds.isEmpty()) return

        for (idStr in firedIds) {
            val alarmId = idStr.toIntOrNull() ?: continue
            removeReminderCard(alarmId)
        }
        prefs.edit().remove(FIRED_KEY).apply()
        saveReminders()
    }

    // ─── Alarm scheduling ──────────────────────────────────────────────────────

    private fun setAlarm(
        millisTime: Long,
        action: String,
        reminder: String,
        alarmId: Int
    ) {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java).apply {
            putExtra(SERVICE_KEY,   action)
            putExtra(REMINDER_TEXT, reminder)
            putExtra(ALARM_ID,      alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, alarmId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (action == START_VAL) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, millisTime, pendingIntent)
            } else {
                Toast.makeText(
                    this,
                    "Exact alarms are disabled. Please enable them in Settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun cancelAlarm(alarmId: Int) {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java).apply {
            putExtra(SERVICE_KEY, STOP_VAL)
            putExtra(ALARM_ID,    alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, alarmId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        // Also stop the AlarmService if it is currently playing the sound
        sendBroadcast(intent)
    }
}
