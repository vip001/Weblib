package com.vip1002.jsbridge.interfaces;

import android.content.Context;

import java.util.Map;

public interface Command {
    String name();
    void exec(Context context, Map params,ResultBack resultBack);
}
