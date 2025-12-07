package com.example.vitatrack;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPass, edtConfirmPass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;  // Instancia de Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();  // Inicializar Firestore

        // Inicializar los campos de entrada
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            // Obtener los datos del formulario
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPass.getText().toString().trim();
            String confirmPassword = edtConfirmPass.getText().toString().trim();

            // Validar los campos
            if (name.isEmpty()) {
                edtName.setError("Por favor ingresa tu nombre");
                edtName.requestFocus();
                return;
            }
            if (email.isEmpty() || !email.contains("@")) {
                edtEmail.setError("Por favor ingresa un correo válido");
                edtEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                edtPass.setError("Por favor ingresa una contraseña");
                edtPass.requestFocus();
                return;
            }
            if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                edtConfirmPass.setError("Las contraseñas no coinciden");
                edtConfirmPass.requestFocus();
                return;
            }

            // Registrarse con Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Si el registro es exitoso
                            String userId = mAuth.getCurrentUser().getUid();  // Obtener el UID del usuario

                            // Crear un objeto con los datos adicionales del usuario
                            User user = new User(name, email);  // Usamos una clase User que tiene los campos 'name' y 'email'

                            // Guardar el nombre del usuario en Firestore
                            db.collection("usuarios")  // Accedemos a la colección 'usuarios'
                                    .document(userId)  // Usamos el UID del usuario como ID del documento
                                    .set(user)  // Almacenamos los datos
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();

                                        // Navegar a la MainActivity
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            // Si el registro falla, verificar el error específico
                            String errorMessage = task.getException().getMessage();

                            // Verificar si el error es por correo ya en uso
                            if (errorMessage != null && errorMessage.contains("The email address is already in use")) {
                                // El correo ya está registrado, le indicamos al usuario que inicie sesión
                                Toast.makeText(RegisterActivity.this, "Este correo ya está registrado. Inicia sesión.", Toast.LENGTH_LONG).show();
                            } else {
                                // Si es otro tipo de error, mostrar el mensaje genérico
                                Toast.makeText(RegisterActivity.this, "Error en el registro: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        // Ir a LoginActivity
        TextView btnLogin = findViewById(R.id.txtLogin);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Texto de "Inicia sesión" con negrita
        TextView txtLogin = findViewById(R.id.txtLogin);
        String fullText = "¿Ya tienes cuenta? Inicia sesión";
        SpannableString spannableString = new SpannableString(fullText);

        // Resaltar "Inicia sesión" en negrita
        int start = fullText.indexOf("Inicia sesión");
        int end = start + "Inicia sesión".length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Establecer el texto modificado en el TextView
        txtLogin.setText(spannableString);
    }
}

// Clase para representar al usuario
class User {
    private String name;
    private String email;

    // Constructor
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
