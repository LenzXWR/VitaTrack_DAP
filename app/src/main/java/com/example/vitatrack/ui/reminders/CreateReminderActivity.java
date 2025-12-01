package com.example.vitatrack.ui.reminders;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vitatrack.R;

import java.util.Calendar;

public class CreateReminderActivity extends AppCompatActivity {

    private Spinner spinnerHabit, spinnerFrequency;
    private TextView tvSelectedTime;
    private String selectedTime = "08:00";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        spinnerHabit = findViewById(R.id.spinnerHabit);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        Button btnPickTime = findViewById(R.id.btnPickTime);
        Button btnSave = findViewById(R.id.btnSaveReminder);

        ArrayAdapter<CharSequence> habitAdapter = ArrayAdapter.createFromResource(
                this, R.array.habits_array, android.R.layout.simple_spinner_item);
        habitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerHabit.setAdapter(habitAdapter);

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
            Intent result = new Intent();
            result.putExtra("habit", spinnerHabit.getSelectedItem().toString());
            result.putExtra("time", selectedTime);
            result.putExtra("frequency", spinnerFrequency.getSelectedItem().toString());

            setResult(RESULT_OK, result);
            finish();
        });
    }
}