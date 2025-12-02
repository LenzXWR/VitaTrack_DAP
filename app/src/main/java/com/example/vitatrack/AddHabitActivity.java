package com.example.vitatrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddHabitActivity extends AppCompatActivity {

    private Spinner spinnerHabitType;
    private EditText editQuantity;
    private Button btnSaveHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        spinnerHabitType = findViewById(R.id.spinnerHabitType);
        editQuantity = findViewById(R.id.editHabitQuantity);
        btnSaveHabit = findViewById(R.id.btnSaveHabit);

        // Poblar Spinner
        String[] habitTypes = {"Consumo de Agua", "Actividad Física", "Horas de Sueño"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                habitTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHabitType.setAdapter(adapter);

        // Cambiar hint según selección
        spinnerHabitType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        editQuantity.setHint("Cantidad (vasos)");
                        break;
                    case 1:
                        editQuantity.setHint("Duración (minutos)");
                        break;
                    case 2:
                        editQuantity.setHint("Horas dormidas");
                        break;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Guardar
        btnSaveHabit.setOnClickListener(v -> {
            String cantidad = editQuantity.getText().toString().trim();

            if (cantidad.isEmpty()) {
                editQuantity.setError("Ingrese un valor");
                return;
            }

            finish();
        });
    }
}

