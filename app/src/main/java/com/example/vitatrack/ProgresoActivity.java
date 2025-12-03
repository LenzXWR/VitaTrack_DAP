package com.example.vitatrack;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProgresoActivity extends AppCompatActivity {

    private ProgressBar progressAgua, progressFisica, progressSueno;
    private TextView txtValorAgua, txtValorFisica, txtValorSueno, tvRecomendacion;
    private Button btnSemanal, btnMensual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progreso);

        progressAgua = findViewById(R.id.progressAgua);
        progressFisica = findViewById(R.id.progressFisica);
        progressSueno = findViewById(R.id.progressSueno);

        txtValorAgua = findViewById(R.id.txtValorAgua);
        txtValorFisica = findViewById(R.id.txtValorFisica);
        txtValorSueno = findViewById(R.id.txtValorSueno);

        tvRecomendacion = findViewById(R.id.tvRecomendacion);

        btnSemanal = findViewById(R.id.btnSemanal);
        btnMensual = findViewById(R.id.btnMensual);


        btnSemanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatosSemanales();
            }
        });

        btnMensual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatosMensuales();
            }
        });

        mostrarDatosSemanales();
    }


    private void mostrarDatosSemanales() {
        actualizarBarras(70, 45, 60);

        tvRecomendacion.setText("Esta semana has mantenido un buen nivel de hidratación.");

        btnSemanal.setBackgroundColor(getColor(R.color.colorPrimary));
        btnMensual.setBackgroundColor(Color.GRAY);
    }

    private void mostrarDatosMensuales() {
        actualizarBarras(40, 80, 50);

        tvRecomendacion.setText("Tu promedio mensual de sueño es bajo. ¡Intenta descansar más!");

        btnSemanal.setBackgroundColor(Color.GRAY);
        btnMensual.setBackgroundColor(getColor(R.color.colorPrimary));
    }

    private void actualizarBarras(int agua, int fisica, int sueno) {
        progressAgua.setProgress(agua);
        txtValorAgua.setText(agua + "%");

        progressFisica.setProgress(fisica);
        txtValorFisica.setText(fisica + "%");

        progressSueno.setProgress(sueno);
        txtValorSueno.setText(sueno + "%");
    }
}