package com.example.emptyapplication.schemas;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz {

    private String quiz_id;
    private String name;
    private ArrayList<Question> questions;
    private String createdBy;
    private int numQuestions;

    public Quiz() {

    }

    public Quiz(String quiz_id, String name, String createdBy, int numQuestions) {
        this.quiz_id = quiz_id;
        this.name = name;
        this.questions = new ArrayList<>();
        this.createdBy = createdBy;
        this.numQuestions = numQuestions;

        for (int i = 0; i < numQuestions; i++) {
            this.questions.add(new Question());
        }
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }
}
