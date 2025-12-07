package com.example.vitatrack.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.vitatrack.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vitatrack_reminders";
    private static final String CHANNEL_NAME = "Recordatorios VitaTrack";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Recibe el intent con los extras
        String habit = intent.getStringExtra("habit");
        String time = intent.getStringExtra("time");
        long id = intent.getLongExtra("id", -1);

        String title = "Recordatorio: " + (habit != null ? habit : "Tu hábito");
        String message = "Hora: " + (time != null ? time : "");

        // Crear el canal de notificación si es necesario
        createChannelIfNeeded(context);

        // Crear la notificación
        NotificationHelper.showNotification(context, (int) (id % Integer.MAX_VALUE), title, message);
    }

    private static void createChannelIfNeeded(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para recordatorios de hábitos");
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }
}
