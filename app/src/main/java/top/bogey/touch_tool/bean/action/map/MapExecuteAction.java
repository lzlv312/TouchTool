package top.bogey.touch_tool.bean.action.map;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicTypePinsAction;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;

public abstract class MapExecuteAction extends ExecuteAction implements DynamicTypePinsAction {

    public MapExecuteAction(ActionType type) {
        super(type);
    }

    public MapExecuteAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void onLinkedTo(Task task, Pin origin, Pin to) {
        handleLinkTo(task, origin, to);
        super.onLinkedTo(task, origin, to);
    }

    protected void handleLinkTo(Task task, Pin origin, Pin to) {
        MapActionLinkEventHandler.onLinkedTo(getDynamicTypePins(), getDynamicKeyTypePins(), getDynamicValueTypePins(), task, origin, to);
    }

    @Override
    public void onUnLinkedFrom(Task task, Pin origin, Pin from) {
        handleUnLinkFrom(origin);
        super.onUnLinkedFrom(task, origin, from);
    }

    protected void handleUnLinkFrom(Pin origin) {
        MapActionLinkEventHandler.onUnLinkedFrom(getDynamicTypePins(), getDynamicKeyTypePins(), getDynamicValueTypePins(), origin);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicKeyTypePins() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<Pin> getDynamicValueTypePins() {
        return Collections.emptyList();
    }
}
