package com.example.emptyapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emptyapplication.schemas.Question;
import com.example.emptyapplication.schemas.QuestionType;
import com.example.emptyapplication.schemas.Quiz;
import com.example.emptyapplication.schemas.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddQuizActivity extends AppCompatActivity {

    private int currQuestionNo;
    private int numQuestions;
    private String newQuizId;
    private Quiz newQuiz;
    private String selectedQuizType;
    private EditText editTextAddQuizName;
    private EditText editTextAddQuizQuestion;
    private LinearLayout answerSection;
    private DatabaseReference newQuizRef;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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
        newQuizId = getIntent().getStringExtra("newQuizId");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        newQuizRef = database.getReference("Quiz").child(newQuizId);

        newQuizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newQuiz = snapshot.getValue(Quiz.class);
                editTextAddQuizName.setText(newQuiz.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editTextAddQuizName = findViewById(R.id.editTextAddQuizName);
        editTextAddQuizQuestion = findViewById(R.id.editTextAddQuizQuestion);
        answerSection = findViewById(R.id.answerSection);
        TextView progressText = findViewById(R.id.progressText);
        ProgressBar progressBarAddQuiz = findViewById(R.id.progressBarAddQuiz);
        Spinner spinnerAddQuizType = findViewById(R.id.spinnerAddQuizType);
        Button buttonAddQuizPrev = findViewById(R.id.buttonAddQuizPrev);
        Button buttonAddQuizNext = findViewById(R.id.buttonAddQuizNext);

        // set up UI
        buttonAddQuizPrev.setVisibility(View.GONE);
        if (currQuestionNo == numQuestions) {
            buttonAddQuizNext.setText("Submit");
        }

        // progress bar
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

        // add button listener
        buttonAddQuizNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonOnClick();
            }
        });
    }

    private void updateAnswerSection(String quizType) {
        selectedQuizType = quizType;
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
        } else if ("True/False".equals(quizType)) {
            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(LinearLayout.VERTICAL);

            RadioButton trueOption = new RadioButton(this);
            trueOption.setText("True");
            radioGroup.addView(trueOption);

            RadioButton falseOption = new RadioButton(this);
            falseOption.setText("False");
            radioGroup.addView(falseOption);

            answerSection.addView(radioGroup);
        }
    }

    private void nextButtonOnClick() {
        saveCurrentState();
    }

    private void saveCurrentState() {
        newQuiz.setName(editTextAddQuizName.getText().toString());
        Question question = newQuiz.getQuestions().get(currQuestionNo - 1);

        if (editTextAddQuizQuestion.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing question text", Toast.LENGTH_SHORT).show();
            return;
        }
        question.setText(editTextAddQuizQuestion.getText().toString());

        if ("Multiple Choice".equals(selectedQuizType)) {
            question.setType(QuestionType.MULTIPLE_CHOICE);
            ArrayList<String> options = new ArrayList<>();
            ArrayList<Integer> correctOptions = new ArrayList<>();

            for (int i = 0; i < answerSection.getChildCount(); i++) {
                View answerView = answerSection.getChildAt(i);
                if (answerView instanceof LinearLayout) {
                    LinearLayout answerContainer = (LinearLayout) answerView;

                    EditText answerInput = null;
                    CheckBox answerCheck = null;
                    for (int j = 0; j < answerContainer.getChildCount(); j++) {
                        View child = answerContainer.getChildAt(j);
                        if (child instanceof EditText) {
                            answerInput = (EditText) child;
                        } else if (child instanceof CheckBox) {
                            answerCheck = (CheckBox) child;
                        }
                    }

                    if (answerInput != null && answerCheck != null) {
                        String answerText = answerInput.getText().toString();
                        boolean isCorrect = answerCheck.isChecked();

                        if (answerText.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Missing answer", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        options.add(answerText);
                        if (isCorrect) {
                            correctOptions.add(i);
                        }
                    }
                }
            }
            if (correctOptions.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Missing correct answer", Toast.LENGTH_SHORT).show();
                return;
            }
            question.setOptions(options);
            question.setCorrectOptions(correctOptions);
        } else if ("True/False".equals(selectedQuizType)) {
            question.setType(QuestionType.TRUE_FALSE);

            RadioGroup radioGroup = null;
            for (int i = 0; i < answerSection.getChildCount(); i++) {
                View child = answerSection.getChildAt(i);
                if (child instanceof RadioGroup) {
                    radioGroup = (RadioGroup) child;
                    break;
                }
            }

            if (radioGroup != null) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // Handle the case where no option is selected
                if (selectedId == -1) {
                    Toast.makeText(getApplicationContext(), "Missing selection", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
                    String answerText = selectedRadioButton.getText().toString();
                    question.setCorrectTFAnswer("True".equals(answerText));
                }
            }
        }

        newQuizRef.setValue(newQuiz)
                .addOnSuccessListener(aVoid -> {
                    Intent intent;
                    if (currQuestionNo < numQuestions) {
                        intent = new Intent(AddQuizActivity.this, AddQuizActivity.class);
                        intent.putExtra("currQuestionNo", currQuestionNo + 1);
                        intent.putExtra("numQuestions", numQuestions);
                        intent.putExtra("newQuizId", newQuizId);
                    } else {
                        intent = new Intent(AddQuizActivity.this, ManageQuizActivity.class);
                    }
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}