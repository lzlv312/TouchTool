package top.bogey.touch_tool.bean.action.system;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.google.gson.JsonObject;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionCheckResult;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;

public class OpenActivityAction extends ExecuteAction {
    private final transient Pin activityPin = new Pin(new PinApplication(PinSubType.SINGLE_ACTIVITY), R.string.pin_app);
    private final transient Pin paramsPin = new Pin(new PinMap(new PinString(), new PinString()), R.string.open_activity_action_params);

    public OpenActivityAction() {
        super(ActionType.OPEN_ACTIVITY);
        addPins(activityPin, paramsPin);
    }

    public OpenActivityAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(activityPin, paramsPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinApplication app = getPinValue(runnable, activityPin);
        String packageName = app.getPackageName();
        String activityClass = app.getFirstActivity();

        PinMap map = getPinValue(runnable, paramsPin);
        Bundle params = new Bundle();
        map.forEach((key, value) -> putBundle(params, key.toString(), value.toString()));

        Context context = MainApplication.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            String currAssistant = Settings.Secure.getString(context.getContentResolver(), "assistant");

            ComponentName componentName = new ComponentName(packageName, activityClass);
            String assistant = componentName.flattenToString();

            try {
                Settings.Secure.putString(context.getContentResolver(), "assistant", assistant);
                SearchManager manager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
                if (manager != null) {
                    HiddenApiBypass.invoke(SearchManager.class, manager, "launchAssist", params);
                }
                runnable.sleep(100);
                Settings.Secure.putString(context.getContentResolver(), "assistant", currAssistant);
            } catch (Exception ignored) {
                Settings.Secure.putString(context.getContentResolver(), "assistant", currAssistant);
            }
        }

        executeNext(runnable, outPin);
    }

    private void putBundle(Bundle bundle, String key, String value) {
        if (value == null || value.isEmpty()) return;
        try {
            int i = Integer.parseInt(value);
            bundle.putInt(key, i);
            return;
        } catch (NumberFormatException ignored) {
        }

        try {
            double d = Double.parseDouble(value);
            bundle.putDouble(key, d);
            return;
        } catch (NumberFormatException ignored) {
        }

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            bundle.putBoolean(key, Boolean.parseBoolean(value));
            return;
        }
        bundle.putString(key, value);
    }

    @Override
    public void check(ActionCheckResult result, Task task) {
        super.check(result, task);
        Context context = MainApplication.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            result.addResult(ActionCheckResult.ResultType.ERROR, R.string.check_android_version_9_error);
        } else if (context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            result.addResult(ActionCheckResult.ResultType.ERROR, R.string.check_secure_permission_error);
        }

    }
}
