package top.bogey.touch_tool.bean.action.map;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.DynamicTypePinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;

public abstract class MapCalculateAction extends CalculateAction implements DynamicTypePinsAction {

    public MapCalculateAction(ActionType type) {
        super(type);
    }

    public MapCalculateAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void onLinkedTo(Task task, Pin origin, Pin to) {
        MapActionLinkEventHandler.onLinkedTo(getDynamicKeyTypePins(), getDynamicValueTypePins(), task, origin, to);
        super.onLinkedTo(task, origin, to);
    }

    @Override
    public void onUnLinkedFrom(Task task, Pin origin, Pin from) {
        MapActionLinkEventHandler.onUnLinkedFrom(getDynamicKeyTypePins(), getDynamicValueTypePins(), origin);
        super.onUnLinkedFrom(task, origin, from);
    }
}
