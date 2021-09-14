package com.vip1002.jsbridge.aidl;

import android.content.Context;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.vip1002.jsbridge.command.CommandsManager;
import com.vip1002.jsbridge.interfaces.ResultBack;
import com.weblib.webview.IWebAidlCallback;
import com.weblib.webview.IWebAidlInterface;

import java.util.Map;

public class MainProAidlInterface extends IWebAidlInterface.Stub {
    private Context context;
    public MainProAidlInterface(Context context){
        this.context = context;
    }
    @Override
    public void handleWebAction(int level, String actionName, String jsonParams, IWebAidlCallback callback) throws RemoteException {
        try{
            handleRemoteAction(level,actionName,new Gson().fromJson(jsonParams,Map.class),callback);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void handleRemoteAction(int level, final String actionName, Map paramMap,final IWebAidlCallback callback){
        CommandsManager.getInstance().findAndExecNonUICommand(context, level, actionName, paramMap, new ResultBack() {
            @Override
            public void onResult(int status, String action, Map result) {
                try {
                    if(callback != null){
                        callback.onResult(status,actionName,new Gson().toJson(result));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }
}
