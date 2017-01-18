package com.hkid.remotecamera;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/18/17
 */
public class WearApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
