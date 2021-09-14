package com.vip1002.jsbridge.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.vip1002.jsbridge.interfaces.DWebViewCallback;
import com.vip1002.jsbridge.interfaces.JSRemoteInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DWebView extends WebView {
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final String TAG = "DWebView";
    public static final String CONTENT_SCHEME = "file:///android_asset/";
    protected Context context;

    boolean isReady;

    private DWebViewCallback dWebViewCallBack;

    private Map<String, String> mHeaders;

    private JSRemoteInterface remoteInterface = null;

    public DWebView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public DWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init(context);
    }

    public void registerWebViewCallback(DWebViewCallback dWebViewCallBack) {
        this.dWebViewCallBack = dWebViewCallBack;
    }

    public void setHeaders(Map<String, String> headers) {
        this.mHeaders = headers;
    }

    protected void init(Context context) {
        this.context = context;
        WebDefaultSettingManager.newInstance().toSetting(this);
        setWebViewClient(new DWebViewClient());
        if (remoteInterface == null) {
            remoteInterface = new JSRemoteInterface(context);
            remoteInterface.setAidlCommand(new JSRemoteInterface.AidlCommand() {
                @Override
                public void exec(Context context, String cmd, String params) {
                    if (dWebViewCallBack != null) {
                        dWebViewCallBack.exec(context, dWebViewCallBack.getCommandLevel(), cmd, params, DWebView.this);
                    }
                }
            });
        }
        setJavascriptInterface(remoteInterface);
        // setWebViewClient(n);
    }

    public void setContent(String htmlContent) {
        try {
            loadDataWithBaseURL(CONTENT_SCHEME, htmlContent, "text/html", "UTF-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setJavascriptInterface(JSRemoteInterface obj) {
        addJavascriptInterface(obj, "webview");
    }

    public void exec(String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            new WaitLoad(trigger).executeOnExecutor(THREAD_POOL);
        }
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    private class WaitLoad extends AsyncTask<Void, Void, Void> {
        private String mTrigger;

        public WaitLoad(String trigger) {
            super();
            mTrigger = trigger;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!DWebView.this.isReady) {
                sleep(100);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            load(mTrigger);
        }

        private synchronized void sleep(long ms) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void loadUrl(@NonNull String url) {
        super.loadUrl(url);
        resetAllStateInternal(url);
    }

    @Override
    public void loadUrl(@NonNull String url, @NonNull Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
        resetAllStateInternal(url);
    }

    public void handleCallback(String response) {
        if (!TextUtils.isEmpty(response)) {
            String trigger = "javascript:" + "dj.callback" + "(" + response + ")";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                evaluateJavascript(trigger, null);
            } else {
                loadUrl(trigger);
            }
        }
    }
    public void loadJs(String cmd,Object params){
        String trigger = "javascript:"+cmd+"("+ new Gson().toJson(params)+")";
        if(!TextUtils.isEmpty(trigger)){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                evaluateJavascript(trigger,null);
            }else{
                loadUrl(trigger);
            }
        }
    }
    public void dispatchEvent(String name){
        Map<String,String> param = new HashMap<>();
        param.put("name",name);
        dispatchEvent(param);
    }

    public void dispatchEvent(Map params){
        Map<String,String> param = new HashMap<>();
       loadJs("dj.dispatchEvent",params);
    }



    private boolean mTouchByUser;

    public boolean isTouchByUser() {
        return mTouchByUser;
    }
    private void resetAllStateInternal(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("javascript:")) {
            return;
        }
        mTouchByUser = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchByUser = true;
                break;
        }
        return super.onTouchEvent(event);
    }

    public class DWebViewClient extends WebViewClient {
        public static final String SCHEME_SMS = "sms:";


        private boolean handleLinked(String url) {
            if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith(SCHEME_SMS) ||
                    url.startsWith(WebView.SCHEME_MAILTO)
                    || url.startsWith(WebView.SCHEME_GEO)
            ) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!TextUtils.isEmpty(url) && url.startsWith(CONTENT_SCHEME)) {
                isReady = true;
            }
            if (dWebViewCallBack != null) {
                dWebViewCallBack.pageFinished(url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!TextUtils.isEmpty(url) && url.startsWith(CONTENT_SCHEME)) {
                isReady = false;
            }
            if (dWebViewCallBack != null) {
                dWebViewCallBack.pageStarted(url);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (dWebViewCallBack != null) {
                dWebViewCallBack.onError();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(!isTouchByUser()){
                return super.shouldOverrideUrlLoading(view,url);
            }
            if(getUrl().equals(url)){
                return super.shouldOverrideUrlLoading(view,url);
            }
            if(handleLinked(url)){
                return true;
            }
            if (dWebViewCallBack != null && dWebViewCallBack.overrideUrlLoading(view, url)) {
                return true;
            }
            // 控制页面中点开新的链接在当前webView中打开
            view.loadUrl(url, mHeaders);
            return true;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (!isTouchByUser()) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            if (getUrl().equals(request.getUrl().toString())) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            if (handleLinked(request.getUrl().toString())) {
                return true;
            }
            if (dWebViewCallBack != null && dWebViewCallBack.overrideUrlLoading(view, request.getUrl().toString())) {
                return true;
            }
            // 控制页面中点开新的链接在当前webView中打开
            view.loadUrl(request.getUrl().toString(), mHeaders);
            return true;

        }
    }

    public boolean isReady() {
        return isReady;
    }
}
