package com.vip1002.jsbridge.interfaces;

import java.util.Map;

public interface ResultBack {
    void onResult(int status, String action, Map<String,Object> result);
}
