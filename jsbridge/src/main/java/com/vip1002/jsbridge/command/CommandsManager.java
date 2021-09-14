package com.vip1002.jsbridge.command;

import android.content.Context;
import android.text.TextUtils;

import com.vip1002.jsbridge.WebConstants;
import com.vip1002.jsbridge.interfaces.Command;
import com.vip1002.jsbridge.interfaces.ResultBack;

import java.util.HashMap;
import java.util.Map;

public class CommandsManager {
    private static CommandsManager instance;
    private UIDependencyCommands uiDependencyCommands;
    private BaseLevelCommands baseLevelCommands;
    private AccountLevelCommands accountLevelCommands;

    private CommandsManager() {
        uiDependencyCommands = new UIDependencyCommands();
        baseLevelCommands = new BaseLevelCommands();
        accountLevelCommands = new AccountLevelCommands();
    }

    public static CommandsManager getInstance() {
        if (instance == null) {
            synchronized (CommandsManager.class) {
                instance = new CommandsManager();
            }
        }
        return instance;
    }

    public void registerCommand(int commandLevel, Command command) {
        switch (commandLevel) {
            case WebConstants.LEVEL_UI:
                uiDependencyCommands.registerCommand(command);
                break;
            case WebConstants.LEVEL_BASE:
                baseLevelCommands.registerCommand(command);
                break;
            case WebConstants.LEVEL_ACCOUNT:
                accountLevelCommands.registerCommand(command);
                break;
        }
    }

    public void findAndExecNonUICommand(Context context, int level, String action, Map params, ResultBack resultBack) {
        boolean methodFlag = false;
        switch (level) {
            case WebConstants.LEVEL_BASE: {
                if (baseLevelCommands.getCommands().get(action) != null) {
                    methodFlag = true;
                    baseLevelCommands.getCommands().get(action).exec(context, params, resultBack);
                }
                break;
            }
            case WebConstants.LEVEL_ACCOUNT: {
                if (accountLevelCommands.getCommands().get(action) != null) {
                    methodFlag = true;
                    accountLevelCommands.getCommands().get(action).exec(context, params, resultBack);
                }
                break;
            }
        }
        if (!methodFlag) {
            HashMap<String, Object> aidlErr = new HashMap<>();
            aidlErr.put(WebConstants.RESULT_CODE, WebConstants.ERRORCODE.NO_METHOD);
            aidlErr.put(WebConstants.RESULT_MESSAGE, WebConstants.ERRORMESSAGE.NO_METHOD);
            String callbackName = (String) params.get(WebConstants.WEB2NATIVE_CALLBACk);
            if (!TextUtils.isEmpty(callbackName)) {
                aidlErr.put(WebConstants.NATIVE2WEB_CALLBACK, callbackName);
            }
            resultBack.onResult(WebConstants.FAILED, action, aidlErr);
        }
    }

    public void findAndExecUICommnad(Context context, int level, String action, Map params, ResultBack resultBack) {
        if (uiDependencyCommands.getCommands().get(action) != null) {
            uiDependencyCommands.getCommands().get(action).exec(context, params, resultBack);
        }
    }

    public boolean checkHitUICommand(int level, String action) {
        return uiDependencyCommands.getCommands().get(action) != null;
    }
}
