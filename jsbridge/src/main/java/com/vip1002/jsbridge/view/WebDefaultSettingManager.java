package com.vip1002.jsbridge.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.vip1002.jsbridge.BuildConfig;


public class WebDefaultSettingManager {
    private WebSettings mWebSettings;

    public static WebDefaultSettingManager newInstance() {
        return new WebDefaultSettingManager();
    }

    protected WebDefaultSettingManager() {

    }

    public WebDefaultSettingManager toSetting(WebView webView) {
        settings(webView);
        return this;
    }

    public WebSettings getWebSettings() {
        return mWebSettings;
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            boolean a = networkInfo.isConnected();
            return a;
        } else {
            return false;
        }
    }

    private void settings(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);

        if (isNetworkConnected(webView.getContext())) {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebSettings.setTextZoom(100);

        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDomStorageEnabled(true);


        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setBlockNetworkImage(false);
        mWebSettings.setAllowFileAccess(true);//允许加载本地文件html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            mWebSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        }

        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        mWebSettings.setSavePassword(false);
        mWebSettings.setSaveFormData(false);

        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(10);
        mWebSettings.setGeolocationEnabled(true);

        String appCacheDir = webView.getContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        mWebSettings.setDatabasePath(appCacheDir);
        mWebSettings.setAppCachePath(appCacheDir);
        mWebSettings.setAppCacheMaxSize(1024 * 1024 * 80);

        mWebSettings.setUserAgentString("webprogress/build you agent info");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
    }

}
