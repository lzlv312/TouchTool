package top.bogey.touch_tool.bean.action.list;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_list.PinList;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListAppendAction extends ListExecuteAction {
    private final transient Pin listPin = new Pin(new PinList());
    private final transient Pin list2Pin = new Pin(new PinList());
    private final transient Pin resultPin = new Pin(new PinList(), R.string.pin_boolean_result, true);

    public ListAppendAction() {
        super(ActionType.LIST_APPEND);
        addPins(listPin, list2Pin, resultPin);
    }

    public ListAppendAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(listPin, list2Pin, resultPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinList list = getPinValue(runnable, listPin);
        PinList list2 = getPinValue(runnable, list2Pin);
        PinList pinList = resultPin.getValue(PinList.class);
        pinList.addAll(list);
        pinList.addAll(list2);
        executeNext(runnable, outPin);
    }

    @Override
    public void resetReturnValue(TaskRunnable runnable, Pin pin) {
        if (!pin.isOut() && pin.isSameClass(PinExecute.class)) {
            resultPin.setValue(new PinList());
        }
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        return Arrays.asList(listPin, list2Pin, resultPin);
    }

}
