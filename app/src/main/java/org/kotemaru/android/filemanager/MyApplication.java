package org.kotemaru.android.filemanager;


import android.app.Application;

import org.kotemaru.android.filemanager.persistent.Settings;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Settings.getSharedPrefs(); // init.
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
