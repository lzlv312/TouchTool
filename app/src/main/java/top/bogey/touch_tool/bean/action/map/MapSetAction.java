package top.bogey.touch_tool.bean.action.map;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.service.TaskRunnable;

public class MapSetAction extends MapExecuteAction {
    private final transient Pin mapPin = new Pin(new PinMap(), R.string.pin_map);
    private final transient Pin keyPin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.map_action_key);
    private final transient Pin valuePin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.map_action_value);
    private final transient Pin resultPin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.map_set_action_pre_value, true);

    public MapSetAction() {
        super(ActionType.MAP_SET);
        addPins(mapPin, keyPin, valuePin, resultPin);
    }

    public MapSetAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(mapPin);
        reAddPin(keyPin, true);
        reAddPin(valuePin, true);
        reAddPin(resultPin, true);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinMap map = getPinValue(runnable, mapPin);
        PinObject key = getPinValue(runnable, keyPin);
        PinObject value = getPinValue(runnable, valuePin);
        resultPin.setValue(map.put(key, value));
        executeNext(runnable, outPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        return Arrays.asList(mapPin, keyPin, valuePin, resultPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicKeyTypePins() {
        return Collections.singletonList(keyPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicValueTypePins() {
        return Arrays.asList(valuePin, resultPin);
    }
}
