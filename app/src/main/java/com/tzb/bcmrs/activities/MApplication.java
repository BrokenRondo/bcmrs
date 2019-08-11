package com.tzb.bcmrs.activities;

import android.app.Application;

public class MApplication extends Application {
    private static MApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static MApplication getContext() {
        return mApplication;
    }
}
