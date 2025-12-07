package com.example.vitatrack.ui.reminders;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.R;
import com.example.vitatrack.models.Reminder;
import com.example.vitatrack.notifications.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.VH> {

    public interface OnToggleListener {
        void onToggle(Reminder reminder, boolean enabled);
    }

    private List<Reminder> reminders;
    private OnToggleListener toggleListener;

    public ReminderAdapter(List<Reminder> reminders, OnToggleListener listener) {
        this.reminders = reminders;
        this.toggleListener = listener;
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
        final Reminder r = reminders.get(position);

        holder.tvHabit.setText(r.getHabitType());
        holder.tvTime.setText(r.getTime() + " • " + r.getFrequency());
        holder.switchEnabled.setChecked(r.isEnabled());

        // Cambiar el estado del recordatorio (habilitar/deshabilitar)
        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            r.setEnabled(isChecked);

            // Actualizar Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("usuarios")
                    .document(userId)
                    .collection("recordatorios")
                    .document(String.valueOf(r.getId()))  // Usa el ID único para actualizar el recordatorio específico
                    .update("enabled", isChecked)
                    .addOnSuccessListener(aVoid -> {
                        // Actualización exitosa en Firestore
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error en caso de falla
                    });

            // Notificar al listener
            if (toggleListener != null) toggleListener.onToggle(r, isChecked);

            // Cambiar la opacidad del item
            holder.itemView.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        // Eliminar recordatorio al hacer largo clic
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar recordatorio")
                    .setMessage("¿Deseas eliminar este recordatorio?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        Reminder removed = reminders.remove(pos);
                        notifyItemRemoved(pos);

                        // Eliminar de Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        db.collection("usuarios")
                                .document(userId)
                                .collection("recordatorios")
                                .document(String.valueOf(removed.getId()))
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // El recordatorio se eliminó exitosamente de Firestore
                                })
                                .addOnFailureListener(e -> {
                                    // Manejar error si la eliminación de Firestore falla
                                });

                        // Cancelar la alarma
                        cancelAlarm(v.getContext(), removed);
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    private void cancelAlarm(Context context, Reminder removed) {
        // Cancelar la alarma usando AlarmManager
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) (removed.getId() % Integer.MAX_VALUE),  // Asegurarse de que el ID sea único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(pendingIntent);  // Cancelar la alarma
        }
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
