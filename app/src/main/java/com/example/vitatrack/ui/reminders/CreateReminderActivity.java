package com.example.vitatrack.ui.reminders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vitatrack.R;

public class CreateReminderActivity extends AppCompatActivity {

    private Spinner spinnerHabit, spinnerFrequency;
    private TimePicker timePicker;
    private String selectedTime = "08:00"; // Default time

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        spinnerHabit = findViewById(R.id.spinnerHabit);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        timePicker = findViewById(R.id.timePicker); // Usamos directamente el TimePicker
        Button btnSave = findViewById(R.id.btnSaveReminder);

        // Configurar el TimePicker para usar formato AM/PM
        timePicker.setIs24HourView(false);  // Esto hace que use AM/PM en lugar de 24 horas

        // Configurar el Spinner de hábitos
        ArrayAdapter<CharSequence> habitAdapter = ArrayAdapter.createFromResource(
                this, R.array.habits_array, android.R.layout.simple_spinner_item);
        habitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHabit.setAdapter(habitAdapter);

        // Configurar el Spinner de frecuencia
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(
                this, R.array.freq_array, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(freqAdapter);

        btnSave.setOnClickListener(v -> {

            // Validaciones
            if (spinnerHabit.getSelectedItem() == null) {
                Toast.makeText(this, "Seleccione un hábito", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener la hora seleccionada del TimePicker
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            selectedTime = String.format("%02d:%02d", hour, minute);

            if (selectedTime == null || selectedTime.isEmpty()) {
                Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerFrequency.getSelectedItem() == null) {
                Toast.makeText(this, "Seleccione la frecuencia", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el objeto de recordatorio
            Intent result = new Intent();
            result.putExtra("habit", spinnerHabit.getSelectedItem().toString());
            result.putExtra("time", selectedTime);
            result.putExtra("frequency", spinnerFrequency.getSelectedItem().toString());

            // Enviar los resultados a la actividad que llama
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
