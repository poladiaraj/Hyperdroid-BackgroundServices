package com.example.nikhil.hyperdroid_backgroundservices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by nikhil on 10/10/17.
 */

public class MyService extends Service {

    private Thread thread;
    private Handler mhandler=null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mhandler = new Handler();
        startRepeatingTask();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable m_statusChecker = new Runnable()
    {
        @Override
        public void run() {
            repeatingTask(); //this function can change value of m_interval.
            mhandler.postDelayed(m_statusChecker, MainActivity.minterval);
        }
    };
    public void startRepeatingTask()
    {
        m_statusChecker.run();
    }

    public void stopRepeatingTask()
    {
        mhandler.removeCallbacks(m_statusChecker);
    }

    public void repeatingTask()
    {
        MainActivity.UpdateTime();
    }
}
