package com.casc.pmtools.bean;

import java.util.ArrayList;
import java.util.List;

public class Detail {

    private String taskid;

    private long generatetime;

    private char flag;

    private char dealflag;

    private long affirmtime;

    private long verifytime;

    private int onlinenumber;

    private int filternumber;

    private int offlinenumber;

    private int downnumber;

    private int packagenumber;

    private List<Package> packageinfo;

    private List<String> downbucket;

    private int cribnumber;

    private int scatternumber;

    public String getTaskID() {
        return taskid;
    }

    public long getGenerateTime() {
        return generatetime;
    }

    public char getFlag() {
        return flag;
    }

    public char getDealFlag() {
        return dealflag;
    }

    public long getAffirmTime() {
        return affirmtime;
    }

    public long getVerifyTime() {
        return verifytime;
    }

    public int getOnlineNumber() {
        return onlinenumber;
    }

    public int getFilterNumber() {
        return filternumber;
    }

    public int getOfflineNumber() {
        return offlinenumber;
    }

    public int getDownNumber() {
        return downnumber;
    }

    public int getPackageNumber() {
        return packagenumber;
    }

    public List<Package> getPackages() {
        return packageinfo == null ? new ArrayList<Package>() : packageinfo;
    }

    public List<String> getDownBuckets() {
        return downbucket;
    }

    public int getCribNumber() {
        return cribnumber;
    }

    public int getScatterNumber() {
        return scatternumber;
    }
}
