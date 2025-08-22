package top.bogey.touch_tool.bean.save.log;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.normal.LoggerAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.utils.GsonUtil;

public class ActionLog extends Log {
    private final int index;

    private final String actionId;
    private final boolean execute;
    private final Map<String, PinObject> values = new HashMap<>();

    public ActionLog(int index, Action action, boolean execute) {
        super(LogType.ACTION);
        this.index = index;
        this.execute = execute;
        actionId = action.getId();

        for (Pin pin : action.getPins()) {
            PinBase value = pin.getValue();
            if (value instanceof PinObject pinObject) {
                // 图片不再原样进日志，转为字符串
                if (value instanceof PinImage) values.put(pin.getId(), new PinString(value.toString()));
                else values.put(pin.getId(), pinObject);
            }
        }

        StringBuilder builder = new StringBuilder();
        if (index == -1 && action instanceof LoggerAction loggerAction) {
            builder.append(loggerAction.getLogPin().getValue().toString());
        } else {
            builder.append("[").append(index).append("] ");
            builder.append(action.getFullDescription());
        }
        log = builder.toString();
    }

    public ActionLog(JsonObject jsonObject) {
        super(jsonObject);
        index = GsonUtil.getAsInt(jsonObject, "index", -1);

        actionId = GsonUtil.getAsString(jsonObject, "actionId", "");
        execute = GsonUtil.getAsBoolean(jsonObject, "execute", true);
        values.putAll(GsonUtil.getAsObject(jsonObject, "values", TypeToken.getParameterized(HashMap.class, String.class, PinObject.class).getType(), new HashMap<>()));
    }

    public int getIndex() {
        return index;
    }

    public String getActionId() {
        return actionId;
    }

    public boolean isExecute() {
        return execute;
    }

    public Map<String, PinObject> getValues() {
        return values;
    }
}
