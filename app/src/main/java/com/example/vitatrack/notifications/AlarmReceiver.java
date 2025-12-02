package com.example.vitatrack.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Recibe el intent con extras
        String habit = intent.getStringExtra("habit");
        String time = intent.getStringExtra("time");
        long id = intent.getLongExtra("id", -1);

        String title = "Recordatorio: " + (habit != null ? habit : "Tu hábito");
        String message = "Hora: " + (time != null ? time : "");

        NotificationHelper.showNotification(context, (int) (id % Integer.MAX_VALUE), title, message);
    }
}