package com.example.mxc_wallet;

import com.example.mxc_wallet.bean.ETHTxByAddressBean;
import com.example.mxc_wallet.bean.EthBalanceBean;
import com.example.mxc_wallet.bean.InterTxByAddressBean;
import com.example.mxc_wallet.bean.MXCBalanceBean;
import com.example.mxc_wallet.bean.MXCTxByAddressBean;
import com.example.mxc_wallet.bean.TxByHashBean;
import com.google.gson.Gson;

import org.web3j.utils.Convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Etherscan {
    private final String API_KEY = "W8M6B92HBM7CUAQINJ8IMST29RY2ZVSQH4";
    private final String MXC_Address = "0x5Ca381bBfb58f0092df149bD3D243b08B9a8386e";
    private URL restURL;
    private HttpURLConnection conn;
    private Gson gson;
    private ArrayList msg;

    public List getETHBalance(String ethAddress) throws IOException {
        String url = "https://api.etherscan.io/api?module=account&action=balance&address=" +
                ethAddress + "&tag=latest&apikey=" + API_KEY;

        restURL = new URL(url);
        conn = getConn(conn);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = bufferedReader.readLine();

        //String JS = new String();
        /*
        while((line = bufferedReader.readLine()) != null ){
            JS = line;
        }
        */
        gson = new Gson();

        //1
        //解析对象：第一个参数：待解析的字符串 第二个参数结果数据类型的Class对象
        EthBalanceBean ethBalanceBeanJS = gson.fromJson(line, EthBalanceBean.class);

        //String jsonJS = gson.toJson(ethBalanceBeanJS.result);
        BigDecimal balance = Convert.fromWei(ethBalanceBeanJS.result, Convert.Unit.ETHER);
        msg = new ArrayList();
        msg.add(balance);
        //2、
        //解析数组要求使用Type
        /*ArrayList<String> list=gson.fromJson(json2,
                new TypeToken<ArrayList<String>>(){}.getType());
        System.out.println(list);*/

        bufferedReader.close();
        return msg;
    }

    public List getMXCBalance(String ethAddress) throws IOException{
        String url = "https://api.etherscan.io/api?module=account&action=tokenbalance&contractaddress=" +
                MXC_Address + "&address=" +
                ethAddress + "&tag=latest&apikey=" + API_KEY;

        restURL = new URL(url);
        conn = getConn(conn);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = bufferedReader.readLine();

        gson = new Gson();
        MXCBalanceBean mxcBalanceBean = gson.fromJson(line, MXCBalanceBean.class);
        BigDecimal balance = Convert.fromWei(mxcBalanceBean.result, Convert.Unit.ETHER);
        msg = new ArrayList();
        msg.add(balance);

        bufferedReader.close();
        return msg;
    }

    public List getETHTxByAddress(String ethAddress) throws IOException{
        String url = "https://api.etherscan.io/api?module=account&action=txlist&address=" +
                ethAddress + "&startblock=0&endblock=99999999&page=1&offset=10&sort=asc&apikey=" + API_KEY;

        restURL = new URL(url);
        conn = getConn(conn);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String JS = new String();
        while((line = bufferedReader.readLine()) != null ){
            JS = line;
        }

        gson = new Gson();
        ETHTxByAddressBean ethTxByAddressBean = gson.fromJson(JS, ETHTxByAddressBean.class);
        //String jsonJS = gson.toJson(txByAddressBean.result);
        List<ETHTxByAddressBean.ResultBean> resultBeanList = ethTxByAddressBean.getResult();
        //System.out.println(resultBeanList.size());

        bufferedReader.close();
        return resultBeanList;
    }

    public List getMXCTxByAddress(String ethAddress) throws IOException{
        String url = "https://api-rinkeby.etherscan.io/api?module=account&action=tokentx&contractaddress=" +
                MXC_Address + "&address=" +
                ethAddress + "&page=1&offset=1000&sort=asc&apikey=" + API_KEY;

        restURL = new URL(url);
        conn = getConn(conn);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = bufferedReader.readLine();

        gson = new Gson();
        MXCTxByAddressBean mxcBalanceBean = gson.fromJson(line, MXCTxByAddressBean.class);
        //String jsonJS = gson.toJson(txByAddressBean.result);
        List<MXCTxByAddressBean.ResultBean> resultBeanList = mxcBalanceBean.getResult();

        bufferedReader.close();
        return resultBeanList;
    }

    public List getInterTxByAddress(String ethAddress) throws IOException{
        String url = "https://api.etherscan.io/api?module=account&action=txlistinternal&address=" +
                ethAddress + "&startblock=0&endblock=2702578&page=1&offset=10&sort=asc&apikey=" + API_KEY;

        restURL = new URL(url);
        conn = getConn(conn);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String JS = new String();
        while((line = bufferedReader.readLine()) != null ){
            JS = line;
        }

        gson = new Gson();
        InterTxByAddressBean interTxByAddressBean = gson.fromJson(JS, InterTxByAddressBean.class);
        //String jsonJS = gson.toJson(interTxByAddressBean);
        List<InterTxByAddressBean.ResultBean> resultBeanList = interTxByAddressBean.getResult();

        bufferedReader.close();
        return resultBeanList;
    }

    public List getTxByHash(String txHash) throws IOException{
        String url = "https://api.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash=" +
                txHash + "&apikey=" + API_KEY;

        restURL = new URL(url);
        conn = getConn(conn);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String JS = new String();
        while((line = bufferedReader.readLine()) != null ){
            JS = line;
        }

        gson = new Gson();
        TxByHashBean txByHashBean = gson.fromJson(JS, TxByHashBean.class);
        String jsonJS = gson.toJson(txByHashBean);
        List<TxByHashBean.ResultBean> resultBeanList = txByHashBean.getResult();


        bufferedReader.close();
        return resultBeanList;
    }

    public void postMethod(String url, String query) throws IOException {
        URL restURL = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        PrintStream printStream = new PrintStream(conn.getOutputStream());
        printStream.print(query);
        printStream.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        br.close();
    }

    private HttpURLConnection getConn(HttpURLConnection conn) throws IOException {
        conn = (HttpURLConnection) restURL.openConnection();
        conn.setRequestMethod("GET"); // POST GET PUT DELETE
        conn.setRequestProperty("Accept", "application/json");
        return conn;
    }
}