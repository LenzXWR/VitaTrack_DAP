package com.example.vitatrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vitatrack.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.VH> {

    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Reminder reminder = reminders.get(position);

        holder.tvHabit.setText(reminder.getHabitType());
        holder.tvTime.setText(reminder.getTime());
        holder.tvFrequency.setText(reminder.getFrequency());
        holder.switchEnabled.setChecked(reminder.isEnabled());

        // Cuando el switch cambie, actualizamos la base de datos
        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.setEnabled(isChecked);
            updateReminderInFirestore(reminder);
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    // Metodo para actualizar el recordatorio en Firestore
    private void updateReminderInFirestore(Reminder reminder) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(userId)
                .collection("recordatorios")
                .document(String.valueOf(reminder.getId())) // Usamos el ID del recordatorio
                .set(reminder)  // Esto actualizará los datos en Firestore
                .addOnSuccessListener(aVoid -> {
                    // Se puede añadir un Toast o algún feedback para el usuario si es necesario
                })
                .addOnFailureListener(e -> {
                    // Manejar error si la actualización falla
                });
    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView tvHabit, tvTime, tvFrequency;
        Switch switchEnabled;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvHabit = itemView.findViewById(R.id.tvHabit);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvFrequency = itemView.findViewById(R.id.tvFrequency);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
        }
    }
}
