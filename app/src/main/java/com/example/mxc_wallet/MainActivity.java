package com.example.mxc_wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mxc_wallet.bean.ETHTxByAddressBean;
import com.example.mxc_wallet.bean.MXCTxByAddressBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;

    boolean logon = false;
    public static final int FUNC_LOGIN = 1;
    private Toolbar toolbar;
    private String cusName;
    private String cusEth;
    private List<ETHTxByAddressBean.ResultBean> mETHList;
    private List<MXCTxByAddressBean.ResultBean> mMXCList;
    private ServiceInBackGround etherAPI = null;

    private TextView mMsgView;
    private View mMainFormView;
    private View mProgressView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private RecyclerView.Adapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!logon) {
            Intent intent = new Intent(this, LoginActivity.class);
//            startActivities(new Intent[]{intent});
            startActivityForResult(intent, FUNC_LOGIN);
        }

        initView();

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.setDrawerListener(mDrawerToggle);

        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(navigationView);
                Toast.makeText(MainActivity.this, "Header View is clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_eth:
                        showProgress(true);
                        etherAPI = new ServiceInBackGround("getETHBalance");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_mxc:
                        showProgress(true);
                        etherAPI = new ServiceInBackGround("getMXCBalance");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_ethtx_check:
                        showProgress(true);
                        etherAPI = new ServiceInBackGround("getETHTxByAddress");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_mxctx_check:
                        showProgress(true);
                        etherAPI = new ServiceInBackGround("getMXCTxByAddress");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_tx_check_hash:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Under Construction...")
                                .setIcon(R.drawable.mxc_logo_02)
                                .setMessage("We are still working on it.")
                                .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                    case R.id.menu_internaltx_check:
                        showProgress(true);
                        etherAPI = new ServiceInBackGround("getInterTxByAddress");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_settings:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Under Construction...")
                                .setIcon(R.drawable.mxc_logo_02)
                                .setMessage("We are still working on it.")
                                .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                    case R.id.menu_share:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Under Construction......")
                                .setIcon(R.drawable.mxc_logo_02)
                                .setMessage("We are still working on it.")
                                .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                    case R.id.menu_about:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("MXC Foundation")
                                .setIcon(R.drawable.mxc_logo_02)
                                .setMessage("MXC wallet 1.0.0-Beta \n \n Powered by MXC Foundation gGmbH")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                        break;
                }
                drawerLayout.closeDrawer(navigationView);
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initView(){
        mMsgView = (TextView) findViewById(R.id.msg);
        mMainFormView = findViewById(R.id.main_container);
        //Loading process
        mProgressView = findViewById(R.id.main_process);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 设置Item添加和移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置Item之间间隔样式
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
    }

    //get data from LoginActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FUNC_LOGIN) {
            if (resultCode == RESULT_OK) {
                logon = true;
                String name = data.getStringExtra("LOGIN_NAME");
                String ethaddress = data.getStringExtra("LOGIN_ETH");
                cusName = name;
                cusEth = ethaddress;
                mMsgView.setText("Hello " + cusName + " welcome to the MXC wallet!");
                //Log.d("User name is: ", name);
                toolbar.setTitle(name);
            } else {
                finish();
            }
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
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        private String mToken;

        public MyRecyclerAdapter(String result) {
            this.mToken = result;
        }

        public void updateData(String result) {
            this.mToken = result;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 实例化展示的view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_rv_item, parent, false);
            // 实例化viewholder
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 绑定数据
            if (mToken.equals("ETH")){
                //holder.mIv.setText(mETHList.get(position).timeStamp);
                java.util.Date time = new java.util.Date(mETHList.get(position).timeStamp);
                holder.mIv.setText(time.toString());
            }else {
                holder.mIv.setText(mMXCList.get(position).timeStamp);
            }
        }

        @Override
        public int getItemCount() {
            if (mToken.equals("ETH")){
                return mETHList == null ? 0 : mETHList.size();
            }else {
                return mMXCList == null ? 0 : mMXCList.size();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mIv;

            public ViewHolder(View itemView) {
                super(itemView);
                mIv = (TextView) itemView.findViewById(R.id.item_iv);
            }
        }
    }

    private class ServiceInBackGround extends AsyncTask<Void, Void, List> {
        private Etherscan etherscan;
        private List asyMsg;
        private List<ETHTxByAddressBean.ResultBean> asyMsgETHList;
        private List<MXCTxByAddressBean.ResultBean> asyMsgMXCList;
        private int asyNo;
        private String etherSwitch;

        public ServiceInBackGround(String etherSwitch) {
            this.etherSwitch = etherSwitch;
        }

        @Override
        protected List doInBackground(Void... params) {
            etherscan = new Etherscan();
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
            etherAPI = null;
            switch (asyNo) {
                case 0:
                    mMsgView.refreshDrawableState();
                    mMsgView.setText(asyMsg.get(0) + " ETH");
                    showProgress(false);
                    break;
                case 1:
                    mMsgView.refreshDrawableState();
                    mMsgView.setText(asyMsg.get(0) + " MXC");
                    showProgress(false);
                    break;
                case 2:
                    mMsgView.refreshDrawableState();
                    mRecyclerLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    mETHList = asyMsgETHList;
                    mRecyclerAdapter = new MyRecyclerAdapter("ETH");
                    initRecyclerView();
                    showProgress(false);
                    break;
                case 3:
                    mMsgView.refreshDrawableState();
                    mRecyclerLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    mMXCList = asyMsgMXCList;
                    mRecyclerAdapter = new MyRecyclerAdapter("MXC");
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
    }
}