package com.hkid.remotecamera.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/14/16
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
