package top.bogey.touch_tool.bean.action.string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class StringReplaceAction extends CalculateAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin findPin = new Pin(new PinString(), R.string.string_replace_action_find);
    private final transient Pin replacePin = new Pin(new PinString(), R.string.string_replace_action_replace);
    private final transient Pin resultPin = new Pin(new PinString(), R.string.string_replace_action_result, true);

    public StringReplaceAction() {
        super(ActionType.STRING_REPLACE);
        addPins(textPin, findPin, replacePin, resultPin);
    }

    public StringReplaceAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, findPin, replacePin, resultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinString text = getPinValue(runnable, textPin);
        PinString find = getPinValue(runnable, findPin);
        PinString replace = getPinValue(runnable, replacePin);

        if (text.getValue() == null) return;
        if (find.getValue() == null || find.getValue().isEmpty() || replace.getValue() == null || replace.getValue().isEmpty()) {
            resultPin.getValue(PinString.class).setValue(text.getValue());
        } else {
            String string = text.getValue().replace(find.getValue(), replace.getValue());
            resultPin.getValue(PinString.class).setValue(string);
        }
    }
}
