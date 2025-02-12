package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.service.TaskRunnable;

public class CustomEndAction extends Action implements DynamicPinsAction {
    private final transient Pin executePin = new Pin(new PinExecute(), R.string.pin_execute);
    private final transient Pin justCallPin = new NotLinkAblePin(new PinBoolean(false), R.string.execute_task_action_just_cal);

    public CustomEndAction() {
        super(ActionType.CUSTOM_END);
        addPins(executePin, justCallPin);
    }

    public CustomEndAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(executePin, justCallPin);
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
    public void resetReturnValue(TaskRunnable runnable) {

    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (start) pins.add(pin);
            if (pin == justCallPin) start = true;
        }
        return pins;
    }

    public boolean isJustCall() {
        PinBoolean justCall = justCallPin.getValue();
        return justCall.getValue();
    }
}
