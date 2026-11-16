package com.tharun.mymusic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

class BecomingNoisyReceiver(
    private val context: Context,
    private val onBecomingNoisy: () -> Unit
) : BroadcastReceiver() {

    private var isRegistered = false

    fun register() {
        if (!isRegistered) {
            context.registerReceiver(
                this,
                IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            )
            isRegistered = true
        }
    }

    fun unregister() {
        if (isRegistered) {
            try {
                context.unregisterReceiver(this)
            } catch (_: Exception) {}
            isRegistered = false
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            onBecomingNoisy()
        }
    }
}
