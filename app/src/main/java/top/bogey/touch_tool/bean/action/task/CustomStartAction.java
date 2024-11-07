package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.service.TaskRunnable;

public class CustomStartAction extends Action {
    private final transient Pin executePin = new Pin(new PinExecute(), 0, true);

    public CustomStartAction(ActionType type) {
        super(type);
        addPin(executePin);
    }

    public CustomStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(executePin);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        executeNext(runnable, executePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {

    }

    @Override
    public void resetReturnValue() {

    }

    public void setParams(Map<String, PinObject> params) {
        if (params == null) return;
        params.forEach((key, value) -> {
            Pin pin = getPinByUid(key);
            if (pin == null) return;
            pin.setValue(value);
        });
    }
}
