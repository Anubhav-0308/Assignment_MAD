package com.example.assignment

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.Toast

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val reminder =
            intent?.getStringExtra(
                MainActivity.REMINDER_TEXT
            )

        Toast.makeText(
            this,
            "Reminder: $reminder",
            Toast.LENGTH_LONG
        ).show()

        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.song
        )

        mediaPlayer?.start()

        return START_STICKY
    }

    override fun onDestroy() {

        mediaPlayer?.stop()

        mediaPlayer?.release()

        mediaPlayer = null

        Toast.makeText(
            this,
            "Alarm stopped",
            Toast.LENGTH_SHORT
        ).show()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}