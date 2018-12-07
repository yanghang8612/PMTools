package com.casc.pmtools.helper.param;

public class MsgCreateTask {

    private int userid;

    private long time;

    private int productcode;

    private int number;

    public MsgCreateTask(int userID, int productCode, int number) {
        this.userid = userID;
        this.time = System.currentTimeMillis();
        this.productcode = productCode;
        this.number = number;
    }
}
