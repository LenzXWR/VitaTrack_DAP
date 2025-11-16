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

        //estos son ejemplos ya que el inge dijo que aun no estara conectada a una base de datos

        listaDeHabitos = new ArrayList<>();
        listaDeHabitos.add(new Habito("Consumo de Agua", "2 de 8 vasos"));
        listaDeHabitos.add(new Habito("Actividad Física", "30 de 60 minutos"));
        listaDeHabitos.add(new Habito("Horas de Sueño", "6 de 8 horas"));

        adapter = new HabitoAdapter(listaDeHabitos);

        recyclerViewHabitos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHabitos.setAdapter(adapter);



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnItemSelectedListener(item -> {int itemId = item.getItemId();

            if (itemId == R.id.nav_progreso) {
                Intent progresoIntent = new Intent(MainActivity.this, ProgresoActivity.class);
                startActivity(progresoIntent);
                return true;

            } else if (itemId == R.id.nav_recordatorios) {
                // Navegar a la Activity de Recordatorios (tarea de Jhan)
                return true;

            } else if (itemId == R.id.nav_inicio) {
                return true;
            }

            return false;
        });
    }
    }
