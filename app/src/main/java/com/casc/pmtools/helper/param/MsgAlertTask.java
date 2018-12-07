package com.casc.pmtools.helper.param;

public class MsgAlertTask {

    private String taskid;

    private int productcode;

    private int number;

    public MsgAlertTask(String taskID, int productCode, int number) {
        this.taskid = taskID;
        this.productcode = productCode;
        this.number = number;
    }
}
