package com.vip1002.jsbridge.command;

import android.content.Context;

import com.vip1002.jsbridge.WebConstants;
import com.vip1002.jsbridge.interfaces.Command;
import com.vip1002.jsbridge.interfaces.ResultBack;

import java.util.Map;

public class BaseLevelCommands extends Commands{
    public BaseLevelCommands(){
        registerCommands();
    }
    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_BASE;
    }
    void registerCommands(){
        registerCommand(pageRouterCommand);
    }
    private final Command pageRouterCommand = new Command() {
        @Override
        public String name() {
            return "newPage";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            String newUrl = params.get("url").toString();
            String title = (String) params.get("title");
        }
    };
}
