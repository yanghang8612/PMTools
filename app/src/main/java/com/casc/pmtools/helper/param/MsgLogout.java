package com.casc.pmtools.helper.param;

public class MsgLogout {

    private int userid;

    private long time;

    public MsgLogout(int userid) {
        this.userid = userid;
        this.time = System.currentTimeMillis();
    }
}
