package com.vip1002.jsbridge;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class SystemInfoUtil {
    public static boolean isMainProcess(Context context,int pid){
        String packageName = context.getPackageName();
        String processName = getProcessName(context,pid);
        return packageName.equals(processName);

    }

    private static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if(runningApps == null){
            return null;
        }
        for(ActivityManager.RunningAppProcessInfo procInfo:runningApps){
            if(procInfo.pid == pid){
                return procInfo.processName;
            }
        }
        return null;
    }
}
