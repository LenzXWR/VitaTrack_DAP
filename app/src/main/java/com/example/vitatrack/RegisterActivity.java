package com.example.vitatrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // 1. Declarar variables
    private EditText etNombre, etEmail, etPassword;
    private Button btnRegister;
    private TextView txtLogin;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 3. Conectar con XML (Asegúrate de tener estos IDs en tu XML)
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        // 4. Lógica del Botón Registrar
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validaciones simples
                if (TextUtils.isEmpty(nombre)) {
                    etNombre.setError("Nombre requerido");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email requerido");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Contraseña requerida");
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("Mínimo 6 caracteres");
                    return;
                }

                registrarUsuario(nombre, email, password);
            }
        });

        // Navegación al Login
        txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registrarUsuario(String nombre, String email, String password) {
        // Crear usuario en Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // ¡Éxito! Ahora guardamos sus datos en Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        guardarDatosEnFirestore(user.getUid(), nombre, email);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error al registrar: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosEnFirestore(String userId, String nombre, String email) {
        // Creamos un mapa con los datos del usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("id", userId);
        usuario.put("nombre", nombre);
        usuario.put("email", email);
        // Podemos agregar campos vacíos para llenar luego en el Perfil
        usuario.put("peso", 0.0);
        usuario.put("altura", 0.0);

        // Guardamos en la colección "usuarios" usando su ID único como nombre del documento
        db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                    // Ir al Dashboard
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                });
    }
}