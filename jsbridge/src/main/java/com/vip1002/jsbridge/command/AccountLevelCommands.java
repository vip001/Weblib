package com.vip1002.jsbridge.command;

import android.content.Context;
import android.text.TextUtils;

import com.vip1002.jsbridge.WebConstants;
import com.vip1002.jsbridge.interfaces.Command;
import com.vip1002.jsbridge.interfaces.ResultBack;

import java.util.HashMap;
import java.util.Map;

public class AccountLevelCommands extends Commands {
    public AccountLevelCommands() {
        registerCommands();

    }

    void registerCommands() {
            registerCommand(appDataProviderCommand);
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_ACCOUNT;
    }

    private final Command appDataProviderCommand = new Command() {
        @Override
        public String name() {
            return "appDataProvider";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            String callbackName = (String) params.get(WebConstants.WEB2NATIVE_CALLBACk);
            String type = (String) params.get("type");
            HashMap<String, Object> result = new HashMap<>();
            if (!TextUtils.isEmpty(callbackName)) {
                result.put(WebConstants.NATIVE2WEB_CALLBACK, callbackName);
            }
            if (type == null) {
                result.put(WebConstants.RESULT_CODE, WebConstants.ERRORCODE.ERROR_PARAM);
                result.put(WebConstants.RESULT_MESSAGE, WebConstants.ERRORMESSAGE.ERROR_PARAM);
                resultBack.onResult(WebConstants.FAILED, this.name(), result);
                return;
            }

            switch (type) {
                case "account":
                    result.put("accountId", "test123456");
                    result.put("accountName", "xud");
                    break;
            }
            resultBack.onResult(WebConstants.SUCCESS, this.name(), result);
        }
    };
}
