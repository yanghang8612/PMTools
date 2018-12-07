package com.casc.pmtools.bean;

public class User {

    private int userid;

    private int role;

    public int getUserID() {
        return userid;
    }

    public int getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == 2;
    }
 }
