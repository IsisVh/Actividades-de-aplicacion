package com.example.temporizador;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etMinutes, etSeconds;
    private TextView tvTimer, tvMsg;
    private Button btnStartPause, btnReset, btnShare;
    private ImageView ivHeader;   // 👈 Agregamos la referencia a la imagen

    private CountDownTimer timer;
    private boolean running = false;
    private long remainingMillis = 0L;
    private long totalMillis = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMinutes = findViewById(R.id.etMinutes);
        etSeconds = findViewById(R.id.etSeconds);
        tvTimer   = findViewById(R.id.tvTimer);
        tvMsg     = findViewById(R.id.tvMsg);

        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset      = findViewById(R.id.btnReset);
        btnShare      = findViewById(R.id.btnShare);
        ivHeader      = findViewById(R.id.ivHeader); // 👈 enlazamos la ImageView

        // Intentar cargar imagen personalizada, si no usar ícono por defecto
        try {
            ivHeader.setImageResource(R.drawable.imag);
        } catch (Exception e) {
            ivHeader.setImageResource(R.mipmap.ic_launcher_round);
        }

        btnStartPause.setOnClickListener(v -> onStartPause());
        btnReset.setOnClickListener(v -> onReset());

        // INTENT IMPLÍCITO: Compartir texto
        btnShare.setOnClickListener(v -> {
            String texto = "Tiempo actual: " + tvTimer.getText();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, texto);
            startActivity(Intent.createChooser(share, "Compartir tiempo"));
            pulse(v);
        });
    }

    /** Iniciar / Pausar con validación + animación + intent implícito (set timer del sistema) */
    private void onStartPause() {
        if (!running) {
            int[] mmss = getValidatedTimeOrShowErrors();
            if (mmss == null) return;

            if (remainingMillis == 0L) {
                totalMillis = (mmss[0] * 60L + mmss[1]) * 1000L;
                remainingMillis = totalMillis;

                // Opcional: crear temporizador del sistema
                Intent sysTimer = new Intent(AlarmClock.ACTION_SET_TIMER)
                        .putExtra(AlarmClock.EXTRA_LENGTH, (mmss[0] * 60 + mmss[1]))
                        .putExtra(AlarmClock.EXTRA_MESSAGE, "Sesión de temporizador");
                if (sysTimer.resolveActivity(getPackageManager()) != null) {
                    startActivity(sysTimer);
                }
            }

            startTimer(remainingMillis);
            btnStartPause.setText("Pausar");
            bounce(btnStartPause);
        } else {
            stopTimer();
            btnStartPause.setText("Reanudar");
            pulse(btnStartPause);
        }
    }

    private void onReset() {
        stopTimer();
        remainingMillis = 0L;
        totalMillis = 0L;
        tvTimer.setText("00:00");
        btnStartPause.setText("Iniciar / Pausar");
        pulse(btnReset);
    }

    /** Validaciones robustas */
    private @Nullable int[] getValidatedTimeOrShowErrors() {
        try {
            String sm = etMinutes.getText().toString().trim();
            String ss = etSeconds.getText().toString().trim();
            if (TextUtils.isEmpty(sm) && TextUtils.isEmpty(ss)) {
                etMinutes.setError("Requerido");
                etSeconds.setError("Requerido");
                tvMsg.setText("Ingresa minutos y/o segundos");
                return null;
            }
            int m = sm.isEmpty() ? 0 : Integer.parseInt(sm);
            int s = ss.isEmpty() ? 0 : Integer.parseInt(ss);

            if (m < 0 || m > 999) { etMinutes.setError("0–999"); return null; }
            if (s < 0 || s > 59)  { etSeconds.setError("0–59"); return null; }
            if (m == 0 && s == 0) { etSeconds.setError("Debe ser > 0"); return null; }

            tvMsg.setText("");
            return new int[]{m, s};
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Solo números válidos", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void startTimer(long millis) {
        stopTimer();
        running = true;
        timer = new CountDownTimer(millis, 1000) {
            @Override public void onTick(long msLeft) {
                remainingMillis = msLeft;
                int totalSec = (int) (msLeft / 1000);
                int m = totalSec / 60;
                int s = totalSec % 60;
                tvTimer.setText(String.format("%02d:%02d", m, s));

                // Animación en cada tick
                tvTimer.setScaleX(0.98f);
                tvTimer.setScaleY(0.98f);
                tvTimer.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            @Override public void onFinish() {
                running = false;
                remainingMillis = 0L;
                tvTimer.setText("00:00");
                btnStartPause.setText("Iniciar / Pausar");

                // Animación al finalizar
                tvTimer.animate().alpha(0.2f).setDuration(250).withEndAction(() ->
                        tvTimer.animate().alpha(1f).setDuration(250)
                ).start();
                Toast.makeText(MainActivity.this, "¡Tiempo finalizado!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        running = false;
    }

    /** Animaciones simples */
    private void pulse(View v) {
        v.animate().alpha(0.6f).setDuration(80).withEndAction(() ->
                v.animate().alpha(1f).setDuration(80)
        ).start();
    }
    private void bounce(View v) {
        v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(120).withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(120)
        ).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
