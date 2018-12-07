package com.casc.pmtools.bean;

public class Task {

    private String taskid;

    private String username;

    private String productname;

    private int number;

    private long time;

    private char flag;

    public String getTaskID() {
        return taskid;
    }

    public String getUsername() {
        return username;
    }

    public String getProductName() {
        return productname;
    }

    public int getNumber() {
        return number;
    }

    public String getTaskInfo() {
        return productname + "<" + number + "(桶)>";
    }

    public long getTime() {
        return time;
    }

    public char getFlag() {
        return flag;
    }
}
