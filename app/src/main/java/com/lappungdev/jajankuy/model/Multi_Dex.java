package com.lappungdev.jajankuy.model;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class Multi_Dex extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/font.ttf");
    }
}