package com.example.emptyapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import android.view.View;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;


public class QuizResultActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textViewResult;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float accelerationCurrentValue;
    private float accelerationPreviousValue;
    private float shakeThreshold = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        //String quizId = getIntent().getStringExtra("quiz_id");

        textViewResult = findViewById(R.id.textViewResult);
        String resultText = getIntent().getStringExtra("resultText");
        textViewResult.setText(resultText);

        Button btnShowAnswers = findViewById(R.id.btnShowAnswers);
        btnShowAnswers.setOnClickListener(v -> showCorrectAnswers());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        accelerationCurrentValue = SensorManager.GRAVITY_EARTH;
        accelerationPreviousValue = SensorManager.GRAVITY_EARTH;
    }


    private void showCorrectAnswers() {
        Intent intent = new Intent(QuizResultActivity.this, CorrectAnswersActivity.class);
        String quizId = getIntent().getStringExtra("quiz_id"); // Assuming you passed quizId to QuizResultActivity
        if (quizId != null) {
            intent.putExtra("quiz_id", quizId); // Pass quizId to CorrectAnswersActivity
            Log.d("QuizResultActivity", "Quiz ID: " + quizId);

            startActivity(intent);
        } else {
            Toast.makeText(this, "Quiz ID is missing", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        accelerationPreviousValue = accelerationCurrentValue;
        accelerationCurrentValue = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = accelerationCurrentValue - accelerationPreviousValue;
        float acceleration = delta * SensorManager.GRAVITY_EARTH;

        if (acceleration > shakeThreshold) {
            showCorrectAnswers();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this context
    }
}


