package com.example.mxc_wallet.backgroundTasks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.example.mxc_wallet.Etherscan;
import com.example.mxc_wallet.MainActivity;
import com.example.mxc_wallet.R;
import com.example.mxc_wallet.bean.ETHTxByAddressBean;
import com.example.mxc_wallet.bean.MXCTxByAddressBean;
import com.example.mxc_wallet.recycleadapter.MyRecyclerAdapter;

import java.io.IOException;
import java.util.List;

public class EtherscanTasks extends MainActivity {
    /*
    public class EtherscanTask extends AsyncTask<Void, Void, List> {
        private Etherscan etherscan;
        private List asyMsg;
        private List<ETHTxByAddressBean.ResultBean> asyMsgETHList;
        private List<MXCTxByAddressBean.ResultBean> asyMsgMXCList;
        private int asyNo;
        private String etherSwitch;
//        private EtherscanTask etherAPI = null;

        public EtherscanTask(String etherSwitch) {
            this.etherSwitch = etherSwitch;
        }

        @Override
        protected List doInBackground(Void... params) {
            etherscan = new Etherscan();
            mMainFormView = findViewById(R.id.main_container);
            mProgressView = findViewById(R.id.main_process);
            showProgress(true);
            switch (etherSwitch) {
                case "getETHBalance":
                    try {
                        asyMsg = etherscan.getETHBalance(cusEth);
                        asyNo = 0;
                        return asyMsg;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getMXCBalance":
                    try {
                        asyMsg = etherscan.getMXCBalance(cusEth);
                        asyNo = 1;
                        return asyMsg;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getETHTxByAddress":
                    try {
                        asyMsgETHList = etherscan.getETHTxByAddress(cusEth);
                        asyNo = 2;
                        return asyMsgETHList;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getMXCTxByAddress":
                    try {
                        asyMsgMXCList = etherscan.getMXCTxByAddress(cusEth);
                        asyNo = 3;
                        return asyMsgMXCList;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getInterTxByAddress":
                    try {
                        asyMsg = etherscan.getInterTxByAddress(cusEth);
                        asyNo = 4;
                        return asyMsg;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List s) {
//            etherAPI = null;
            switch (asyNo) {
                case 0:
                    mMsgView.refreshDrawableState();
                    mMsgView.setText(asyMsg.get(0) + " ETH");
                    initData("");
                    initRecyclerView();
                    showProgress(false);
                    break;
                case 1:
                    mMsgView.refreshDrawableState();
                    mMsgView.setText(asyMsg.get(0) + " MXC");
                    initData("");
                    initRecyclerView();
                    showProgress(false);
                    break;
                case 2:
                    mMsgView.setText("");
                    mETHList = asyMsgETHList;
                    initData("ETH");
                    initRecyclerView();
                    showProgress(false);
                    break;
                case 3:
                    mMsgView.setText("");
                    mMXCList = asyMsgMXCList;
                    initData("MXC");
                    initRecyclerView();
                    showProgress(false);
                    break;
                case 4:
                    mMsgView.refreshDrawableState();
                    //mMsgView.setText(asyMsg);
                    showProgress(false);
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }

        private void initData(String token) {
            mRecyclerLayoutManager = new LinearLayoutManager(
                    EtherscanTasks.this, LinearLayoutManager.VERTICAL,
                    false);
            mRecyclerAdapter = new MyRecyclerAdapter(token);

            ((MyRecyclerAdapter) mRecyclerAdapter).setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    new AlertDialog.Builder(EtherscanTasks.this)
                            .setTitle("Details")
                            .setIcon(R.drawable.mxc_logo_02)
                            .setMessage("From: " + mETHList.get(position).from +
                                    "\n To: " + mETHList.get(position).to +
                                    "\n Amount: " + mETHList.get(position).value)
                            .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    //Toast.makeText(MainActivity.this,"long click " + position + " item", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Show loading
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/
}
