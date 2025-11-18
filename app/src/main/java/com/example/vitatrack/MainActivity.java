package com.example.vitatrack;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewHabitos;
    HabitoAdapter adapter;
    List<Habito> listaDeHabitos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewHabitos = findViewById(R.id.RecicleViewHabito);

        // tus ejemplos
        listaDeHabitos = new ArrayList<>();
        listaDeHabitos.add(new Habito("Consumo de Agua", "2 de 8 vasos"));
        listaDeHabitos.add(new Habito("Actividad Física", "30 de 60 minutos"));
        listaDeHabitos.add(new Habito("Horas de Sueño", "6 de 8 horas"));

        adapter = new HabitoAdapter(listaDeHabitos);
        recyclerViewHabitos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHabitos.setAdapter(adapter);

        // FAB correcto
        FloatingActionButton fabAddHabit = findViewById(R.id.fabAddHabit);
        fabAddHabit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            startActivity(intent);
        });

        // navegación inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_inicio);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_progreso) {
                startActivity(new Intent(MainActivity.this, ProgresoActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_recordatorios) {
                return true;
            }
            return true;
        });
    }
}
