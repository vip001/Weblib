package com.vip1002.weblib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.vip1002.jsbridge.CommandDispatcher;
import com.vip1002.jsbridge.WebActivity;
import com.vip1002.jsbridge.WebConstants;
import com.vip1002.jsbridge.view.DWebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.openWeb1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.start(MainActivity.this, "腾讯网", "https://xw.qq.com/?f=qqcom", WebConstants.LEVEL_BASE);
            }
        });
        findViewById(R.id.openWeb2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for baselevel
                //RemoteCommonWebActivity.start(MainActivity.this, "AIDL测试", DWebView.CONTENT_SCHEME + "aidl.html");

                // for account level
                WebActivity.start(MainActivity.this, "AIDL测试", DWebView.CONTENT_SCHEME + "aidl.html", WebConstants.LEVEL_ACCOUNT);
            }
        });

    }
}