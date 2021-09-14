package com.vip1002.jsbridge.command;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.vip1002.jsbridge.WebConstants;
import com.vip1002.jsbridge.WebUtils;
import com.vip1002.jsbridge.interfaces.Command;
import com.vip1002.jsbridge.interfaces.ResultBack;

import java.util.List;
import java.util.Map;

public class UIDependencyCommands extends Commands {
    public UIDependencyCommands() {
        super();
        registCommands();

    }

    void registCommands() {
        registerCommand(showToastCommand);
        registerCommand(showDialogCommand);
    }

    @Override
    int getCommandLevel() {
        return WebConstants.LEVEL_UI;
    }

    private final Command showToastCommand = new Command() {
        @Override
        public String name() {
            return "showToast";
        }

        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            Toast.makeText(context, String.valueOf(params.get("message")), Toast.LENGTH_SHORT).show();
        }
    };
    private final Command showDialogCommand = new Command() {
        @Override
        public String name() {
            return "showDialog";
        }



        @Override
        public void exec(Context context, Map params, ResultBack resultBack) {
            if (WebUtils.isNotNull(params)) {
                String title = (String) params.get("title");
                String content = (String) params.get("content");
                int canceledOutside = 1;
                if (params.get("canceledOutside") != null) {
                    canceledOutside = (int) params.get("canceledOutside");
                }
                List<Map<String, Object>> buttons = (List<Map<String, Object>>) params.get("buttons");
                final String callbackName = (String) params.get(WebConstants.WEB2NATIVE_CALLBACk);
                if (!TextUtils.isEmpty(content)) {
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(content)
                            .create();
                    dialog.setCanceledOnTouchOutside(canceledOutside == 1);

                    if (WebUtils.isNotNull(buttons)) {
                        for (int i = 0; i < buttons.size(); i++) {
                            final Map<String,Object> button = buttons.get(i);
                            int buttonWhich = getDialogButtonWhich(i);
                            if(buttonWhich == 0) return;
                            dialog.setButton(buttonWhich, (String) button.get("title"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    button.put(WebConstants.NATIVE2WEB_CALLBACK,callbackName);
                                    resultBack.onResult(WebConstants.SUCCESS,name(),button);
                                }
                            });
                        }
                    }
                    dialog.show();
                }

            }

        }

        private int getDialogButtonWhich(int index){
            switch (index){
                case 0:
                    return DialogInterface.BUTTON_POSITIVE;
                case 1:
                    return DialogInterface.BUTTON_NEGATIVE;
                case 2:
                    return DialogInterface.BUTTON_NEUTRAL;
            }
            return 0;
        }
    };
}
