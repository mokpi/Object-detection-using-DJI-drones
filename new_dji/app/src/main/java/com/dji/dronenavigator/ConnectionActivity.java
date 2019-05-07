package com.dji.dronenavigator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class ConnectionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = ConnectionActivity.class.getName();
    private TextView mTextConnectionStatus;
    private TextView mVersionTv;
    private Button mBtnOpen;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        this.doubleBackToExitPressedOnce = true;
        showToast("Press again to exit");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        initUI();
        // Register the broadcast receiver for receiving the device connection's changes.
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegnmoreApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);

    }




    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }

    private void initUI() {

        mTextConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(this);
        mBtnOpen.setEnabled(false);
        mVersionTv = (TextView) findViewById(R.id.textView2);
        mVersionTv.setText(getResources().getString(R.string.sdk_version, DJISDKManager.getInstance().getSDKVersion()));
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DJISDKManager.getInstance().startConnectionToProduct();
                new DJISDKManager.SDKManagerCallback() {
                    @Override
                    public void onRegister(DJIError djiError) {

                    }

                    @Override
                    public void onProductDisconnect() {
                        showToast("Product Disconnected");
                        //mBtnOpen.setEnabled(false);
                       // mTextConnectionStatus.setText(R.string.connection_loose);

                    }

                    @Override
                    public void onProductConnect(BaseProduct baseProduct) {
                        showToast("Product Connected");
                        //mBtnOpen.setEnabled(true);
                        //String str = baseProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
                        //if (null != baseProduct.getModel()) {
                         //   str=(baseProduct.getModel().getDisplayName());
                        //}
                        //mTextConnectionStatus.setText("Status: " + str + " connected");

                    }

                    @Override
                    public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
                        if (baseComponent1 != null) {
                            baseComponent1.setComponentListener(new BaseComponent.ComponentListener() {

                            @Override
                            public void onConnectivityChange(boolean isConnected) {
                                Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
                                }
                            });
                        }
                        Log.d(TAG, String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s", componentKey, baseComponent, baseComponent1));
                    }
                };
            }
        });
    }
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
        }
    };

    private void refreshSDKRelativeUI() {
        BaseProduct mProduct = RegnmoreApplication.getProductInstance();

        if (null != mProduct && mProduct.isConnected()) {

            Log.v(TAG, "refreshSDK: True");
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
            if (null != mProduct.getModel()) {
                   str=(mProduct.getModel().getDisplayName());
                }
            mTextConnectionStatus.setText("Status: " + str + " connected");


        } else {

            Log.v(TAG, "refreshSDK: False");
            mBtnOpen.setEnabled(false);

            //mTextProduct.setText(R.string.product_information);
            mTextConnectionStatus.setText(R.string.connection_loose);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_open: {
                Intent intent = new Intent(this, com.dji.dronenavigator.MainActivity.class);
                startActivity(intent);
                break;
            }

            default:
                break;
        }
    }

    private void showToast(final String toastMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();

            }
        });
    }
}

