package com.example.owner.todolist.Model;

public class Task {
    String taskName;
    String list_id;
    String notes;
    boolean completed;
    String dueDate;

    public Task(String taskName, String list_id, String notes, boolean completed, String dueDate) {
        this.taskName = taskName;
        this.list_id = list_id;
        this.notes = notes;
        this.completed = completed;
        this.dueDate = dueDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
