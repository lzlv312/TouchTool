package top.bogey.touch_tool.bean.action.list;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_list.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListRemoveAction extends ListExecuteAction {
    private final transient Pin listPin = new Pin(new PinList());
    private final transient Pin indexPin = new Pin(new PinInteger(), R.string.list_action_index);
    private final transient Pin resultPin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.pin_object, true);

    public ListRemoveAction() {
        super(ActionType.LIST_REMOVE);
        addPins(listPin, indexPin, resultPin);
    }

    public ListRemoveAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(listPin);
        reAddPin(indexPin);
        reAddPin(resultPin, true);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinList list = getPinValue(runnable, listPin);
        PinNumber<?> index = getPinValue(runnable, indexPin);
        int indexValue = index.intValue();
        if (indexValue >= 1 && indexValue <= list.size()) {
            resultPin.setValue(list.remove(indexValue - 1));
        }
        executeNext(runnable, outPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        return Arrays.asList(listPin, resultPin);
    }

}
