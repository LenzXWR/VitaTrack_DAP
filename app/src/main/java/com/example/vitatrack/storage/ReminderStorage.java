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

    private static final String PREF_NAME = "reminders_pref";
    private static final String KEY_DATA = "reminders_data";

    public static void saveReminders(Context ctx, List<Reminder> list) {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor.putString(KEY_DATA, json);
        editor.apply();
    }

    public static List<Reminder> loadReminders(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = pref.getString(KEY_DATA, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Reminder>>() {}.getType();
        return gson.fromJson(json, type);
    }
}