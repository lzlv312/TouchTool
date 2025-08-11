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
import top.bogey.touch_tool.service.TaskRunnable;

public class MapGetAction extends MapCalculateAction {
    private final transient Pin mapPin = new Pin(new PinMap());
    private final transient Pin keyPin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.map_action_key);
    private final transient Pin resultPin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.map_action_value, true);

    public MapGetAction() {
        super(ActionType.MAP_GET);
        addPins(mapPin, keyPin, resultPin);
    }

    public MapGetAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(mapPin);
        reAddPin(keyPin, true);
        reAddPin(resultPin, true);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinMap map = getPinValue(runnable, mapPin);
        PinObject key = getPinValue(runnable, keyPin);
        resultPin.setValue(map.get(key));
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        return Arrays.asList(mapPin, keyPin, resultPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicKeyTypePins() {
        return Collections.singletonList(keyPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicValueTypePins() {
        return Collections.singletonList(resultPin);
    }
}
