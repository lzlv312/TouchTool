package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.service.TaskRunnable;

public class CustomEndAction extends Action implements DynamicPinsAction {
    private final transient Pin executePin = new Pin(new PinExecute(), 0);

    public CustomEndAction() {
        super(ActionType.CUSTOM_END);
        addPin(executePin);
    }

    public CustomEndAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(executePin);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        Map<String, PinObject> params = new HashMap<>();
        for (Pin p : getDynamicPins()) {
            if (!p.isOut()) {
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

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == executePin) start = true;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
