package com.example.myapplication;

public class Task {
    private int id;
    private String title;
    private String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getter & Setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter for Title & Description
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
