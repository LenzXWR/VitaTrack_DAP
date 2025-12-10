package com.example.vitatrack.ui.reminders;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vitatrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateReminderActivity extends AppCompatActivity {

    private Spinner spinnerHabit, spinnerFrequency;
    private TextView tvSelectedTime;
    private String selectedTime = "08:00";

    // Firebase
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        spinnerHabit = findViewById(R.id.spinnerHabit);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        Button btnPickTime = findViewById(R.id.btnPickTime);
        Button btnSave = findViewById(R.id.btnSaveReminder);

        cargarHabitosEnSpinner();

        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(
                this, R.array.freq_array, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(freqAdapter);

        tvSelectedTime.setText(selectedTime);

        btnPickTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, h, m) -> {
                selectedTime = String.format("%02d:%02d", h, m);
                tvSelectedTime.setText(selectedTime);
            }, hour, minute, true).show();
        });

        btnSave.setOnClickListener(v -> {
            if (spinnerHabit.getSelectedItem() == null) {
                Toast.makeText(this, "Primero crea un hábito en el Inicio", Toast.LENGTH_LONG).show();
                return;
            }

            Intent result = new Intent();
            result.putExtra("habit", spinnerHabit.getSelectedItem().toString());
            result.putExtra("time", selectedTime);
            result.putExtra("frequency", spinnerFrequency.getSelectedItem().toString());

            setResult(RESULT_OK, result);
            finish();
        });
    }

    private void cargarHabitosEnSpinner() {
        if (userId == null) return;

        db.collection("habitos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> listaNombres = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) {
                            listaNombres.add(nombre);
                        }
                    }

                    if (listaNombres.isEmpty()) {
                        listaNombres.add("No tienes hábitos creados");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CreateReminderActivity.this,
                            android.R.layout.simple_spinner_item,
                            listaNombres
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerHabit.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar hábitos", Toast.LENGTH_SHORT).show();
                });
    }
}