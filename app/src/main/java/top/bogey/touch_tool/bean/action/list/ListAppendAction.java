package top.bogey.touch_tool.bean.action.list;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_list.PinList;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListAppendAction extends ListExecuteAction {
    private final transient Pin listPin = new Pin(new PinList());
    private final transient Pin list2Pin = new Pin(new PinList());
    private final transient Pin addPin = new Pin(new PinAdd(Arrays.asList(list2Pin)), R.string.pin_add_pin);
    private final transient Pin resultPin = new Pin(new PinList(), R.string.pin_boolean_result, true);

    public ListAppendAction() {
        super(ActionType.LIST_APPEND);
        addPins(listPin, list2Pin, addPin, resultPin);
    }

    public ListAppendAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(listPin);
        reAddPins(list2Pin);
        reAddPin(addPin);
        reAddPin(resultPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinList result = resultPin.getValue(PinList.class);
        for (Pin p : getListPins()) {
            PinList list = getPinValue(runnable, p);
            if (list != null) {
                result.addAll(list);
            }
        }
        executeNext(runnable, outPin);
    }

    @Override
    public void resetReturnValue(TaskRunnable runnable, Pin pin) {
        if (!pin.isOut() && pin.isSameClass(PinExecute.class)) {
            resultPin.setValue(new PinList());
        }
    }

    private List<Pin> getListPins() {
        List<Pin> pins = new ArrayList<>();
        for (Pin pin : getPins()) {
            if (pin == addPin) {
                break;
            }
            if (pin.isSameClass(PinList.class)) {
                pins.add(pin);
            }
        }
        return pins;
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        List<Pin> pins = new ArrayList<>(getListPins());
        pins.add(resultPin);
        return pins;
    }

}
