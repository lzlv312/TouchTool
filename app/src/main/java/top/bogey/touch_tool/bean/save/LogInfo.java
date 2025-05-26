package top.bogey.touch_tool.bean.save;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.utils.AppUtil;

public class LogInfo {
    private final long time;
    private final int index;

    private final String actionId;
    private final Map<String, PinObject> values = new HashMap<>();
    private final List<LogInfo> children = new ArrayList<>();

    public LogInfo(int index, Action action) {
        this.time = System.currentTimeMillis();
        this.index = index;
        this.actionId = action.getId();

        for (Pin pin : action.getPins()) {
            PinBase value = pin.getValue();
            if (value instanceof PinObject pinObject) {
                values.put(pin.getId(), (PinObject) pinObject.copy());
            }
        }
    }

    public long getTime() {
        return time;
    }

    public String getTime(Context context) {
        return AppUtil.formatDateTime(context, time, "\n", true, false);
    }

    public int getIndex() {
        return index;
    }

    public Action getAction(Task task) {
        return task.getAction(actionId);
    }

    public Map<String, PinObject> getValues() {
        return values;
    }

    public List<LogInfo> getChildren() {
        return children;
    }

    public void addChild(LogInfo logInfo) {
        children.add(logInfo);
    }

}
