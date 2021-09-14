package com.vip1002.jsbridge;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vip1002.jsbridge.interfaces.Action;
import com.vip1002.jsbridge.interfaces.DWebViewCallback;
import com.vip1002.jsbridge.interfaces.DefaultWebLifeCycleImpl;
import com.vip1002.jsbridge.interfaces.WebLifeCycle;
import com.vip1002.jsbridge.view.DWebView;


public abstract class BaseWebviewFragment extends Fragment implements DWebViewCallback {
    protected WebLifeCycle webLifeCycle;
    protected DWebView webView;
    public String webUrl;

    protected abstract int getLayoutRes();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            webUrl = bundle.getString(WebConstants.INTENT_TAG_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        webView = view.findViewById(R.id.web_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webLifeCycle = new DefaultWebLifeCycleImpl(webView);
        webView.registerWebViewCallback(this);
        CommandDispatcher.getInstance().initAidlConnect(getContext(), new Action() {
            @Override
            public void call(Object o) {
                loadUrl();
            }
        });
    }

    protected void loadUrl() {
        webView.loadUrl(webUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.dispatchEvent("pageResume");
        webLifeCycle.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.dispatchEvent("pagePause");
        webLifeCycle.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        webView.dispatchEvent("pageStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView.dispatchEvent("pageDestroy");
        webLifeCycle.onDestroy();
        clearWebView(webView);
    }

    @Override
    public int getCommandLevel() {
        return WebConstants.LEVEL_BASE;
    }

    @Override
    public void pageStarted(String url) {

    }

    @Override
    public void pageFinished(String url) {

    }

    @Override
    public boolean overrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onError() {

    }

    protected CommandDispatcher.DispatcherCallBack getDispatcherCallback() {
        return null;
    }


    @Override
    public void exec(Context context, int commandLevel, String cmd, String params, WebView webView) {
        CommandDispatcher.getInstance().exec(context, commandLevel, cmd, params, webView, getDispatcherCallback());
    }

    private void clearWebView(WebView webView) {
        if (webView == null) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        webView.stopLoading();
        if (webView.getHandler() != null) {
            webView.getHandler().removeCallbacksAndMessages(null);
        }
        webView.removeAllViews();
        ViewGroup viewGroup = (ViewGroup) webView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(webView);
        }
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.setTag(null);
        webView.clearHistory();
        webView.destroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return onBackHandle();
        }
        return false;
    }

    protected boolean onBackHandle() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
