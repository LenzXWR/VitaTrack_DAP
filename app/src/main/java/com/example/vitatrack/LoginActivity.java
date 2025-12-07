package com.example.vitatrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;  // Instancia de Firestore

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Verificar si el usuario ya está logueado
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);  // El valor por defecto es false

        if (isLoggedIn) {
            // Si ya está logueado, redirigir a MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Cerrar esta actividad para que no se pueda volver a ella
            return;  // Asegúrate de salir del metodo para evitar que el resto del código se ejecute
        }

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener las vistas de los campos de correo y contraseña
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);

        // Botón de inicio de sesión
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String password = edtPass.getText().toString();

            // Validar si los campos están vacíos
            if (email.isEmpty() || password.isEmpty()) {
                edtEmail.setError("Por favor ingresa tu correo");
                edtPass.setError("Por favor ingresa tu contraseña");
                return;
            }

            // Deshabilitar todos los campos y botones mientras se realiza el inicio de sesión
            disableUI(true);

            // Iniciar sesión
            loginUser(email, password);
        });

        // Registro
        TextView btnRegister = findViewById(R.id.txtRegister);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Texto
        TextView txtRegister = findViewById(R.id.txtRegister);
        String fullText = "¿No tienes cuenta? Regístrate aquí";
        SpannableString spannableString = new SpannableString(fullText);

        // Resaltar "Regístrate aquí" en negrita
        int start = fullText.indexOf("Regístrate aquí");
        int end = start + "Regístrate aquí".length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Establecer el texto modificado
        txtRegister.setText(spannableString);
    }

    // Metodo para deshabilitar y habilitar los elementos de la UI
    private void disableUI(boolean disable) {
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtRegister = findViewById(R.id.txtRegister);

        // Deshabilitar los botones y campos de texto durante el inicio de sesión
        btnLogin.setEnabled(!disable);
        edtEmail.setEnabled(!disable);
        edtPass.setEnabled(!disable);
        txtRegister.setEnabled(!disable);
    }

    // Metodo para autenticar al usuario con Firebase
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Rehabilitar los botones y ocultar el ProgressBar
                    disableUI(false);

                    if (task.isSuccessful()) {
                        // Si la autenticación es exitosa, obtener el usuario
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();  // Obtener el UID del usuario autenticado

                            // Guardar en SharedPreferences que el usuario está logueado
                            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isLoggedIn", true);  // Marcamos que el usuario está logueado
                            editor.apply();  // Guardamos los cambios

                            // Recuperar los datos del usuario desde Firestore
                            db.collection("usuarios")
                                    .document(userId)  // Usamos el UID como ID del documento
                                    .get()  // Obtener el documento
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // Recuperar el nombre del usuario de Firestore
                                            String name = documentSnapshot.getString("name");

                                            // Mostrar un Toast con el nombre del usuario
                                            if (name != null) {
                                                Toast.makeText(LoginActivity.this, "Bienvenido, " + name + "!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "No se encontró el nombre del usuario", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Si no se encuentran los datos del usuario en Firestore
                                            Toast.makeText(LoginActivity.this, "No se encontraron los datos del usuario", Toast.LENGTH_SHORT).show();
                                        }

                                        // Navegar a la MainActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Manejar errores al obtener el documento
                                        Toast.makeText(LoginActivity.this, "Error al obtener los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            // Si el usuario es nulo, mostrar un error
                            Toast.makeText(LoginActivity.this, "Error: usuario no encontrado", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Si la autenticación falla, muestra un Toast
                        Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
