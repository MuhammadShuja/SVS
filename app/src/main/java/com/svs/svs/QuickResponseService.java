package com.svs.svs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Muhammad on 20/12/2017.
 */
public class QuickResponseService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return super.onStartCommand(intent,flags,startID);
    }
}