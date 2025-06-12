package top.bogey.touch_tool.bean.save;

import android.content.Context;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.normal.LoggerAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.GsonUtil;
import top.bogey.touch_tool.utils.tree.ITreeNodeData;

public class LogInfo implements ITreeNodeData {
    private final long time;
    private final int index;
    private final String log;

    private final String actionId;
    private final boolean execute;
    private final Map<String, PinObject> values = new HashMap<>();

    private final List<LogInfo> children = new ArrayList<>();

    public LogInfo(int index, Action action, boolean execute) {
        this.time = System.currentTimeMillis();
        this.index = index;
        this.actionId = action.getId();
        this.execute = execute;

        for (Pin pin : action.getPins()) {
            PinBase value = pin.getValue();
            if (value instanceof PinObject pinObject) {
                values.put(pin.getId(), (PinObject) pinObject.copy());
            }
        }
        StringBuilder builder = new StringBuilder();
        if (index == -1 && action instanceof LoggerAction loggerAction) {
            builder.append(loggerAction.getLogPin().getValue().toString());
        } else {
            builder.append("[").append(index).append("] ");
            builder.append(action.getTitle());
            if (!children.isEmpty()) builder.append("(").append(children.size()).append(")");
        }
        log = builder.toString();
    }

    public LogInfo(String log) {
        this.time = System.currentTimeMillis();
        index = -1;
        this.log = log;
        actionId = "";
        execute = false;
    }

    public LogInfo(JsonObject jsonObject) {
        time = GsonUtil.getAsLong(jsonObject, "time", System.currentTimeMillis());
        index = GsonUtil.getAsInt(jsonObject, "index", -1);
        log = GsonUtil.getAsString(jsonObject, "log", "");
        actionId = GsonUtil.getAsString(jsonObject, "actionId", "");
        execute = GsonUtil.getAsBoolean(jsonObject, "execute", true);
        values.putAll(GsonUtil.getAsObject(jsonObject, "values", TypeToken.getParameterized(HashMap.class, String.class, PinBase.class).getType(), new HashMap<>()));
        children.addAll(GsonUtil.getAsObject(jsonObject, "children", TypeToken.getParameterized(ArrayList.class, LogInfo.class).getType(), new ArrayList<>()));
    }

    public long getTime() {
        return time;
    }

    public String getTime(Context context) {
        return AppUtil.formatDateTime(context, time, true, false);
    }

    public int getIndex() {
        return index;
    }

    public String getLog() {
        return log;
    }

    public Action getAction(Task task) {
        return task.getAction(actionId);
    }

    public boolean isExecute() {
        return execute;
    }

    public Map<String, PinObject> getValues() {
        return values;
    }

    @Override
    public List<LogInfo> getChildren() {
        return children;
    }

    public void addChild(LogInfo logInfo) {
        children.add(logInfo);
    }

    public static class LogDeserialize implements JsonDeserializer<LogInfo> {
        @Override
        public LogInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new LogInfo(json.getAsJsonObject());
        }
    }
}
