package com.example.vitatrack;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vitatrack.models.Reminder;
import com.example.vitatrack.notifications.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateReminderActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private Spinner spinnerHabit, spinnerFrequency;
    private TimePicker timePicker;
    private String selectedTime = "08:00"; // Default time
    private List<String> habitsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        // Inicializar las vistas
        spinnerHabit = findViewById(R.id.spinnerHabit);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        timePicker = findViewById(R.id.timePicker);
        Button btnSave = findViewById(R.id.btnSaveReminder);

        // Configurar el TimePicker para usar formato AM/PM
        timePicker.setIs24HourView(false);

        // Configurar el Spinner de frecuencia
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(
                this, R.array.freq_array, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(freqAdapter);

        // Cargar los hábitos personalizados desde Firestore
        loadUserHabits();

        // Acción para guardar el recordatorio
        btnSave.setOnClickListener(v -> {
            // Validaciones
            if (spinnerHabit.getSelectedItem() == null) {
                Toast.makeText(this, "Seleccione un hábito", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener hora seleccionada
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            selectedTime = String.format("%02d:%02d", hour, minute);

            if (spinnerFrequency.getSelectedItem() == null) {
                Toast.makeText(this, "Seleccione la frecuencia", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el objeto Reminder
            String habitType = spinnerHabit.getSelectedItem().toString();
            String frequency = spinnerFrequency.getSelectedItem().toString();
            Reminder reminder = new Reminder(System.currentTimeMillis(), habitType, selectedTime, frequency, true);

            // Guardar el recordatorio en Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("usuarios")
                    .document(userId)
                    .collection("recordatorios") // Cambié a "recordatorios"
                    .document(String.valueOf(reminder.getId()))
                    .set(reminder)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CreateReminderActivity.this, "Recordatorio guardado", Toast.LENGTH_SHORT).show();
                        scheduleReminderAlarm(reminder); // Programar la alarma
                        finish(); // Volver a la actividad anterior
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateReminderActivity.this, "Error al guardar recordatorio", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // Metodo para cargar los hábitos del usuario desde Firestore
    private void loadUserHabits() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .collection("habitos")  // Suponiendo que los hábitos del usuario están en "habitos"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    habitsList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String habit = document.getString("nombre"); // Asumiendo que el campo se llama "nombre"
                        habitsList.add(habit);
                    }

                    // Actualizar el Spinner con los hábitos del usuario
                    ArrayAdapter<String> habitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, habitsList);
                    habitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerHabit.setAdapter(habitAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateReminderActivity.this, "Error al cargar los hábitos", Toast.LENGTH_SHORT).show();
                });
    }

    // Metodo para programar la alarma con AlarmManager
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleReminderAlarm(Reminder reminder) {
        // Verificar si el permiso para alarmas exactas está otorgado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isExactAlarmPermissionGranted()) {
            // Solicitar permiso
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.SCHEDULE_EXACT_ALARM
            }, REQUEST_PERMISSION_CODE);
            return;
        }

        // Convertir la hora seleccionada a un valor en milisegundos desde la medianoche
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Si la hora ya pasó hoy, programar para mañana
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Crear el intent para el AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("habit", reminder.getHabitType());
        intent.putExtra("time", reminder.getTime());
        intent.putExtra("id", reminder.getId());

        // Crear el PendingIntent para activar el AlarmReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) (reminder.getId() % Integer.MAX_VALUE), // Usamos un ID único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

// Programar la alarma con AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarma programada para las " + reminder.getTime(), Toast.LENGTH_SHORT).show();
        }
    }

    // Verificar si el permiso para alarmas exactas está otorgado
    private boolean isExactAlarmPermissionGranted() {
        // Para Android 12 (API 31) y versiones superiores, verificamos el permiso de alarmas exactas
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED;
    }

    // Lógica para manejar el resultado de la solicitud de permisos en tiempo de ejecución
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, programar la alarma
                Reminder reminder = new Reminder(System.currentTimeMillis(), spinnerHabit.getSelectedItem().toString(),
                        selectedTime, spinnerFrequency.getSelectedItem().toString(), true);
                scheduleReminderAlarm(reminder);
            } else {
                Toast.makeText(this, "Permiso necesario para programar alarmas exactas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
