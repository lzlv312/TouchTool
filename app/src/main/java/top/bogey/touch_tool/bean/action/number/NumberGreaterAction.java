package top.bogey.touch_tool.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class NumberGreaterAction extends NumberResultAction{

    public NumberGreaterAction() {
        super(ActionType.NUMBER_GREATER);
    }

    public NumberGreaterAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinNumber<?> first = getPinValue(runnable, firstPin);
        PinNumber<?> second = getPinValue(runnable, secondPin);
        boolean result = first.doubleValue() > second.doubleValue();
        resultPin.getValue(PinBoolean.class).setValue(result);
    }
}
