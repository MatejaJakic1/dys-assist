package com.example.sldapp.ADHD

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sldapp.Preferences.PrefUtilAdhd
import com.example.sldapp.Preferences.PrefUtilNotification

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtilNotification.setBroadcastCompleted(true, context)
        PrefUtilNotification.sendNotification(context)
        PrefUtilAdhd.setTimerState(AdhdActivity.TimerState.Stopped, context)
        PrefUtilAdhd.setAlarmSetTime(0, context)
    }
}