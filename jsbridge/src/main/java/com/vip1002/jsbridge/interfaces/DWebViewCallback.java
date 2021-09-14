package com.vip1002.jsbridge.interfaces;

import android.content.Context;
import android.webkit.WebView;

public interface DWebViewCallback {
    int getCommandLevel();

    void pageStarted(String url);

    void pageFinished(String url);

    boolean overrideUrlLoading(WebView view, String url);

    void onError();

    void exec(Context context, int commandLevel, String cmd, String params, WebView webView);
}
