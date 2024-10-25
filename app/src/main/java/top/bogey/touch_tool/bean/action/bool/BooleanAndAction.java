package top.bogey.touch_tool.bean.action.bool;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class BooleanAndAction extends CalculateAction implements DynamicPinsAction {
    private final static Pin morePin = new Pin(new PinBoolean(), R.string.pin_boolean_condition);
    private final transient Pin firstPin = new Pin(new PinBoolean(), R.string.pin_boolean_condition);
    private final transient Pin secondPin = new Pin(new PinBoolean(), R.string.pin_boolean_condition);
    private final transient Pin addPin = new Pin(new PinAdd(morePin), R.string.pin_add_pin);
    private final transient Pin resultPin = new Pin(new PinBoolean(), R.string.pin_boolean_result, true);

    public BooleanAndAction() {
        super(ActionType.BOOLEAN_AND);
        addPins(firstPin, secondPin, addPin, resultPin);
    }

    public BooleanAndAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(firstPin, secondPin);
        reAddPins(morePin);
        reAddPins(addPin, resultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        boolean result = true;
        for (Pin dynamicPin : getDynamicPins()) {
            PinBoolean value = getPinValue(runnable, dynamicPin);
            result &= value.getValue();
        }
        resultPin.getValue(PinBoolean.class).setValue(result);
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == addPin) start = false;
            if (pin == firstPin) start = true;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
