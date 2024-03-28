package com.example.emptyapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddQuizActivity extends AppCompatActivity {

    private int currQuestionNo;
    private int numQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currQuestionNo = getIntent().getIntExtra("currQuestionNo", 0);
        numQuestions = getIntent().getIntExtra("numQuestions", 0);

        TextView progressText = findViewById(R.id.progressText);
        ProgressBar progressBarAddQuiz = findViewById(R.id.progressBarAddQuiz);
        Spinner spinnerAddQuizType = findViewById(R.id.spinnerAddQuizType);


        progressText.setText(String.format("Question %d / %d", currQuestionNo, numQuestions));
        progressBarAddQuiz.setProgress(currQuestionNo);
        progressBarAddQuiz.setMax(numQuestions);

        // set up dynamic section based on quiz type
        spinnerAddQuizType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuizType = parent.getItemAtPosition(position).toString();
                updateAnswerSection(selectedQuizType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateAnswerSection(String quizType) {
        LinearLayout answerSection = findViewById(R.id.answerSection);
        answerSection.removeAllViews(); // Clear previous components

        if ("Multiple Choice".equals(quizType)) {
            for (int i = 0; i < 4; i++) {
                // Create a horizontal LinearLayout for each answer
                LinearLayout answerContainer = new LinearLayout(this);
                answerContainer.setOrientation(LinearLayout.HORIZONTAL);
                answerContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // Add a CheckBox
                CheckBox answerCheck = new CheckBox(this);
                answerCheck.setId(View.generateViewId());

                // Add an EditText for answer text
                EditText answerInput = new EditText(this);
                answerInput.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                answerInput.setHint("Answer " + (i + 1));
                answerInput.setId(View.generateViewId());

                // Add the text input and checkbox to the container
                answerContainer.addView(answerInput);
                answerContainer.addView(answerCheck);

                // Add the answer layout to the section
                answerSection.addView(answerContainer);
            }
        }
    }
}