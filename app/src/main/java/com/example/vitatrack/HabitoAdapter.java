package com.example.vitatrack;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitatrack.Habito;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HabitoAdapter extends RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder> {

    private List<Habito> listaHabitos;
    private FirebaseFirestore db;

    public HabitoAdapter(List<Habito> listaHabitos) {
        this.listaHabitos = listaHabitos;
        this.db = FirebaseFirestore.getInstance();
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

        holder.btnSumar.setOnClickListener(v -> {
            mostrarDialogoSumar(holder.itemView.getContext(), habito);
        });
    }

    private void mostrarDialogoSumar(Context context, Habito habito) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Actualizar " + habito.getNombre());
        builder.setMessage("¿Cuánto quieres sumar a tu progreso?");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); // Permite decimales
        builder.setView(input);

        builder.setPositiveButton("Sumar", (dialog, which) -> {
            String cantidadStr = input.getText().toString();
            if (!cantidadStr.isEmpty()) {
                double cantidadASumar = Double.parseDouble(cantidadStr);
                actualizarProgresoEnFirebase(habito, cantidadASumar);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void actualizarProgresoEnFirebase(Habito habito, double cantidadASumar) {
        try {
            String[] partes = habito.getProgreso().split(" ");

            double actual = Double.parseDouble(partes[0]);

            double nuevoTotal = actual + cantidadASumar;

            String nuevoTotalStr;
            if (nuevoTotal == (long) nuevoTotal) {
                nuevoTotalStr = String.format("%d", (long) nuevoTotal);
            } else {
                nuevoTotalStr = String.format("%s", nuevoTotal);
            }

            String nuevoProgresoTexto = nuevoTotalStr + " " + obtenerRestoDelTexto(partes);

            if (habito.getIdDocumento() != null) {
                db.collection("habitos").document(habito.getIdDocumento())
                        .update("progreso", nuevoProgresoTexto);
            }

        } catch (Exception e) {
        }
    }

    private String obtenerRestoDelTexto(String[] partes) {
        StringBuilder resto = new StringBuilder();
        for (int i = 1; i < partes.length; i++) {
            resto.append(partes[i]).append(" ");
        }
        return resto.toString().trim();
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    public static class HabitoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreHabito, tvProgresoHabito;
        ImageButton btnSumar;

        public HabitoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreHabito = itemView.findViewById(R.id.tvNombreHabito);
            tvProgresoHabito = itemView.findViewById(R.id.tvProgresoHabito);
            btnSumar = itemView.findViewById(R.id.btnSumarProgreso);
        }
    }
}