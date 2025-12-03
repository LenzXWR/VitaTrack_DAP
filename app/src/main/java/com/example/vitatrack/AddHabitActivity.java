package com.example.vitatrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AddHabitActivity extends AppCompatActivity {

    private TextInputEditText etNombreHabito;
    private TextInputEditText etCantidad;
    private Button btnGuardar;
    private Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        etNombreHabito = findViewById(R.id.etNombreHabito);

        etCantidad = findViewById(R.id.etCantidad);

        btnGuardar = findViewById(R.id.btnGuardar);

        btnCancelar = findViewById(R.id.btnCancelar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombreHabito.getText().toString();
                String cantidad = etCantidad.getText().toString();

                if (nombre.isEmpty() || cantidad.isEmpty()) {
                    if(nombre.isEmpty()) etNombreHabito.setError("Escribe un nombre");
                    if(cantidad.isEmpty()) etCantidad.setError("Define una meta");
                } else {

                    Toast.makeText(AddHabitActivity.this, "Hábito guardado: " + nombre, Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la pantalla y vuelve al inicio
                }
            }
        });

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Solo cierra la pantalla
                }
            });
        }
    }
}