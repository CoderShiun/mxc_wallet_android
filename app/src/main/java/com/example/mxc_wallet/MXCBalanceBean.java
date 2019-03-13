package com.example.mxc_wallet;

public class MXCBalanceBean {
    public String status;
    public String message;
    public String result;

    public MXCBalanceBean(String status, String message, String result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}
