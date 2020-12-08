package com.example.owner.todolist.Model;

public class ToDoList
{
    int id;
    String name;

    public ToDoList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ToDoList(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
