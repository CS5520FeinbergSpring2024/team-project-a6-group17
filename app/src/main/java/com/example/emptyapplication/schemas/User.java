package com.example.emptyapplication.schemas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private List<String> quizcreated;
//    todo: quiz taken

    public User() {
        // Default constructor required for Firebase
    }

    public User(String username) {
        this.username = username;
        this.quizcreated = new ArrayList<>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getQuizcreated() {
        return quizcreated;
    }

    public void setQuizcreated(List<String> quizcreated) {
        this.quizcreated = quizcreated;
    }
}
