package com.example.vitatrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usar el layout que tiene el ImageView centrado
        setContentView(R.layout.activity_splash);

        // Opcional: animación de zoom in
        ImageView logo = findViewById(R.id.logoImageView);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        logo.startAnimation(zoomIn);

        // Abrir LoginActivity después del splash
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // <- quita la animación al abrir LoginActivity
            finish(); // Cierra el splash
        }, SPLASH_DURATION);
    }
}
