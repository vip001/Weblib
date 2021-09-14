package com.vip1002.jsbridge.interfaces;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

public final class JSRemoteInterface {
    private final Context mContext;
    private final Handler mHandler = new Handler();
    private AidlCommand aidlCommand;
    public JSRemoteInterface(Context context){
        mContext = context;
    }
    @JavascriptInterface
    public void post(final String cmd,final String param){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if(aidlCommand != null){
                        aidlCommand.exec(mContext,cmd,param);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    public void setAidlCommand(AidlCommand aidlCommand){
        this.aidlCommand = aidlCommand;
    }

    public interface AidlCommand{
        void exec(Context context,String cmd,String params);
    }

}
