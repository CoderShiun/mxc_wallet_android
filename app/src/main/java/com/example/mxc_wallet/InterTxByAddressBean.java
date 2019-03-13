package com.example.mxc_wallet;

import java.util.List;

public class InterTxByAddressBean {
    public String status;
    public String message;

    public List<ResultBean> result;

    public class ResultBean {
    }

    public InterTxByAddressBean(String status, String message, List<ResultBean> result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

}
