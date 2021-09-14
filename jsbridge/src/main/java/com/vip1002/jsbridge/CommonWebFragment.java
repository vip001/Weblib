package com.vip1002.jsbridge;

import android.os.Bundle;

public class CommonWebFragment extends BaseWebviewFragment{
    public static CommonWebFragment newInstance(String url){
        CommonWebFragment fragment = new CommonWebFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(WebConstants.INTENT_TAG_URL,url);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_common_webview;
    }

    @Override
    public int getCommandLevel() {
        return WebConstants.LEVEL_BASE;
    }
}
