package com.example.emptyapplication;


import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class QuizResultActivity extends AppCompatActivity {

    private TextView textViewResult;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_quiz_result);
//
//        textViewResult = findViewById(R.id.textViewResult);
//
//        // Retrieve correctAnswers from intent
//        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
//        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
//
//
//        // Display the quiz result
//        String resultText = getString(R.string.quiz_result, correctAnswers, totalQuestions);
//        textViewResult.setText(resultText);
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        textViewResult = findViewById(R.id.textViewResult);

        // Retrieve the result text from the intent
        String resultText = getIntent().getStringExtra("resultText");

        // Display the quiz result
        textViewResult.setText(resultText);
    }

}
