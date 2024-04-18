package com.example.emptyapplication;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emptyapplication.schemas.Question;
import com.example.emptyapplication.schemas.QuestionType;
import com.example.emptyapplication.schemas.Quiz;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TakeQuizQuestionsActivity extends AppCompatActivity {
    //private String quizId;

    private Quiz quiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private TextView textViewQuizInfo;
    private TextView textViewQuestion;
    private RadioGroup radioGroupOptions;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz_questions);

        // Retrieve quiz data from intent
        String quizId = getIntent().getStringExtra("quiz_id");

        // Initialize Firebase
        assert quizId != null;
        DatabaseReference quizzesRef = FirebaseDatabase.getInstance().getReference("Quiz").child(quizId);
        // Retrieve quiz data from Firebase
        quizzesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    quiz = snapshot.getValue(Quiz.class);
                    assert quiz != null;
                    questions = quiz.getQuestions();
                    // Initialize views and load first question
                    initializeViews();
                    loadQuestion(currentQuestionIndex);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void initializeViews() {
        textViewQuizInfo = findViewById(R.id.textViewQuizInfo); // Initialize the quiz info TextView
        textViewQuestion = findViewById(R.id.textViewQuestion);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        Button buttonNext = findViewById(R.id.buttonNext);
        Button buttonFinish = findViewById(R.id.buttonFinish);
        buttonFinish.setVisibility(View.GONE);

        // Set quiz information text
        textViewQuizInfo.setText(getString(R.string.quiz_info, quiz.getName(), currentQuestionIndex + 1, questions.size()));

        // Set next button click listener
        buttonNext.setOnClickListener(v -> {
            checkAnswer();
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                loadQuestion(currentQuestionIndex);
                updateButtonText();
                // Update quiz info text when loading new question
                textViewQuizInfo.setText(getString(R.string.quiz_info, quiz.getName(), currentQuestionIndex + 1, questions.size()));
            } else {
                showResult();
            }
        });
        // Set Finish button click listener
        buttonFinish.setOnClickListener(v -> {
            showResult(); // Call showResult method when Finish button is clicked
        });
        updateButtonText();
    }

    private void updateButtonText() {
        Button buttonNext = findViewById(R.id.buttonNext);
        if (currentQuestionIndex < questions.size() - 1) {
            buttonNext.setText(R.string.button_next_text);
        } else {
            buttonNext.setText(R.string.button_finish_text);
        }
    }

    private void loadQuestion(int index) {
        Question question = questions.get(index);
        textViewQuestion.setText(question.getText());
        radioGroupOptions.removeAllViews();
        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            for (String option : question.getOptions()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                radioGroupOptions.addView(radioButton);
            }
        } else if (question.getType() == QuestionType.TRUE_FALSE) {
            RadioButton trueButton = new RadioButton(this);
            trueButton.setText(R.string.button_true_text);
            RadioButton falseButton = new RadioButton(this);
            falseButton.setText(R.string.button_false_text);
            radioGroupOptions.addView(trueButton);
            radioGroupOptions.addView(falseButton);
        }
    }

    private void checkAnswer() {
        int selectedRadioButtonId = radioGroupOptions.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String selectedOption = selectedRadioButton.getText().toString();
            Question question = questions.get(currentQuestionIndex);
            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                ArrayList<Integer> correctOptions = question.getCorrectOptions();
                if (correctOptions.size() == 1) {
                    int selectedOptionIndex = question.getOptions().indexOf(selectedOption);
                    if (selectedOptionIndex != -1 && correctOptions.contains(selectedOptionIndex)) {
                        correctAnswers++;
                    }
                } else {
                    // Handling multiple correct options
                    int selectedOptionIndex = question.getOptions().indexOf(selectedOption);
                    if (selectedOptionIndex != -1) {
                        boolean allCorrectOptionsSelected = true;
                        for (int optionIndex : correctOptions) {
                            if (optionIndex != selectedOptionIndex) {
                                allCorrectOptionsSelected = false;
                                break;
                            }
                        }
                        if (allCorrectOptionsSelected) {
                            correctAnswers++;
                        }
                    }
                }
            } else if (question.getType() == QuestionType.TRUE_FALSE) {
                boolean correctAnswer = question.isCorrectTFAnswer();
                boolean selectedAnswer = selectedOption.equals("True");
                if (correctAnswer == selectedAnswer) {
                    correctAnswers++;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the quiz? Your progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> TakeQuizQuestionsActivity.super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void showResult() {
        int totalQuestions = questions.size(); // Get the total number of questions
        String resultText = "Result: " + correctAnswers + "/" + totalQuestions + " are correct! Congratulations! You won " + correctAnswers + " coins!" ;
        Intent intent = new Intent(TakeQuizQuestionsActivity.this, QuizResultActivity.class);

        intent.putExtra("resultText", resultText);
        String quizId = getIntent().getStringExtra("quiz_id");
        intent.putExtra("quiz_id", quizId);
        startActivity(intent); // Launch the QuizResultActivity
        finish(); // Finish the current activity

        updateCoins(correctAnswers);
    }

    private void updateCoins(int coins) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(username).child("coins");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get current number of coins and update
                Integer currentCoins = dataSnapshot.getValue(Integer.class);
                if (currentCoins == null) currentCoins = 0;
                // Update coins based on correct answers
                userRef.setValue(currentCoins + correctAnswers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Failed to read value." + databaseError.toException());
            }
        });
    }

}
