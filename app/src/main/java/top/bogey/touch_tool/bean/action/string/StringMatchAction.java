package top.bogey.touch_tool.bean.action.string;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleLineString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.special_pin.AlwaysShowPin;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.AppUtil;

public class StringMatchAction extends ExecuteAction implements DynamicPinsAction {
    private final static Pin morePin = new Pin(new PinString(), R.string.string_match_action_match_result, true);

    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin matchPin = new Pin(new PinSingleLineString(), R.string.string_match_action_match);
    private final transient Pin resultPin = new Pin(new PinBoolean(), R.string.pin_boolean_result, true);
    private final transient Pin addPin = new AlwaysShowPin(new PinAdd(morePin), R.string.pin_add_pin, true);

    public StringMatchAction() {
        super(ActionType.STRING_REGEX);
        addPins(textPin, matchPin, resultPin, addPin);
    }

    public StringMatchAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, matchPin, resultPin);
        reAddPins(morePin);
        reAddPin(addPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinObject text = getPinValue(runnable, textPin);
        PinObject match = getPinValue(runnable, matchPin);

        Pattern pattern = AppUtil.getPattern(match.toString());
        if (pattern == null) {
            resultPin.getValue(PinBoolean.class).setValue(false);
        } else {
            Matcher matcher = pattern.matcher(text.toString());
            if (matcher.find()) {
                resultPin.getValue(PinBoolean.class).setValue(true);
                List<Pin> pins = getDynamicPins();
                for (int i = 0; i < matcher.groupCount(); i++) {
                    if (i >= pins.size()) break;
                    String group = matcher.group(i + 1);
                    pins.get(i).getValue(PinString.class).setValue(group);
                }
            }
        }
        executeNext(runnable, outPin);
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
            if (pin == resultPin) start = true;
        }
        return pins;
    }
}
