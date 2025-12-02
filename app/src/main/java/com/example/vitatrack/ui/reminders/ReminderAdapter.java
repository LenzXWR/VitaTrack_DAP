package com.example.vitatrack.ui.reminders;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.R;
import com.example.vitatrack.models.Reminder;
import com.example.vitatrack.storage.ReminderStorage;

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

        holder.tvHabitType.setText(r.getHabitType());
        holder.tvTimeFreq.setText(r.getTime() + " • " + r.getFrequency());
        holder.switchEnabled.setChecked(r.isEnabled());

        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            r.setEnabled(isChecked);
            ReminderStorage.saveReminders(buttonView.getContext(), reminders);
            if (toggleListener != null) toggleListener.onToggle(r, isChecked);
            holder.itemView.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar recordatorio")
                    .setMessage("¿Deseas eliminar este recordatorio?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        Reminder removed = reminders.remove(pos);
                        notifyItemRemoved(pos);
                        // guardar y cancelar alarma
                        ReminderStorage.saveReminders(v.getContext(), reminders);
                        // cancelar alarma
                        // usamos el método público del activity: no disponible aquí -> alternativa: cancelar por AlarmManager directamente
                        android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.vitatrack.notifications.AlarmReceiver.class);
                        android.app.PendingIntent pending = android.app.PendingIntent.getBroadcast(
                                v.getContext(),
                                (int) (removed.getId() % Integer.MAX_VALUE),
                                intent,
                                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
                        );
                        android.app.AlarmManager am = (android.app.AlarmManager) v.getContext().getSystemService(android.content.Context.ALARM_SERVICE);
                        if (am != null) am.cancel(pending);
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

    public static class VH extends RecyclerView.ViewHolder {

        TextView tvHabitType, tvTimeFreq;
        Switch switchEnabled;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvHabitType = itemView.findViewById(R.id.tvHabitType);
            tvTimeFreq = itemView.findViewById(R.id.tvTimeFreq);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
        }
    }
}