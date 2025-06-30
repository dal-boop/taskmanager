package com.example.taskmanager;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String list;


    public Task(int id, String title, String description, String dueDate, String list) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.list = list;

    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public String getList() { return list; }

}
