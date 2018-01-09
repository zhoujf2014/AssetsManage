package com.gtafe.assetsmanage.beans;

/**
 * Created by ZhouJF on 2018/1/8.
 */

public class EventBusBean {
    private int type;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
