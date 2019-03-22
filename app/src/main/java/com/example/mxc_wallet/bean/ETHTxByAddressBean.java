package com.example.mxc_wallet.bean;

import java.util.List;

public class ETHTxByAddressBean {
    public String status;
    public String message;
    public List<ResultBean> result;

    public class ResultBean{
        public String blockNumber;
        public String timeStamp;
        public String hash;
        public String nonce;
        public String blockHash;
        public String transactionIndex;
        public String from;
        public String to;
        public String value;
        public String gas;
        public String gasPrice;
        public String isError;
        public String txreceipt_status;
        public String input;
        public String contractAddress;
        public String cumulativeGasUsed;
        public String gasUsed;
        public String confirmations;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public ETHTxByAddressBean(String status, String message, List<ResultBean> result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}
