package com.example.vitatrack;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddHabitActivity extends AppCompatActivity {

    private TextInputEditText etNombreHabito;
    private TextInputEditText etDescripcion;
    private Button btnGuardar;
    private Button btnCancelar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        // Inicializar FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Asociar las vistas con los IDs
        etNombreHabito = findViewById(R.id.etNombreHabito);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Acción para guardar el hábito
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombreHabito.getText().toString();
                String descripcion = etDescripcion.getText().toString();

                // Validación de campos vacíos
                if (nombre.isEmpty() || descripcion.isEmpty()) {
                    if (nombre.isEmpty()) etNombreHabito.setError("Escribe un nombre");
                    if (descripcion.isEmpty()) etDescripcion.setError("Define una meta");
                } else {
                    // Obtener el UID del usuario autenticado
                    String userId = mAuth.getCurrentUser().getUid();

                    // Crear un objeto Hábito para almacenar en Firestore
                    Habito nuevoHabito = new Habito(nombre, descripcion);

                    // Guardar el hábito en Firestore
                    db.collection("usuarios")
                            .document(userId)
                            .collection("habitos")
                            .add(nuevoHabito) // Guardamos el hábito en la subcolección "habitos"
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(AddHabitActivity.this, "Hábito guardado", Toast.LENGTH_SHORT).show();

                                // Devolver un resultado a MainActivity para actualizar la lista de hábitos
                                setResult(RESULT_OK);
                                finish(); // Cierra la actividad después de guardar el hábito
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddHabitActivity.this, "Error al guardar el hábito: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        // Acción para cancelar la operación
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la pantalla sin hacer nada más
            }
        });
    }
}
