package com.example.mxc_wallet;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;

    boolean logon = false;
    public static final int FUNC_LOGIN = 1;
    private TextView mMsgView;
    private Toolbar toolbar;
    private String cusName;
    private String cusEth;
    private String balance;
    private Etherscan etherscan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!logon) {
            Intent intent = new Intent(this, LoginActivity.class);
//            startActivities(new Intent[]{intent});
            startActivityForResult(intent, FUNC_LOGIN);
        }

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.setDrawerListener(mDrawerToggle);
        mMsgView = (TextView) findViewById(R.id.msg);

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
                        try {
                            etherscan = new Etherscan();
                            //String a = new Etherscan().getETHBalance(cusEth);
                            //balance = etherscan.getETHBalance(cusEth);
                            mMsgView.setText(balance + " ETH");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case R.id.menu_mxc:

                        break;
                    case R.id.menu_tx_check:

                        break;
                    case R.id.menu_tx_check_hash:

                        break;
                    case R.id.menu_internaltx_check:

                        break;
                    case R.id.menu_settings:
                        Toast.makeText(MainActivity.this, "Settings is clicked!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_share:
                        Toast.makeText(MainActivity.this, "Share is clicked!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_about:
                        Toast.makeText(MainActivity.this, "About is clicked!", Toast.LENGTH_SHORT).show();
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


    //.constraint.ConstraintLayout

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
                //Log.d("User name is: ", name);
                toolbar.setTitle(name);
            } else {
                finish();
            }
        }
    }
}