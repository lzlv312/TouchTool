package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.utils.AppUtil;

public class OpenUriAction extends ExecuteAction {
    private final transient Pin uriPin = new Pin(new PinString(), R.string.open_uri_action_uri);

    public OpenUriAction() {
        super(ActionType.OPEN_URI);
        addPins(uriPin);
    }

    public OpenUriAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(uriPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinString uri = getPinValue(runnable, uriPin);
        AppUtil.gotoScheme(MainApplication.getInstance(), uri.getValue());
        executeNext(runnable, outPin);
    }
}
