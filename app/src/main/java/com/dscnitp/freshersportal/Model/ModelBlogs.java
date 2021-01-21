package com.dscnitp.freshersportal.Model;

public class ModelBlogs {
    String Type;
    String Writtenby;
    String Title;
    String Description;
    String Department;
    String Time;

    public ModelBlogs() {
    }

    public ModelBlogs(String type, String writtenby, String title, String description, String department, String time) {
        Type = type;
        Writtenby = writtenby;
        Title = title;
        Description = description;
        Department = department;
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getWrittenby()
    {
        return Writtenby;
    }

    public void setWrittenby(String writtenby) {
        Writtenby = writtenby;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    }
