package com.example.vitatrack.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.vitatrack.models.Reminder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReminderStorage {

    private static final String PREF_NAME = "reminders_pref"; // Nombre del archivo de preferencias
    private static final String KEY_DATA = "reminders_data";  // Clave para almacenar los recordatorios

    // Método para guardar los recordatorios en SharedPreferences
    public static void saveReminders(Context ctx, List<Reminder> reminders) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convertir la lista de recordatorios a JSON usando Gson
        Gson gson = new Gson();
        String json = gson.toJson(reminders);

        // Guardar el JSON en SharedPreferences
        editor.putString(KEY_DATA, json);
        editor.apply(); // Guardar de forma asíncrona
    }

    // Método para cargar los recordatorios desde SharedPreferences
    public static List<Reminder> loadReminders(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Obtener el JSON guardado
        String json = sharedPreferences.getString(KEY_DATA, null);

        if (json == null) {
            return new ArrayList<>();  // Si no hay datos, devolver una lista vacía
        }

        // Convertir el JSON de vuelta a la lista de recordatorios
        Gson gson = new Gson();
        Type type = new TypeToken<List<Reminder>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
