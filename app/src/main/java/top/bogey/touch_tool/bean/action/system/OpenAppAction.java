package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.AppUtil;

public class OpenAppAction extends ExecuteAction {
    private final transient Pin appPin = new Pin(new PinApplication(PinSubType.SINGLE_APP_WITH_ACTIVITY), R.string.pin_app);
    private final transient Pin paramsPin = new Pin(new PinMap(PinType.STRING, PinType.STRING, false), R.string.open_app_action_params, false, false, true);

    public OpenAppAction() {
        super(ActionType.OPEN_APP);
        addPins(appPin, paramsPin);
    }

    public OpenAppAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(appPin, paramsPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinApplication app = getPinValue(runnable, appPin);
        PinMap map = getPinValue(runnable, paramsPin);
        Map<String, String> params = new HashMap<>();
        map.forEach((key, value) -> params.put(key.toString(), value.toString()));

        List<String> classes = app.getActivityClasses();
        if (classes == null || classes.isEmpty()) {
            AppUtil.gotoApp(MainApplication.getInstance(), app.getPackageName(), params);
        } else {
            AppUtil.gotoActivity(MainApplication.getInstance(), app.getPackageName(), classes.get(0), params);
        }
        executeNext(runnable, outPin);
    }
}
