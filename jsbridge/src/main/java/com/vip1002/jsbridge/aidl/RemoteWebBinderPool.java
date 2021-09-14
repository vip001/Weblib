package com.vip1002.jsbridge.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;


import com.weblib.webview.IBinderPool;

import java.util.concurrent.CountDownLatch;

public class RemoteWebBinderPool {
    public static final int BINDER_WEB_AIDL = 1;
    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile RemoteWebBinderPool sInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;



    private RemoteWebBinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();

    }

    public static RemoteWebBinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (RemoteWebBinderPool.class) {
                if (sInstance == null) {
                    sInstance = new RemoteWebBinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, MainProHandleRemoteService.class);
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }
    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();

            //mBinderPool.asBinder().linkToDeath();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();

        }
    };
    public static class BinderPoolImpl extends IBinderPool.Stub {
        private Context context;

        public BinderPoolImpl(Context context) {
            this.context = context;
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_WEB_AIDL: {
                    binder = new MainProAidlInterface(context);
                    break;
                }
            }
            return binder;
        }
    }
}
