package top.bogey.touch_tool.bean.action.string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;

public class StringSubStringAction extends CalculateAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin startPin = new Pin(new PinInteger(1), R.string.string_substring_action_start);
    private final transient Pin endPin = new Pin(new PinInteger(1), R.string.string_substring_action_end);
    private final transient Pin resultPin = new Pin(new PinString(), R.string.string_substring_action_result, true);

    public StringSubStringAction() {
        super(ActionType.STRING_SUBSTRING);
        addPins(textPin, startPin, endPin, resultPin);
    }

    public StringSubStringAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, startPin, endPin, resultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinObject textObj = getPinValue(runnable, textPin);
        PinNumber<?> startNum = getPinValue(runnable, startPin);
        PinNumber<?> endNum = getPinValue(runnable, endPin);
        String text = textObj != null ? textObj.toString() : "";
        int length = text.length();
        PinString resultValue = resultPin.getValue(PinString.class);
        if (length == 0) {
            resultValue.setValue("");
            return;
        }
        int startPos = parseIndex(startNum.intValue(), length);
        int endPos = parseIndex(endNum.intValue(), length);
        if (startPos > endPos) {
            int temp = startPos;
            startPos = endPos;
            endPos = temp;
        }
        String result = text.substring(startPos, endPos + 1);
        resultValue.setValue(result);
    }

    private int parseIndex(int userIndex, int length) {
        if (userIndex > 0) {
            // 正数索引：用户1 → 0，超出长度则取最后一个字符位置
            int pos = userIndex - 1;
            return Math.min(pos, length - 1); // 最大只能到最后一个字符（length-1）
        } else if (userIndex < 0) {
            // 负数索引：用户-1 → length-1，超出范围则取第一个字符位置（0）
            int pos = length + userIndex;
            return Math.max(pos, 0); // 最小只能到第一个字符（0）
        } else {
            // 用户输入0：视为无效，默认取第一个字符位置（0）
            return 0;
        }
    }
}
