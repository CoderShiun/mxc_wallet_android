package com.example.mxc_wallet;

public class EthBalanceBean {
    public String status;
    public String message;
    public String result;

    /*
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }*/

    public EthBalanceBean(String status, String message, String result) {
        super();
        this.status = status;
        this.message = message;
        this.result = result;
    }
    public EthBalanceBean() {
        super();
    }

    /*
    @Override
    public String toString() {
        return "Balance [status=" + status + ", message=" + message + ", result=" + result + "]";
    }
    */
}
