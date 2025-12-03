package com.example.vitatrack.ui.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.notifications.AlarmReceiver;
import com.example.vitatrack.storage.ReminderStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.vitatrack.R;
import com.example.vitatrack.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    private static final int REQ_CREATE = 100;
    private RecyclerView rv;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        rv = findViewById(R.id.rvReminders);
        FloatingActionButton fab = findViewById(R.id.fabAddReminder);

        // 1. Cargar datos (o usar demo si está vacío)
        reminderList = ReminderStorage.loadReminders(this);

        if (reminderList.isEmpty()) {
            reminderList.add(new Reminder(1, "Consumo de Agua", "08:00", "Diario", true));
            reminderList.add(new Reminder(2, "Actividad Física", "18:00", "Lunes, Miércoles, Viernes", false));
        }

        // 2. Configurar el Adaptador
        adapter = new ReminderAdapter(reminderList, (reminder, enabled) -> {
            // Guardar estado cambiado (Switch on/off)
            ReminderStorage.saveReminders(RemindersActivity.this, reminderList);

            // Activar o cancelar alarma del sistema
            if (enabled) scheduleAlarm(RemindersActivity.this, reminder);
            else cancelAlarm(RemindersActivity.this, reminder);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 3. El Botón Flotante (Versión fusionada)
        // Usamos startActivityForResult para saber cuando el usuario guardó algo nuevo
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateReminderActivity.class);
            startActivityForResult(i, REQ_CREATE);
        });

        // 4. Reprogramar alarmas existentes al abrir la app (Lógica de Jhan)
        for (Reminder r : reminderList) {
            if (r.isEnabled()) scheduleAlarm(this, r);
        }
    }

    // Este método recibe los datos cuando vuelves de "Crear Recordatorio"
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CREATE && resultCode == RESULT_OK && data != null) {

            String habit = data.getStringExtra("habit");
            String time = data.getStringExtra("time");
            String frequency = data.getStringExtra("frequency");

            long newId = System.currentTimeMillis();

            Reminder newR = new Reminder(
                    newId, // Usamos el tiempo actual como ID único
                    habit,
                    time,
                    frequency,
                    true
            );

            // Agregamos al inicio de la lista
            reminderList.add(0, newR);
            adapter.notifyItemInserted(0);
            rv.scrollToPosition(0); // Hacemos scroll arriba para ver el nuevo

            // Guardar en almacenamiento y programar alarma
            ReminderStorage.saveReminders(this, reminderList);
            scheduleAlarm(this, newR);
        }
    }

    // --- MÉTODOS DE ALARMA (Lógica de Jhan) ---

    private void scheduleAlarm(Context ctx, Reminder r) {
        // transformar hora "HH:mm" a hora y minuto
        String[] parts = r.getTime().split(":");
        int hour = 8, minute = 0;
        try {
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (Exception e) { }

        // crear Calendar para la próxima ocurrencia
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        // si la hora ya pasó hoy, programar para mañana
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("habit", r.getHabitType());
        intent.putExtra("time", r.getTime());
        intent.putExtra("id", r.getId());

        PendingIntent pending = PendingIntent.getBroadcast(
                ctx,
                (int) (r.getId() % Integer.MAX_VALUE),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            // Usamos setExactAndAllowWhileIdle para que suene incluso en modo ahorro
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
            } catch (SecurityException e) {
                // En Android 12+ se requieren permisos especiales para alarmas exactas,
                // pero para este proyecto esto debería bastar.
            }
        }
    }

    private void cancelAlarm(Context ctx, Reminder r) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                ctx,
                (int) (r.getId() % Integer.MAX_VALUE),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(pending);
        }
    }
}