package com.example.vitatrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vitatrack.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class RemindersActivity extends AppCompatActivity {

    private RecyclerView rvReminders;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminders = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        rvReminders = findViewById(R.id.rvReminders);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Configurar RecyclerView
        reminderAdapter = new ReminderAdapter(reminders);
        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        rvReminders.setAdapter(reminderAdapter);

        // Cargar recordatorios de Firestore
        loadReminders();

        // Botón flotante para agregar recordatorio
        findViewById(R.id.fabAddReminder).setOnClickListener(v -> {
            // Navegar a la pantalla de crear recordatorio
            Intent intent = new Intent(RemindersActivity.this, CreateReminderActivity.class);
            startActivity(intent);
        });

        // Configurar el SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadReminders(); // Recargar recordatorios al hacer swipe
            swipeRefreshLayout.setRefreshing(false); // Detener el refresco una vez cargados los datos
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders(); // Recargar recordatorios cuando se regresa a esta actividad
    }

    private void loadReminders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(userId)
                .collection("recordatorios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        reminders.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Reminder reminder = document.toObject(Reminder.class);
                            if (reminder != null) {
                                reminders.add(reminder);
                            }
                        }
                        reminderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(RemindersActivity.this, "No tienes recordatorios", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RemindersActivity.this, "Error al cargar recordatorios", Toast.LENGTH_SHORT).show();
                });
    }
}
