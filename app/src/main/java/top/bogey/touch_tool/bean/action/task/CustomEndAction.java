package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class CustomEndAction extends Action {
    private final transient Pin executePin = new Pin(new PinExecute(), 0);

    public CustomEndAction(ActionType type) {
        super(type);
        addPins(executePin);
    }

    public CustomEndAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(executePin);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        Map<String, PinObject> params = new HashMap<>();
        for (Pin p : getPins()) {
            if (!p.isOut() && p.getValue() instanceof PinObject) {
                PinObject value = getPinValue(runnable, p);
                params.put(p.getUid(), value);
            }
        }
        runnable.getTask().executeNext(runnable, params);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {

    }

    @Override
    public void resetReturnValue() {

    }
}
