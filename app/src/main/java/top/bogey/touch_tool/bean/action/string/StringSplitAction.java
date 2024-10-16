package top.bogey.touch_tool.bean.action.string;

import com.google.gson.JsonObject;

import java.util.stream.IntStream;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class StringSplitAction extends CalculateAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin separatorPin = new Pin(new PinString(), R.string.string_split_action_split);
    private final transient Pin emptyPin = new Pin(new PinBoolean(true), R.string.string_split_action_empty);
    private final transient Pin resultPin = new Pin(new PinList(PinType.STRING), R.string.pin_string, true);

    public StringSplitAction() {
        super(ActionType.STRING_SPLIT);
        addPins(textPin, separatorPin, emptyPin, resultPin);
    }

    public StringSplitAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, separatorPin, emptyPin, resultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinList value = resultPin.getValue(PinList.class);

        PinString text = getPinValue(runnable, textPin);
        PinString separator = getPinValue(runnable, separatorPin);
        PinBoolean empty = getPinValue(runnable, emptyPin);

        if (text.getValue() == null) return;

        if (separator.getValue() == null || separator.getValue().isEmpty()) {
            IntStream intStream = text.getValue().codePoints();
            intStream.forEach(cp -> {
                String s = new String(Character.toChars(cp));
                if (empty.getValue()) {
                    s = s.trim();
                    if (s.isEmpty()) return;
                }
                value.getValues().add(new PinString(s));
            });
        } else {
            String[] split = text.getValue().split(separator.getValue());
            for (String s : split) {
                if (empty.getValue()) {
                    s = s.trim();
                    if (s.isEmpty()) return;
                }
                value.getValues().add(new PinString(s));
            }
        }
    }
}
