package com.example.vitatrack.ui.reminders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // Datos falsos por ahora (demo)
        reminderList.add(new Reminder(1, "Consumo de Agua", "08:00", "Diario", true));
        reminderList.add(new Reminder(2, "Actividad Física", "18:00", "Lunes, Miércoles, Viernes", false));

        adapter = new ReminderAdapter(reminderList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateReminderActivity.class);
            startActivityForResult(i, REQ_CREATE);
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindersActivity.this, CreateReminderActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CREATE && resultCode == RESULT_OK && data != null) {

            String habit = data.getStringExtra("habit");
            String time = data.getStringExtra("time");
            String frequency = data.getStringExtra("frequency");

            Reminder newR = new Reminder(
                    System.currentTimeMillis(),
                    habit,
                    time,
                    frequency,
                    true
            );

            reminderList.add(newR);
            adapter.notifyItemInserted(reminderList.size() - 1);
        }
    }
}