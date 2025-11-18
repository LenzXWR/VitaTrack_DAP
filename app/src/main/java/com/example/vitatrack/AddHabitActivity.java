package com.example.vitatrack;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddHabitActivity extends AppCompatActivity {

    private Spinner spinnerHabit;
    private EditText etQuantity;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        // Tus ids originales
        spinnerHabit = findViewById(R.id.TipoDeHabito);
        etQuantity = findViewById(R.id.cantidad);
        btnSave = findViewById(R.id.GuardarHabito);

        // Configurar spinner (correcto)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.habit_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHabit.setAdapter(adapter);

        // Botón guardar
        btnSave.setOnClickListener(v -> finish());
    }
}
