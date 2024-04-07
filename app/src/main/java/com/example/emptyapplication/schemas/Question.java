package com.example.emptyapplication.schemas;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


public class Question {
    private String text;
    private QuestionType type;

    private ArrayList<String> options;

    private ArrayList<Integer> correctOptions;

    private boolean correctTFAnswer;

    public Question() {

    }

    public Question(String text, QuestionType type) {
        this.text = text;
        this.type = type;
        this.options = new ArrayList<>();
        this.correctOptions = new ArrayList<>();
        this.correctTFAnswer = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public ArrayList<Integer> getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(ArrayList<Integer> correctOptions) {
        this.correctOptions = correctOptions;
    }

    public boolean isCorrectTFAnswer() {
        return correctTFAnswer;
    }

    public void setCorrectTFAnswer(boolean correctTFAnswer) {
        this.correctTFAnswer = correctTFAnswer;
    }

    public String getCorrectAnswerString() {
        if (this.type == QuestionType.TRUE_FALSE) {
            return correctTFAnswer ? "True" : "False";
        } else if (this.type == QuestionType.MULTIPLE_CHOICE) {
            return correctOptions.stream()
                    .map(correctOption -> options.get(correctOption))
                    .collect(Collectors.joining(", "));
        }
        return "N/A"; // In case there are other types without a clear 'correct answer'
    }
}
