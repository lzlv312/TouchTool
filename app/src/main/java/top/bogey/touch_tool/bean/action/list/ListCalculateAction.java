package top.bogey.touch_tool.bean.action.list;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.DynamicTypePinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;

public abstract class ListCalculateAction extends CalculateAction implements DynamicTypePinsAction {

    public ListCalculateAction(ActionType type) {
        super(type);
    }

    public ListCalculateAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void onLinkedTo(Task task, Pin origin, Pin to) {
        ListActionLinkEventHandler.onLinkedTo(getDynamicValueTypePins(), task, origin, to);
        super.onLinkedTo(task, origin, to);
    }

    @Override
    public void onUnLinkedFrom(Task task, Pin origin, Pin from) {
        ListActionLinkEventHandler.onUnLinkedFrom(getDynamicValueTypePins(), origin);
        super.onUnLinkedFrom(task, origin, from);
    }

    @NonNull
    @Override
    public List<Pin> getDynamicKeyTypePins() {
        return Collections.emptyList();
    }
}
