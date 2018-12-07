package com.casc.pmtools.helper.param;

public class MsgVerifyTask {

    private String taskid;

    private long time;

    private int cribnumber;

    private int scatternumber;

    public MsgVerifyTask(String taskID, int cribNum, int scatterNum) {
        this.taskid = taskID;
        this.time = System.currentTimeMillis();
        this.cribnumber = cribNum;
        this.scatternumber = scatterNum;
    }
}
