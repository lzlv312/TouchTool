package top.bogey.touch_tool.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinValueArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class NumberToValueArea extends CalculateAction {
    private final transient Pin firstPin = new Pin(new PinInteger(), R.string.pin_number_integer);
    private final transient Pin secondPin = new Pin(new PinInteger(), R.string.pin_number_integer);
    private final transient Pin valueAreaPin = new Pin(new PinValueArea(), R.string.Pin_value_area, true);

    public NumberToValueArea() {
        super(ActionType.NUMBER_TO_VALUE_AREA);
        addPins(firstPin, secondPin, valueAreaPin);
    }

    public NumberToValueArea(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(firstPin, secondPin, valueAreaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinNumber<?> first = getPinValue(runnable, firstPin);
        PinNumber<?> second = getPinValue(runnable, secondPin);
        PinValueArea valueArea = valueAreaPin.getValue(PinValueArea.class);
        valueArea.setMin(first.intValue());
        valueArea.setMax(second.intValue());
    }
}
