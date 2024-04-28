package com.example.stepcountingandrewardapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.logging.LogRecord;

class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountTextView;

    private TextView distanceTextView;

    private TextView TimeTextView;

    private Button pauseButton;

    private SensorManager sensorManager;

    private Sensor stepcountersensor;

    private int stepCount = 0;

    private ProgressBar progressBar;

    private boolean ispaused = false;

    private long timePaused = 0;

    private float steplengthInmeter = 0.762f;

    private long startTime;

    private int stepCountTargget = 5000;

    private TextView stepCountTargetTextview;

    private Handler timerHandler = new Handler();

    private Runnable timerRunnable = new Runnable(){
        @Override
        public void run() {
            long milis = System.currentTimeMillis() - startTime;
            int seconds=(int)(milis/1000);
            int min= seconds/60;
            seconds= seconds%60;
            TimeTextView.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", min, seconds));
            timerHandler.postDelayed(this, 1000);
        }

    };





    @Override
    protected void onStop() {
        super.onStop();
        if (stepcountersensor != null) {
            sensorManager.unregisterListener(this);
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepcountersensor != null) {
            sensorManager.registerListener(this, stepcountersensor, SensorManager.SENSOR_DELAY_NORMAL);
            timerHandler.postDelayed(timerRunnable,0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stepCountTextView = findViewById(R.id.stepCountTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        TimeTextView = findViewById(R.id.timeTextView);
        pauseButton = findViewById(R.id.pauseButton);
        stepCountTargetTextview = findViewById(R.id.stepCountTargetTextView);
        progressBar = findViewById(R.id.progressBar);

        startTime = System.currentTimeMillis();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepcountersensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(stepCountTargget);
        stepCountTargetTextview.setText("Step Goal:" + stepCountTargget);
        if (stepcountersensor == null) {
            stepCountTextView.setText("step counter is not available");
        }


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) sensorEvent.values[0];
            stepCountTextView.setText("Step Count:" + stepCount);
            progressBar.setProgress(stepCount);

            if (stepCount >= stepCountTargget) {

                stepCountTargetTextview.setText("Step Goal Achieved");
            }

           float distanceInKM= stepCount*  steplengthInmeter/1000;
            distanceTextView.setText(String.format(Locale.getDefault(),"distsnce: %.2f KM", distanceInKM));
        }
    }

            @Override
            public void onAccuracyChanged(Sensor sensor,int accuracy){

            }

    public void onPauseBυttοnclicked(View view) {
        if (ispaused) {
            ispaused=false;
            pauseButton.setText("Paused");
            startTime= System.currentTimeMillis()-timePaused;
            timerHandler.postDelayed(timerRunnable,0);
        }else{
            ispaused=true;
            pauseButton.setText("Resume");

            timerHandler.removeCallbacks(timerRunnable);
            timePaused=System.currentTimeMillis()-startTime;
        }
    }
}

