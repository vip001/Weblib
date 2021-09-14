package com.vip1002.jsbridge.view;

import android.os.Handler;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


public class ProgessWebChromeClient extends WebChromeClient {
    private Handler progressHandler;
    public ProgessWebChromeClient(Handler progressHandler){
        this.progressHandler = progressHandler;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        Message message = Message.obtain();
        if(newProgress == 100){
            message.obj = newProgress;
            progressHandler.sendMessageDelayed(message,200);
        }else{
            if(newProgress < 10){
                newProgress = 10;
            }
            message.obj = newProgress;
            progressHandler.sendMessage(message);
        }
        super.onProgressChanged(view, newProgress);
    }
}
