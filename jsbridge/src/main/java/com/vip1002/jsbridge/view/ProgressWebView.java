package com.vip1002.jsbridge.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


public class ProgressWebView extends DWebView {
    public ProgressWebView(Context context) {
        super(context);
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private WebProgressBar progressBar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int newProgress = (int) msg.obj;
            if (newProgress == 0) {
                progressBar.reset();
            } else if (newProgress > 0 && newProgress <= 10) {
                progressBar.show();
            } else if (newProgress > 10 && newProgress < 95) {
                progressBar.setProgress(newProgress);
            } else {
                progressBar.setProgress(newProgress);
                progressBar.hide();
            }
        }
    };

    @Override
    public Handler getHandler() {
        return handler;
    }

    private void init() {
        progressBar = new WebProgressBar(context);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setVisibility(GONE);
        addView(progressBar);
        setWebChromeClient(new ProgessWebChromeClient(handler));
    }
}
