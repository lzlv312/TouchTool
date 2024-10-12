package top.bogey.touch_tool.bean.action.string;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class StringEqualAction extends CalculateAction {
    private final transient Pin firstPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin secondPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin ignoreCasePin = new Pin(new PinBoolean(), R.string.string_equal_action_ignore_case);
    private final transient Pin resultPin = new Pin(new PinBoolean(), R.string.pin_boolean_result, true);

    public StringEqualAction() {
        super(ActionType.STRING_EQUAL);
        addPins(firstPin, secondPin, ignoreCasePin, resultPin);
    }

    public StringEqualAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(firstPin, secondPin, ignoreCasePin, resultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinString first = getPinValue(runnable, firstPin);
        PinString second = getPinValue(runnable, secondPin);
        PinBoolean ignoreCase = getPinValue(runnable, ignoreCasePin);

        boolean result;
        if (ignoreCase.getValue()) {
            String firstValue = first.getValue();
            String secondValue = second.getValue();
            if (firstValue == null && secondValue == null) result = true;
            else if (firstValue == null || secondValue == null) result = false;
            else result = firstValue.equalsIgnoreCase(secondValue);
        } else {
            result = Objects.equals(first.getValue(), second.getValue());
        }

        resultPin.getValue(PinBoolean.class).setValue(result);
    }
}
