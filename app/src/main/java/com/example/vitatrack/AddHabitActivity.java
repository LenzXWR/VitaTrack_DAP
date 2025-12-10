package com.example.vitatrack;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddHabitActivity extends AppCompatActivity {

    private TextInputEditText etNombreHabito;
    private TextInputEditText etCantidad;
    private Button btnGuardar;
    private Button btnCancelar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        db = FirebaseFirestore.getInstance();

        etNombreHabito = findViewById(R.id.etNombreHabito);
        etCantidad = findViewById(R.id.etCantidad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnGuardar.setOnClickListener(v -> guardarHabito());

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> finish());
        }
    }

    private void guardarHabito() {
        String nombre = etNombreHabito.getText().toString().trim();
        String cantidad = etCantidad.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombreHabito.setError("Escribe un nombre");
            return;
        }
        if (TextUtils.isEmpty(cantidad)) {
            etCantidad.setError("Define una meta");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> nuevoHabito = new HashMap<>();
        nuevoHabito.put("nombre", nombre);
        nuevoHabito.put("progreso", "0 de " + cantidad);

        nuevoHabito.put("userId", userId);

        db.collection("habitos")
                .add(nuevoHabito)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Hábito privado guardado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}