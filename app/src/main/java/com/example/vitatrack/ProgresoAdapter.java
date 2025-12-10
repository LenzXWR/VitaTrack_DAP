package com.example.vitatrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.Habito;

import java.util.List;

public class ProgresoAdapter extends RecyclerView.Adapter<ProgresoAdapter.ProgresoViewHolder> {

    private List<Habito> listaHabitos;

    public ProgresoAdapter(List<Habito> listaHabitos) {
        this.listaHabitos = listaHabitos;
    }

    @NonNull
    @Override
    public ProgresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progreso, parent, false);
        return new ProgresoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgresoViewHolder holder, int position) {
        Habito habito = listaHabitos.get(position);

        holder.tvNombre.setText(habito.getNombre());

        // --- CÁLCULO MATEMÁTICO ---
        int porcentaje = calcularPorcentaje(habito.getProgreso());

        // Actualizamos la barra y el texto
        holder.progressBar.setProgress(porcentaje);
        holder.tvPorcentaje.setText(porcentaje + "%");

        // --- LÓGICA MOTIVACIONAL ---
        if (porcentaje >= 100) {
            holder.tvMensaje.setVisibility(View.VISIBLE); // ¡Mostrar mensaje!
            holder.tvMensaje.setText("🎉 ¡Objetivo Cumplido! Eres imparable.");
            // Aquí podríamos cambiar el color de la barra a dorado si quisiéramos
        } else {
            holder.tvMensaje.setVisibility(View.GONE); // Ocultar si no ha terminado
        }
    }

    private int calcularPorcentaje(String progresoTexto) {
        try {
            // Formato: "2 de 8 vasos"
            String[] partes = progresoTexto.split(" ");
            double actual = Double.parseDouble(partes[0]);
            double meta = 100; // Default
            if (partes.length >= 3) meta = Double.parseDouble(partes[2]);
            if (meta == 0) return 0;

            double resultado = (actual / meta) * 100;
            return (int) Math.min(resultado, 100);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    public static class ProgresoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPorcentaje, tvMensaje;
        ProgressBar progressBar;

        public ProgresoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreHabito);
            tvPorcentaje = itemView.findViewById(R.id.tvPorcentaje);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvMensaje = itemView.findViewById(R.id.tvMensajeMotivacional);
        }
    }
}