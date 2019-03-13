package com.example.mxc_wallet;

import java.util.List;

public class TxByHashBean {
    public String jsonrpc;
    public String id;

    public List<ResultBean> result;

    public class ResultBean {
        public String blockHash;
        public String blockNumber;
        public String from;
        public String gas;
        public String gasPrice;
        public String hash;
        public String input;
        public String nonce;
        public String to;
        public String transactionIndex;
        public String value;
        public String v;
        public String r;
        public String s;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public TxByHashBean(String jsonrpc, String id, List<ResultBean> result) {
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.result = result;
    }
}
