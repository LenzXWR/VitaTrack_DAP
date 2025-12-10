package com.example.vitatrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.ui.reminders.RemindersActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewHabitos;
    HabitoAdapter adapter;
    List<Habito> listaDeHabitos;
    FirebaseFirestore db;

    TextView tvWelcomeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String nombre = user.getDisplayName();
            if (nombre != null && !nombre.isEmpty()) {
                tvWelcomeName.setText("Hola, " + nombre + " 👋");
            } else {
                tvWelcomeName.setText("Hola, Usuario 👋");
            }
        }

        recyclerViewHabitos = findViewById(R.id.RecicleViewHabito);
        listaDeHabitos = new ArrayList<>();
        adapter = new HabitoAdapter(listaDeHabitos);
        recyclerViewHabitos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHabitos.setAdapter(adapter);

        escucharHabitosEnTiempoReal();

        FloatingActionButton fabAddHabit = findViewById(R.id.fab_add_habit);
        fabAddHabit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_inicio);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_inicio) {
                return true;
            }
            else if (itemId == R.id.nav_progreso) {
                startActivity(new Intent(this, ProgresoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_consejos) {
                startActivity(new Intent(this, TipsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_recordatorios) {
                startActivity(new Intent(this, RemindersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void escucharHabitosEnTiempoReal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String myUserId = user.getUid();

        db.collection("habitos")
                .whereEqualTo("userId", myUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error al escuchar", error);
                        return;
                    }

                    listaDeHabitos.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        try {
                            Habito habito = doc.toObject(Habito.class);
                            habito.setIdDocumento(doc.getId());
                            listaDeHabitos.add(habito);
                        } catch (Exception e) {
                            Log.e("FirestoreError", "Error al convertir", e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}