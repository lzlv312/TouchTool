package top.bogey.touch_tool.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinNumber;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class NumberRandomAction extends NumberAction{

    public NumberRandomAction() {
        super(ActionType.NUMBER_RANDOM);
        secondPin.getValue(PinDouble.class).setValue(1.0);
    }

    public NumberRandomAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinNumber<?> first = getPinValue(runnable, firstPin);
        PinNumber<?> second = getPinValue(runnable, secondPin);
        double result = Math.random() * (second.doubleValue() - first.doubleValue()) + first.doubleValue();
        resultPin.getValue(PinDouble.class).setValue(result);
    }
}
