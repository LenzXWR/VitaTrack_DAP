package com.example.vitatrack;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.Intent;

public class AddHabitActivity extends AppCompatActivity {

    private Spinner spinnerHabit;
    private EditText etQuantity;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        spinnerHabit = findViewById(R.id.spinnerHabitType);
        etQuantity = findViewById(R.id.etQuantity);
        btnSave = findViewById(R.id.btnSaveHabit);

        // Spinner configuration
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.habit_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHabit.setAdapter(adapter);

        // Save button
        btnSave.setOnClickListener(v -> finish()); // Just closes and returns
    }
}
