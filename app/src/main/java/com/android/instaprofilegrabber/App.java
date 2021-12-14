package com.android.instaprofilegrabber;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ramin on 1/27/2017.
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
