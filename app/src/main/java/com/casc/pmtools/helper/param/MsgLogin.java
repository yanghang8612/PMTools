package com.casc.pmtools.helper.param;

public class MsgLogin {

    private String username;

    private String password;

    private long time;

    public MsgLogin(String username, String password) {
        this.username = username;
        this.password = password;
        this.time = System.currentTimeMillis();
    }
}
