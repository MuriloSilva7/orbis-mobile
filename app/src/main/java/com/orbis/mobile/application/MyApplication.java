package com.orbis.mobile.application;

import android.app.Application;

import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        OneSignal.initWithContext(this);
    }
}