package com.example.mxc_wallet.bean;

import java.util.List;

public class MXCTxByAddressBean {
    public String status;
    public String message;
    public List<ResultBean> result;

    public class ResultBean {
        public String blockNumber;
        public String timeStamp;
        public String hash;
        public String nonce;
        public String blockHash;
        public String from;
        public String contractAddress;
        public String to;
        public String value;
        public String tokenName;
        public String tokenSymbol;
        public String tokenDecimal;
        public String transactionIndex;
        public String gas;
        public String gasPrice;
        public String gasUsed;
        public String cumulativeGasUsed;
        public String input;
        public String confirmations;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public MXCTxByAddressBean(String status, String message, List<ResultBean> result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}
