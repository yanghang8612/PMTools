package com.casc.pmtools.helper.param;

public class MsgAffirmTask {

    private String taskid;

    private long time;

    public MsgAffirmTask(String taskID) {
        this.taskid = taskID;
        this.time = System.currentTimeMillis();
    }
}
