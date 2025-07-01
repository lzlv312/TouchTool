package top.bogey.touch_tool.bean.action.string;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinFileContentString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;

public class ParseJsonAction extends CalculateAction implements SyncAction {
    private final transient Pin jsonPin = new Pin(new PinFileContentString(), R.string.pin_string);
    private final transient Pin resultPin = new Pin(new PinMap(), R.string.pin_boolean_result, true);

    public ParseJsonAction() {
        super(ActionType.PARSE_JSON);
        addPins(jsonPin, resultPin);
    }

    public ParseJsonAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(jsonPin);
        reAddPin(resultPin, true);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        if (!resultPin.getValue().isDynamic()) return;
        PinObject json = getPinValue(runnable, jsonPin);
        String jsonString = json.toString();
        Gson gson = new Gson();
        try {
            if (jsonString.startsWith("{")) {
                Map<String, Object> map = gson.fromJson(jsonString, new TypeToken<Map<String, Object>>() {
                }.getType());
                PinBase pinBase = PinBase.parseValue(map);
                resultPin.setValue(pinBase);
            } else if (jsonString.startsWith("[")) {
                List<Object> list = gson.fromJson(jsonString, new TypeToken<List<Object>>() {
                }.getType());
                PinBase pinBase = PinBase.parseValue(list);
                resultPin.setValue(pinBase);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void sync(Task context) {
        if (jsonPin.isLinked()) return;
        resultPin.setValue(new PinMap());
        PinFileContentString pinValue = jsonPin.getValue(PinFileContentString.class);
        String json = pinValue.getValue();
        Gson gson = new Gson();
        try {
            if (json.startsWith("{")) {
                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
                PinBase pinBase = PinBase.parseValue(map);
                resultPin.setValue(pinBase);
            } else if (json.startsWith("[")) {
                List<Object> list = gson.fromJson(json, new TypeToken<List<Object>>() {
                }.getType());
                PinBase pinBase = PinBase.parseValue(list);
                resultPin.setValue(pinBase);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onValueUpdated(Pin origin, PinBase value) {
        super.onValueUpdated(origin, value);
        sync(null);
    }
}
