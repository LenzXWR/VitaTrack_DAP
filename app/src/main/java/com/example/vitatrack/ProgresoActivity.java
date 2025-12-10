package com.example.vitatrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.Habito;
import com.example.vitatrack.ui.reminders.RemindersActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProgresoActivity extends AppCompatActivity {

    private RecyclerView rvProgreso;
    private ProgresoAdapter adapter;
    private List<Habito> listaHabitos;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso);

        // 1. Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // 2. Configurar RecyclerView
        rvProgreso = findViewById(R.id.rvProgreso);
        listaHabitos = new ArrayList<>();
        adapter = new ProgresoAdapter(listaHabitos);

        rvProgreso.setLayoutManager(new LinearLayoutManager(this));
        rvProgreso.setAdapter(adapter);

        // 3. Cargar datos
        cargarDatos();

        // 4. --- LÓGICA DE LA BARRA DE NAVEGACIÓN (NUEVO) ---
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // ¡IMPORTANTE! Marcamos "Progreso" como el botón activo
        bottomNav.setSelectedItemId(R.id.nav_progreso);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_progreso) {
                return true; // Ya estamos aquí, no hacemos nada
            }
            else if (itemId == R.id.nav_inicio) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0); // Sin animación
                finish();
                return true;
            }
            else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_consejos) {
                startActivity(new Intent(getApplicationContext(), TipsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_recordatorios) {
                startActivity(new Intent(getApplicationContext(), RemindersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void cargarDatos() {
        if (userId == null) return;

        db.collection("habitos")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    listaHabitos.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Habito h = doc.toObject(Habito.class);
                        listaHabitos.add(h);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}