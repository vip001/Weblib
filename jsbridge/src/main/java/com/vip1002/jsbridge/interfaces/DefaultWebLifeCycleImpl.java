package com.vip1002.jsbridge.interfaces;

import android.webkit.WebView;

public class DefaultWebLifeCycleImpl implements WebLifeCycle {
    private WebView mWebview;
    public DefaultWebLifeCycleImpl(WebView webView){
        this.mWebview = webView;
    }
    @Override
    public void onResume() {
        if(this.mWebview != null){
            this.mWebview.onResume();
        }
    }

    @Override
    public void onPause() {
        if(this.mWebview != null){
            this.mWebview.onPause();
        }
    }

    @Override
    public void onDestroy() {

    }
}
