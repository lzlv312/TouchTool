package top.bogey.touch_tool.bean.action.map;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class MapMakeAction extends MapCalculateAction implements DynamicPinsAction {
    private final static Pin keyMorePin = new Pin(new PinObject(), R.string.map_make_action_key);
    private final static Pin valueMorePin = new Pin(new PinObject(), R.string.map_make_action_value);
    private final transient Pin addPin = new Pin(new PinAdd(keyMorePin), R.string.pin_add_pin);
    private final transient Pin mapPin = new Pin(new PinMap(), R.string.pin_map, true);

    public MapMakeAction() {
        super(ActionType.MAP_MAKE);
        addPins(addPin, mapPin);
    }

    public MapMakeAction(JsonObject jsonObject) {
        super(jsonObject);
        while (!tmpPins.get(0).isSameClass(PinAdd.class)) {
            reAddPin(keyMorePin);
            reAddPin(valueMorePin);
        }
        reAddPins(addPin, mapPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinMap map = mapPin.getValue();
        List<Pin> dynamicPins = getDynamicPins();
        for (int i = 0; i < dynamicPins.size(); i += 2) {
            Pin keyPin = dynamicPins.get(i);
            Pin valuePin = dynamicPins.get(i + 1);
            PinObject keyObject = getPinValue(runnable, keyPin);
            PinObject valueObject = getPinValue(runnable, valuePin);
            map.put(keyObject, valueObject);
        }
    }

    @Override
    public List<Pin> getDynamicKeyTypePins() {
        List<Pin> pins = new ArrayList<>();
        List<Pin> dynamicPins = getDynamicPins();
        for (int i = 0; i < dynamicPins.size(); i++) {
            Pin dynamicPin = dynamicPins.get(i);
            if (i % 2 == 0) pins.add(dynamicPin);
        }
        pins.add(mapPin);
        return pins;
    }

    @Override
    public List<Pin> getDynamicValueTypePins() {
        List<Pin> pins = new ArrayList<>();
        List<Pin> dynamicPins = getDynamicPins();
        for (int i = 0; i < dynamicPins.size(); i++) {
            Pin dynamicPin = dynamicPins.get(i);
            if (i % 2 == 1) pins.add(dynamicPin);
        }
        pins.add(mapPin);
        return pins;
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = true;
        for (Pin pin : getPins()) {
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
