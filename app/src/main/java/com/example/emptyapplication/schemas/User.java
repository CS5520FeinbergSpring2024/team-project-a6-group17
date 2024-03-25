package com.example.emptyapplication.schemas;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private List<String> quizCreated;
//    todo: quiz taken

    public User() {
        // Default constructor required for Firebase
    }

    public User(String username) {
        this.username = username;
        this.quizCreated = new ArrayList<>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getQuizCreated() {
        return quizCreated;
    }

    public void setQuizCreated(List<String> quizCreated) {
        this.quizCreated = quizCreated;
    }
}
