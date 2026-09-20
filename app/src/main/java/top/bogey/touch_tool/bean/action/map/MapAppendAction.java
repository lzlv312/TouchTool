package top.bogey.touch_tool.bean.action.map;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.service.TaskRunnable;

public class MapAppendAction extends MapExecuteAction {
    private final transient Pin mapPin = new Pin(new PinMap());
    private final transient Pin map2Pin = new Pin(new PinMap());
    private final transient Pin addPin = new Pin(new PinAdd(Arrays.asList(map2Pin)), R.string.pin_add_pin);
    private final transient Pin resultPin = new Pin(new PinMap(), R.string.pin_boolean_result, true);

    public MapAppendAction() {
        super(ActionType.MAP_APPEND);
        addPins(mapPin, map2Pin, addPin, resultPin);
    }

    public MapAppendAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(mapPin);
        reAddPins(map2Pin);
        reAddPin(addPin);
        reAddPin(resultPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinMap result = resultPin.getValue(PinMap.class);
        for (Pin p : getMapPins()) {
            PinMap map = getPinValue(runnable, p);
            if (map != null) {
                result.putAll(map);
            }
        }
        executeNext(runnable, outPin);
    }

    @Override
    public void resetReturnValue(TaskRunnable runnable, Pin pin) {
        if (!pin.isOut() && pin.isSameClass(PinExecute.class)) {
            resultPin.setValue(new PinMap());
        }
    }

    private List<Pin> getMapPins() {
        List<Pin> pins = new ArrayList<>();
        for (Pin pin : getPins()) {
            if (pin == addPin) {
                break;
            }
            if (pin.isSameClass(PinMap.class)) {
                pins.add(pin);
            }
        }
        return pins;
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        List<Pin> pins = new ArrayList<>(getMapPins());
        pins.add(resultPin);
        return pins;
    }

}
