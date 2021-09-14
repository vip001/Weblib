package com.vip1002.jsbridge;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.vip1002.jsbridge.aidl.RemoteWebBinderPool;
import com.vip1002.jsbridge.command.CommandsManager;
import com.vip1002.jsbridge.interfaces.Action;
import com.vip1002.jsbridge.interfaces.ResultBack;
import com.vip1002.jsbridge.view.DWebView;
import com.weblib.webview.IWebAidlCallback;
import com.weblib.webview.IWebAidlInterface;

import java.util.Map;

public class CommandDispatcher {
    private static CommandDispatcher instance;
    private Gson gson = new Gson();
    protected IWebAidlInterface webAidlInterface;

    private CommandDispatcher() {

    }

    public static CommandDispatcher getInstance() {
        if (instance == null) {
            synchronized (CommandDispatcher.class) {
                if (instance == null) {
                    instance = new CommandDispatcher();
                }
            }
        }
        return instance;
    }

    public IWebAidlInterface getWebAidlInterface(Context context) {
        if (webAidlInterface == null) {
            initAidlConnect(context, null);
        }
        return webAidlInterface;
    }

    public void initAidlConnect(final Context context, final Action action) {
        if (webAidlInterface != null || SystemInfoUtil.isMainProcess(context, android.os.Process.myPid())) {
            if (action != null) {
                MainLooper.runOnUiThread(
                        () -> {
                            action.call(null);
                        }
                );
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                RemoteWebBinderPool binderPool = RemoteWebBinderPool.getInstance(context);
                IBinder iBinder = binderPool.queryBinder(RemoteWebBinderPool.BINDER_WEB_AIDL);
                webAidlInterface = IWebAidlInterface.Stub.asInterface(iBinder);
                if (action != null) {
                    MainLooper.runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    action.call(null);
                                }
                            }
                    );
                }
            }
        }).start();
    }

    public void exec(Context context, int commandLevel, String cmd, String params, WebView webView, DispatcherCallBack dispatcherCallBack) {
        try {
            if (CommandsManager.getInstance().checkHitUICommand(commandLevel, cmd)) {
                execUI(context, commandLevel, cmd, params, webView, dispatcherCallBack);
            } else {
                execNonUI(context, commandLevel, cmd, params, webView, dispatcherCallBack);
            }
        } catch (Exception e) {

        }


    }

    private void execNonUI(Context context, int commandLevel, String cmd, String params, WebView webView, DispatcherCallBack dispatcherCallBack) throws RemoteException {
        Map mapParams = gson.fromJson(params, Map.class);
        if (SystemInfoUtil.isMainProcess(context, android.os.Process.myPid())) {
            CommandsManager.getInstance().findAndExecNonUICommand(context, commandLevel, cmd, mapParams,
                    new ResultBack() {
                        @Override
                        public void onResult(int status, String action, Map result) {
                            handleCallback(status, action, gson.toJson(result), webView, dispatcherCallBack);
                        }
                    });
        } else {
            if (webAidlInterface != null) {
                webAidlInterface.handleWebAction(commandLevel, cmd, params, new IWebAidlCallback.Stub() {

                    @Override
                    public void onResult(int responseCode, String actionName, String response) throws RemoteException {
                        handleCallback(responseCode, actionName, response, webView, dispatcherCallBack);
                    }
                });
            }
        }
    }

    private void execUI(final Context context, final int commandLevel, final String cmd,
                        final String params, final WebView webView, final DispatcherCallBack dispatcherCallBack) {
        Map mapParams = gson.fromJson(params, Map.class);
        CommandsManager.getInstance().findAndExecUICommnad(context, commandLevel, cmd, mapParams, new ResultBack() {
            @Override
            public void onResult(int status, String action, Map result) {
                try {
                    handleCallback(status, action, gson.toJson(result), webView, dispatcherCallBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleCallback(int status, String action, String response, WebView webView, DispatcherCallBack dispatcherCallBack) {
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dispatcherCallBack != null && dispatcherCallBack.preHandleBeforeCallback(status, action, response)) {
                    return;
                }
                if (webView instanceof DWebView) {
                    ((DWebView) webView).handleCallback(response);
                }
            }
        });
    }

    /**
     * Dispatcher 过程中的回调介入
     */
    public interface DispatcherCallBack {
        boolean preHandleBeforeCallback(int responseCode, String actionName, String response);
    }


}
