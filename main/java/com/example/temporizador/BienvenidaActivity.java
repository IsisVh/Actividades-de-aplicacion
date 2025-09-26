package com.example.temporizador;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BienvenidaActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvTitle, tvSubtitle;
    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitybienvenida);

        // Referencias UI
        ivLogo = findViewById(R.id.ivLogoBienvenida);
        tvTitle = findViewById(R.id.tvTitleBienvenida);
        tvSubtitle = findViewById(R.id.tvSubtitleBienvenida);
        btnContinuar = findViewById(R.id.btnContinuar);

        // Cargar animaciones (de res/anim/)
        Animation animZoom   = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        Animation animSlide  = AnimationUtils.loadAnimation(this, R.anim.slideup);
        Animation animBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Aplicar animaciones
        ivLogo.startAnimation(animZoom);

        // Pequeño desfase para que el texto entre después del logo (opcional)
        new Handler().postDelayed(() -> {
            tvTitle.startAnimation(animSlide);
            tvSubtitle.startAnimation(animSlide);
        }, 250);

        // Retrasa la animación del botón para un efecto más elegante (opcional)
        new Handler().postDelayed(() -> btnContinuar.startAnimation(animBounce), 500);

        // Acción del botón: ir a MainActivity
        btnContinuar.setOnClickListener(v -> {
            Intent i = new Intent(BienvenidaActivity.this, MainActivity.class);
            startActivity(i);
            // Transición suave entre actividades (opcional)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
}
