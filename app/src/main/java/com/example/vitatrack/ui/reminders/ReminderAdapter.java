package com.example.vitatrack.ui.reminders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.R;
import com.example.vitatrack.models.Reminder;

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
        Reminder r = reminders.get(position);

        holder.tvHabitType.setText(r.getHabitType());
        holder.tvTimeFreq.setText(r.getTime() + " • " + r.getFrequency());
        holder.switchEnabled.setChecked(r.isEnabled());

        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            r.setEnabled(isChecked);
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