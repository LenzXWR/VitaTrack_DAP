package com.example.vitatrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HabitoAdapter extends RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder> {

    private List<Habito> listaHabitos;

    public HabitoAdapter(List<Habito> listaHabitos) {
        this.listaHabitos = listaHabitos;
    }

    @NonNull
    @Override
    public HabitoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_habit, parent, false);
        return new HabitoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitoViewHolder holder, int position) {
        Habito habito = listaHabitos.get(position);
        holder.tvNombreHabito.setText(habito.getNombre());
        holder.tvProgresoHabito.setText(habito.getProgreso());
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    public static class HabitoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreHabito;
        TextView tvProgresoHabito;

        public HabitoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreHabito = itemView.findViewById(R.id.tvNombreHabito);
            tvProgresoHabito = itemView.findViewById(R.id.tvProgresoHabito);
        }
    }
}