package com.java.eventfy;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by swapnil on 4/22/17.
 */

public class EventefyApplication  extends Application {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }


}
