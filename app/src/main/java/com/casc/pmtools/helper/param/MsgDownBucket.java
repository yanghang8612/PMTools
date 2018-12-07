package com.casc.pmtools.helper.param;

import java.util.ArrayList;
import java.util.List;

public class MsgDownBucket {

    private String taskid;

    private List<Bucket> bucketlist;

    public MsgDownBucket(String taskid, String bodyCode) {
        this.taskid = taskid;
        this.bucketlist = new ArrayList<>();
        this.bucketlist.add(new Bucket(bodyCode));
    }

    private class Bucket {

        private String bodycode;

        private long time;

        public Bucket(String bodyCode) {
            this.bodycode = bodyCode;
            this.time = System.currentTimeMillis();
        }
    }
}
