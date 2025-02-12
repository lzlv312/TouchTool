package top.bogey.touch_tool.bean.action.string;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.AppUtil;

public class StringToNumberAction extends CalculateAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin numberPin = new Pin(new PinDouble(), R.string.pin_number_integer, true);

    public StringToNumberAction() {
        super(ActionType.STRING_TO_NUMBER);
        addPins(textPin, numberPin);
    }

    public StringToNumberAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, numberPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        numberPin.getValue(PinDouble.class).setValue(0.0);
        PinObject text = getPinValue(runnable, textPin);
        if (text.toString().isEmpty()) return;
        Pattern pattern = AppUtil.getPattern("-?\\d+(\\.\\d+)?");
        if (pattern == null) return;
        Matcher matcher = pattern.matcher(text.toString());
        if (matcher.find()) {
            numberPin.getValue(PinDouble.class).setValue(Double.parseDouble(matcher.group()));
        }
    }
}
