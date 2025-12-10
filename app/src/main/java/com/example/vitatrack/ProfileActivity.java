package com.example.vitatrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private TextInputEditText etPeso, etAltura;
    private Button btnUpdateProfile, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quitamos EdgeToEdge para que se vea bien la barra superior
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            irAlLogin();
            return;
        }
        userId = user.getUid();

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);

        btnUpdateProfile = findViewById(R.id.btnActualizar);
        btnLogout = findViewById(R.id.btnLogout);

        tvUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Usuario");
        tvUserEmail.setText(user.getEmail());

        cargarDatosDelPerfil();

        contarHabitosCompletados();

        btnUpdateProfile.setOnClickListener(v -> guardarCambios());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            irAlLogin();
        });

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_perfil);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_perfil) {
                return true;
            } else if (itemId == R.id.nav_inicio) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_progreso) {
                startActivity(new Intent(getApplicationContext(), ProgresoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_consejos) {
                startActivity(new Intent(getApplicationContext(), TipsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_recordatorios) {
                startActivity(new Intent(getApplicationContext(), com.example.vitatrack.ui.reminders.RemindersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void cargarDatosDelPerfil() {
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double peso = documentSnapshot.getDouble("peso");
                        Double altura = documentSnapshot.getDouble("altura");

                        Long completados = documentSnapshot.getLong("habitosCompletados");

                        if (peso != null) etPeso.setText(String.valueOf(peso));
                        if (altura != null) etAltura.setText(String.valueOf(altura));
                    }
                });
    }

    private void guardarCambios() {
        String pesoStr = etPeso.getText().toString();
        String alturaStr = etAltura.getText().toString();

        if (TextUtils.isEmpty(pesoStr) || TextUtils.isEmpty(alturaStr)) {
            Toast.makeText(this, "Ingresa peso y altura", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("peso", Double.parseDouble(pesoStr));
        updates.put("altura", Double.parseDouble(alturaStr));

        db.collection("usuarios").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
    }

    private void contarHabitosCompletados() {
        db.collection("habitos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    int completados = 0;
                    for (QueryDocumentSnapshot doc : query) {
                        String prog = doc.getString("progreso");
                        if (prog != null && esHabitoCompletado(prog)) {
                            completados++;
                        }
                    }

                    if (completados > 0) {
                        db.collection("usuarios").document(userId)
                                .update("habitosCompletados", completados);

                        Toast.makeText(this, "Llevas " + completados + " hábitos completados al 100%", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean esHabitoCompletado(String progreso) {
        try {
            String[] p = progreso.split(" ");
            double act = Double.parseDouble(p[0]);
            double meta = Double.parseDouble(p[2]);
            return act >= meta;
        } catch (Exception e) {
            return false;
        }
    }

    private void irAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}