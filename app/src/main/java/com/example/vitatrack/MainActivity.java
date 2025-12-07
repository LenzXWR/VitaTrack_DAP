package com.example.vitatrack;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import android.widget.Button;
import android.content.SharedPreferences;



public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewHabitos;
    HabitoAdapter adapter;
    List<Habito> listaDeHabitos;

    private static final int REQUEST_CODE_ADD_HABIT = 1; // Código de solicitud para AddHabitActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar las vistas
        recyclerViewHabitos = findViewById(R.id.RecicleViewHabito);
        FloatingActionButton fabAddHabit = findViewById(R.id.fab_add_habit);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        TextView welcomeText = findViewById(R.id.welcomeText);

        // Lista de hábitos y adaptador
        listaDeHabitos = new ArrayList<>();
        adapter = new HabitoAdapter(listaDeHabitos);
        recyclerViewHabitos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHabitos.setAdapter(adapter);

        // Obtener el usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Si hay un usuario autenticado, obtener sus datos
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener los datos del usuario desde Firestore
            db.collection("usuarios")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtener el nombre del usuario
                            String userName = documentSnapshot.getString("name");
                            // Si el nombre es nulo o vacío, mostrar un mensaje genérico
                            if (userName == null || userName.isEmpty()) {
                                welcomeText.setText("Hola, ... 👋");
                            } else {
                                welcomeText.setText("Hola, " + userName + " 👋");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                    });

            // Cargar los hábitos del usuario desde Firestore
            loadUserHabits(userId);
        } else {
            // Si el usuario no está autenticado, redirigir a la pantalla de login
            Toast.makeText(this, "No estás autenticado. Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
        }

        // Acción para agregar un nuevo hábito
        fabAddHabit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_HABIT);
        });

        // Configuración del BottomNavigationView
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_progreso) {
                startActivity(new Intent(MainActivity.this, ProgresoActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_recordatorios) {
                Intent intent = new Intent(MainActivity.this, RemindersActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });

        Button btnCerrarSesion = findViewById(R.id.btn_cerrarsesion);

        // Acción para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            // Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // Limpiar el estado de la sesión
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();  // Guardamos los cambios

            // Redirigir al login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Finalizamos la actividad actual para que no se pueda volver
        });
    }

    /**
     * Cargar los hábitos del usuario desde Firestore.
     * @param userId ID del usuario actual.
     */
    private void loadUserHabits(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(userId)
                .collection("habitos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Habito> habitos = queryDocumentSnapshots.toObjects(Habito.class);
                        listaDeHabitos.clear();
                        listaDeHabitos.addAll(habitos);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "No tienes hábitos registrados", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al cargar los hábitos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_HABIT && resultCode == RESULT_OK) {
            // Si el hábito se guardó correctamente, volver a cargar los hábitos
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                loadUserHabits(currentUser.getUid());
            }
        }
    }
}
