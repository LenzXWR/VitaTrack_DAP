package com.example.vitatrack.ui.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.MainActivity;
import com.example.vitatrack.ProfileActivity;
import com.example.vitatrack.ProgresoActivity;
import com.example.vitatrack.TipsActivity;
import com.example.vitatrack.notifications.AlarmReceiver;
import com.example.vitatrack.R;
import com.example.vitatrack.models.Reminder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    private static final int REQ_CREATE = 100;
    private RecyclerView rv;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;
    private String myUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            finish();
            return;
        }

        rv = findViewById(R.id.rvReminders);
        FloatingActionButton fab = findViewById(R.id.fabAddReminder);

        adapter = new ReminderAdapter(reminderList, (reminder, enabled) -> {
            reminder.setEnabled(enabled);
            actualizarEstadoEnFirebase(reminder);

            if (enabled) scheduleAlarm(RemindersActivity.this, reminder);
            else cancelAlarm(RemindersActivity.this, reminder);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        escucharRecordatorios();

        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateReminderActivity.class);
            startActivityForResult(i, REQ_CREATE);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_recordatorios);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_recordatorios) {
                return true; // Ya estamos aquí
            }
            else if (itemId == R.id.nav_inicio) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_progreso) {
                startActivity(new Intent(getApplicationContext(), ProgresoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_consejos) {
                startActivity(new Intent(getApplicationContext(), TipsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void escucharRecordatorios() {
        db.collection("recordatorios")
                .whereEqualTo("userId", myUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    reminderList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Reminder r = doc.toObject(Reminder.class);
                        reminderList.add(r);
                        if (r.isEnabled()) scheduleAlarm(this, r);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CREATE && resultCode == RESULT_OK && data != null) {
            String habit = data.getStringExtra("habit");
            String time = data.getStringExtra("time");
            String frequency = data.getStringExtra("frequency");
            long newId = System.currentTimeMillis();

            Reminder newR = new Reminder(newId, habit, time, frequency, true);
            newR.setUserId(myUserId);

            db.collection("recordatorios").document(String.valueOf(newId))
                    .set(newR)
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
        }
    }

    private void actualizarEstadoEnFirebase(Reminder r) {
        db.collection("recordatorios").document(String.valueOf(r.getId()))
                .update("enabled", r.isEnabled());
    }

    private void scheduleAlarm(Context ctx, Reminder r) {
        String[] parts = r.getTime().split(":");
        int hour = 8, minute = 0;
        try {
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (Exception e) { }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);

        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("habit", r.getHabitType());
        intent.putExtra("id", r.getId());

        PendingIntent pending = PendingIntent.getBroadcast(
                ctx, (int) r.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
            } catch (SecurityException e) { }
        }
    }

    private void cancelAlarm(Context ctx, Reminder r) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                ctx, (int) r.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pending);
    }
}