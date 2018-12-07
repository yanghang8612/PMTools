package com.casc.pmtools.message;

public class TaskAlertedMessage {

    public String productName;

    public int productCount;

    public TaskAlertedMessage(String productName, int productCount) {
        this.productName = productName;
        this.productCount = productCount;
    }
}
