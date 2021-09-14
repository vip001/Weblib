package com.vip1002.jsbridge.aidl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MainProHandleRemoteService extends Service {
    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new RemoteWebBinderPool.BinderPoolImpl(context);
    }
}
