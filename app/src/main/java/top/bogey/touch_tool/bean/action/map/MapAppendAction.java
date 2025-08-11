package top.bogey.touch_tool.bean.action.map;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.service.TaskRunnable;

public class MapAppendAction extends MapExecuteAction {
    private final transient Pin mapPin = new Pin(new PinMap());
    private final transient Pin map2Pin = new Pin(new PinMap());
    private final transient Pin resultPin = new Pin(new PinMap(), R.string.pin_boolean_result, true);

    public MapAppendAction() {
        super(ActionType.MAP_APPEND);
        addPins(mapPin, map2Pin, resultPin);
    }

    public MapAppendAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(mapPin, map2Pin, resultPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinMap map = getPinValue(runnable, mapPin);
        PinMap map2 = getPinValue(runnable, map2Pin);
        PinMap pinMap = resultPin.getValue(PinMap.class);
        pinMap.putAll(map);
        pinMap.putAll(map2);
        executeNext(runnable, outPin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        return Arrays.asList(mapPin, map2Pin, resultPin);
    }

}
