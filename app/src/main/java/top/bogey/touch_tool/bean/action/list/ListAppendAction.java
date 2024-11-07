package top.bogey.touch_tool.bean.action.list;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListAppendAction extends ListExecuteAction {
    private final transient Pin listPin = new Pin(new PinList());
    private final transient Pin elementPin = new Pin(new PinList());
    private final transient Pin resultPin = new Pin(new PinBoolean(), R.string.pin_boolean_result, true);

    public ListAppendAction() {
        super(ActionType.LIST_APPEND);
        addPins(listPin, elementPin, resultPin);
    }

    public ListAppendAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(listPin, elementPin, resultPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinList list = getPinValue(runnable, listPin);
        PinList element = getPinValue(runnable, elementPin);
        boolean result = list.addAll(element);
        resultPin.getValue(PinBoolean.class).setValue(result);
        executeNext(runnable, outPin);
    }

    @Override
    public List<Pin> getDynamicValueTypePins() {
        return Arrays.asList(listPin, elementPin);
    }

}
