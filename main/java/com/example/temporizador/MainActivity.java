package com.example.temporizador;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ===== Tabs / Secciones =====
    private Button btnTabStopwatch, btnTabTimer;
    private LinearLayout boxStopwatch, boxTimer;

    // ===== Cronómetro =====
    private TextView tvStopwatch;
    private Button btnSwStartPause, btnSwReset;
    private Handler swHandler = new Handler();
    private long swStartTime = 0L;
    private long swTimeInMillis = 0L;
    private long swAccumulated = 0L; // buffer al pausar
    private boolean swRunning = false;

    private final Runnable swTick = new Runnable() {
        @Override
        public void run() {
            swTimeInMillis = System.currentTimeMillis() - swStartTime;
            long total = swAccumulated + swTimeInMillis;

            int secs = (int) (total / 1000);
            int mins = secs / 60;
            int hrs  = mins / 60;
            secs = secs % 60;
            mins = mins % 60;

            tvStopwatch.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs));
            swHandler.postDelayed(this, 1000);
        }
    };

    // ===== Temporizador =====
    private TextView tvTimer, tvHistory, tvState;
    private EditText etMinutes, etSeconds;
    private Button btnTmrStartPause, btnTmrReset, btnAddPreset;
    private CountDownTimer countDownTimer;
    private boolean tmrRunning = false;
    private long tmrMillisLeft = 0L;

    // ===== Presets (2) Temporizadores múltiples =====
    private ListView lvPresets;
    private ArrayList<String> presetList = new ArrayList<>();
    private ArrayAdapter<String> presetAdapter;

    // ===== Historial (4) Sesiones completadas por día =====
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "timer_prefs";

    // ===== Colores (3) Tema según estado =====
    private static final int COLOR_IDLE = Color.parseColor("#FFFFFF");  // blanco
    private static final int COLOR_RUNNING = Color.parseColor("#E8F5E9"); // verde claro
    private static final int COLOR_PAUSED = Color.parseColor("#FFFDE7");  // amarillo claro
    private static final int COLOR_FINISHED = Color.parseColor("#FFEBEE"); // rojo claro

    private void applyTimerState(String state) {
        int bg = COLOR_IDLE;
        String label = "Estado: Inactivo";
        switch (state) {
            case "running":
                bg = COLOR_RUNNING;
                label = "Estado: Ejecutando";
                break;
            case "paused":
                bg = COLOR_PAUSED;
                label = "Estado: Pausado";
                break;
            case "finished":
                bg = COLOR_FINISHED;
                label = "Estado: Terminado";
                break;
            default:
                // idle
                break;
        }
        boxTimer.setBackgroundColor(bg);
        tvState.setText(label);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        bindViews();
        setupTabs();
        setupStopwatch();
        setupTimer();
        setupPresets();      // (2)
        updateHistoryLabel(); // (4)
        applyTimerState("idle"); // (3)
    }

    private void bindViews() {
        btnTabStopwatch = findViewById(R.id.btnTabStopwatch);
        btnTabTimer     = findViewById(R.id.btnTabTimer);
        boxStopwatch    = findViewById(R.id.boxStopwatch);
        boxTimer        = findViewById(R.id.boxTimer);

        // Cronómetro
        tvStopwatch     = findViewById(R.id.tvStopwatch);
        btnSwStartPause = findViewById(R.id.btnSwStartPause);
        btnSwReset      = findViewById(R.id.btnSwReset);

        // Temporizador
        tvTimer         = findViewById(R.id.tvTimer);
        tvHistory       = findViewById(R.id.tvHistory); // (4)
        tvState         = findViewById(R.id.tvState);   // (3)
        etMinutes       = findViewById(R.id.etMinutes);
        etSeconds       = findViewById(R.id.etSeconds);
        btnTmrStartPause= findViewById(R.id.btnTmrStartPause);
        btnTmrReset     = findViewById(R.id.btnTmrReset);

        // Presets (2)
        btnAddPreset    = findViewById(R.id.btnAddPreset);
        lvPresets       = findViewById(R.id.lvPresets);
    }

    private void setupTabs() {
        btnTabStopwatch.setOnClickListener(v -> showStopwatch());
        btnTabTimer.setOnClickListener(v -> showTimer());
        showStopwatch(); // default
    }

    private void showStopwatch() {
        boxStopwatch.setVisibility(LinearLayout.VISIBLE);
        boxTimer.setVisibility(LinearLayout.GONE);
    }

    private void showTimer() {
        boxStopwatch.setVisibility(LinearLayout.GONE);
        boxTimer.setVisibility(LinearLayout.VISIBLE);
    }

    /* ===================== CRONÓMETRO ===================== */
    private void setupStopwatch() {
        btnSwStartPause.setOnClickListener(v -> {
            if (!swRunning) {
                swStartTime = System.currentTimeMillis();
                swHandler.post(swTick);
                swRunning = true;
                btnSwStartPause.setText("Pausar");
            } else {
                swAccumulated += (System.currentTimeMillis() - swStartTime);
                swHandler.removeCallbacks(swTick);
                swRunning = false;
                btnSwStartPause.setText("Iniciar");
            }
        });

        btnSwReset.setOnClickListener(v -> {
            swHandler.removeCallbacks(swTick);
            swRunning = false;
            swStartTime = 0L;
            swTimeInMillis = 0L;
            swAccumulated = 0L;
            tvStopwatch.setText("00:00:00");
            btnSwStartPause.setText("Iniciar");
        });
    }

    /* ===================== TEMPORIZADOR ===================== */
    private void setupTimer() {
        btnTmrStartPause.setOnClickListener(v -> {
            if (!tmrRunning) {
                if (tmrMillisLeft == 0L) {
                    long ms = parseInputsToMillis();
                    if (ms <= 0) {
                        new AlertDialog.Builder(this)
                                .setTitle("Tiempo inválido")
                                .setMessage("Ingresa minutos/segundos mayores a 0.")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }
                    tmrMillisLeft = ms;
                    updateTimerText(tmrMillisLeft);
                }
                startCountDown();
            } else {
                pauseCountDown();
            }
        });

        btnTmrReset.setOnClickListener(v -> resetCountDown());
    }

    private long parseInputsToMillis() {
        String mStr = etMinutes.getText() != null ? etMinutes.getText().toString().trim() : "";
        String sStr = etSeconds.getText() != null ? etSeconds.getText().toString().trim() : "";

        int m = TextUtils.isEmpty(mStr) ? 0 : Integer.parseInt(mStr);
        int s = TextUtils.isEmpty(sStr) ? 0 : Integer.parseInt(sStr);
        if (s >= 60) s = 59;

        return (m * 60L + s) * 1000L;
    }

    private void startCountDown() {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(tmrMillisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tmrMillisLeft = millisUntilFinished;
                updateTimerText(tmrMillisLeft);
                applyTimerState("running"); // (3)
            }

            @Override
            public void onFinish() {
                tmrRunning = false;
                tmrMillisLeft = 0L;
                updateTimerText(0L);
                btnTmrStartPause.setText("Iniciar");
                applyTimerState("finished"); // (3)
                incrementTodayHistory();     // (4)
                updateHistoryLabel();        // (4)

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("¡Tiempo cumplido!")
                        .setMessage("El temporizador llegó a 0.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }.start();

        tmrRunning = true;
        btnTmrStartPause.setText("Pausar");
    }

    private void pauseCountDown() {
        if (countDownTimer != null) countDownTimer.cancel();
        tmrRunning = false;
        btnTmrStartPause.setText("Iniciar");
        applyTimerState("paused"); // (3)
    }

    private void resetCountDown() {
        if (countDownTimer != null) countDownTimer.cancel();
        tmrRunning = false;
        tmrMillisLeft = 0L;
        tvTimer.setText("00:00");
        btnTmrStartPause.setText("Iniciar");
        applyTimerState("idle"); // (3)
    }

    private void updateTimerText(long millis) {
        int secs = (int) (millis / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
    }

    /* ===================== (2) PRESETS ===================== */
    private void setupPresets() {
        // Presets iniciales
        presetList.add("05:00");
        presetList.add("10:00");
        presetList.add("25:00");

        presetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, presetList);
        lvPresets.setAdapter(presetAdapter);

        // Tocar un preset -> iniciar de inmediato
        lvPresets.setOnItemClickListener((parent, view, position, id) -> {
            String mmss = presetList.get(position);
            long ms = parseMmSsToMillis(mmss);
            if (ms > 0) {
                tmrMillisLeft = ms;
                updateTimerText(tmrMillisLeft);
                startCountDown();
            }
        });

        // Guardar el tiempo actual de inputs como nuevo preset
        btnAddPreset.setOnClickListener(v -> {
            long ms = parseInputsToMillis();
            if (ms <= 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Tiempo inválido")
                        .setMessage("Ingresa minutos/segundos mayores a 0 para guardar.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            String label = formatMillisToMmSs(ms);
            if (!presetList.contains(label)) {
                presetList.add(label);
                presetAdapter.notifyDataSetChanged();
            }
        });
    }

    private long parseMmSsToMillis(String mmss) {
        try {
            String[] parts = mmss.split(":");
            int m = Integer.parseInt(parts[0]);
            int s = Integer.parseInt(parts[1]);
            return (m * 60L + s) * 1000L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private String formatMillisToMmSs(long ms) {
        int secs = (int) (ms / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    /* ===================== (4) HISTORIAL ===================== */
    private String todayKey() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return "history_" + today;
    }

    private void incrementTodayHistory() {
        String key = todayKey();
        int current = prefs.getInt(key, 0);
        prefs.edit().putInt(key, current + 1).apply();
    }

    private void updateHistoryLabel() {
        int count = prefs.getInt(todayKey(), 0);
        tvHistory.setText("Completados hoy: " + count);
    }

    /* Limpieza */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        swHandler.removeCallbacks(swTick);
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
