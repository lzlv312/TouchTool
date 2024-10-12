package top.bogey.touch_tool.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinNumber;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class NumberModAction extends NumberAction{
    public NumberModAction() {
        super(ActionType.NUMBER_MOD);
    }

    public NumberModAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinNumber<?> first = getPinValue(runnable, firstPin);
        PinNumber<?> second = getPinValue(runnable, secondPin);
        if (second.doubleValue() == 0) return;
        resultPin.getValue(PinDouble.class).setValue(first.doubleValue() % second.doubleValue());
    }
}
