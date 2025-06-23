package top.bogey.touch_tool.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.color.DynamicColors;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (SettingSaver.getInstance().isBootCompletedAutoStart()) {
                new KeepAliveFloatView(DynamicColors.wrapContextIfAvailable(context, R.style.Theme_TouchTool)).show();
            }
        }
    }
}
