package com.orbis.mobile.application;

import android.app.Application;

import com.onesignal.OneSignal;


public class MyApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "d7883972-6f04-4f2a-a40c-6b1d14d5aa18";

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE,
                OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);

        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}