package top.bogey.touch_tool.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.service.TaskRunnable;

public class NumberRandomAction extends CalculateAction {
    protected final transient Pin firstPin = new Pin(new PinDouble(), R.string.pin_value_area_min);
    protected final transient Pin secondPin = new Pin(new PinDouble(), R.string.pin_value_area_max);
    protected final transient Pin resultPin = new Pin(new PinDouble(), R.string.pin_number_double, true);

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
