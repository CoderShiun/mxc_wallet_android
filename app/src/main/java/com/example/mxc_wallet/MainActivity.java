package com.example.mxc_wallet;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mxc_wallet.bean.ETHTxByAddressBean;
import com.example.mxc_wallet.bean.MXCTxByAddressBean;
import com.example.mxc_wallet.recycleadapter.MyRecyclerAdapter;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;

    boolean logon = false;
    public static final int FUNC_LOGIN = 1;
    public static final int FUNC_BARCODE = 2;
    private Toolbar toolbar;
    private String cusName;
    private String cusEth;
    private String sendTo;
    private String amount;
    public static List<ETHTxByAddressBean.ResultBean> mETHList;
    public static List<MXCTxByAddressBean.ResultBean> mMXCList;
    private EtherscanTask etherAPI = null;

    private TextView mMsgView;
    private View mMainFormView;
    private View mProgressView;

    //for tx dialog
    LayoutInflater mDialogFactory;
    private View mDialogEntryView;
    private EditText mDialogEdit_to;
    private EditText mDialogEdit_amount;


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
                        etherAPI = new EtherscanTask("getETHBalance");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_mxc:
                        showProgress(true);
                        etherAPI = new EtherscanTask("getMXCBalance");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_ethtx_check:
                        showProgress(true);
                        etherAPI = new EtherscanTask("getETHTxByAddress");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_mxctx_check:
                        showProgress(true);
                        etherAPI = new EtherscanTask("getMXCTxByAddress");
                        etherAPI.execute((Void) null);
                        break;
                    case R.id.menu_eth_tx:
                        mDialogFactory = LayoutInflater.from(MainActivity.this);
                        mDialogEntryView = mDialogFactory.inflate(R.layout.dialog_tx, null);
                        mDialogEdit_to = mDialogEntryView.findViewById(R.id.editTextTo);
                        mDialogEdit_amount = mDialogEntryView.findViewById(R.id.editTextAmount);
                        AlertDialog.Builder editDialog = new AlertDialog.Builder(MainActivity.this);
                        editDialog.setTitle(getString(R.string.dialog_EthTx_msg));
                        editDialog.setIcon(R.drawable.ic_attach_money_black_24dp);

                        editDialog.setView(mDialogEntryView);

                        editDialog.setPositiveButton(getString(R.string.dialog_EthTx_ok)
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendTo = mDialogEdit_to.getText().toString();
                                        amount = mDialogEdit_amount.getText().toString();

                                        Intent ethIntent = new Intent(MainActivity.this, ScanBarcodeActivity.class);
                                        startActivityForResult(ethIntent, FUNC_BARCODE);
                                        //etherAPI = new ServiceInBackGround("getMXCTxByAddress");
//                                      etherAPI.execute((Void) null);
                                        dialog.dismiss();
                                    }
                                });

                        editDialog.setNegativeButton(getString(R.string.dialog_EthTx_cancel)
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        editDialog.create().show();
                        break;
                    case R.id.menu_mxc_tx:
                        Intent mxcIntent = new Intent(MainActivity.this, ScanBarcodeActivity.class);
                        startActivityForResult(mxcIntent, FUNC_BARCODE);
                        //etherAPI = new ServiceInBackGround("getMXCTxByAddress");
//                        etherAPI.execute((Void) null);
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
                        etherAPI = new EtherscanTask("getInterTxByAddress");
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

    private void initView() {
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

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 设置Item添加和移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置Item之间间隔样式
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                MainActivity.this, LinearLayoutManager.VERTICAL));
    }

    //get data from Other Activity
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

        if (requestCode == FUNC_BARCODE) {
            if (resultCode == RESULT_OK) {
                String priKey = data.getStringExtra("PRIVATE_KEY");
                mMsgView.setText(priKey);
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

    private class EtherscanTask extends AsyncTask<Void, Void, List> {
        private Etherscan etherscan;
        private List asyMsg;
        private List<ETHTxByAddressBean.ResultBean> asyMsgETHList;
        private List<MXCTxByAddressBean.ResultBean> asyMsgMXCList;
        private int asyNo;
        private String etherSwitch;

        public EtherscanTask(String etherSwitch) {
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
                    MainActivity.this, LinearLayoutManager.VERTICAL,
                    false);
            mRecyclerAdapter = new MyRecyclerAdapter(token);

            ((MyRecyclerAdapter) mRecyclerAdapter).setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    new AlertDialog.Builder(MainActivity.this)
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
}