package com.dji.dronenavigator;

import android.app.Application;
import android.content.Context;

import com.secneo.sdk.Helper;

public class MApplication extends Application {
    private RegnmoreApplication regnmore;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
        if (regnmore == null) {
            regnmore = new RegnmoreApplication();
            regnmore.setContext(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        regnmore.onCreate();
    }

}
