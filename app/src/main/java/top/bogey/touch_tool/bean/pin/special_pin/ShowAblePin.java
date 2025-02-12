package top.bogey.touch_tool.bean.pin.special_pin;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.task.Task;

public class ShowAblePin extends Pin {

    public ShowAblePin(JsonObject jsonObject) {
        super(jsonObject);
    }

    public ShowAblePin(PinBase value) {
        super(value);
    }

    public ShowAblePin(PinBase value, boolean out) {
        super(value, out);
    }

    public ShowAblePin(PinBase value, int titleId) {
        super(value, titleId);
    }

    public ShowAblePin(PinBase value, int titleId, boolean out) {
        super(value, titleId, out);
    }

    public ShowAblePin(PinBase value, int titleId, boolean out, boolean dynamic) {
        super(value, titleId, out, dynamic);
    }

    public ShowAblePin(PinBase value, int titleId, boolean out, boolean dynamic, boolean hide) {
        super(value, titleId, out, dynamic, hide);
    }

    @Override
    public boolean showAble(Task context) {
        Action action = context.getAction(getOwnerId());
        if (action instanceof ExecuteTaskAction executeTaskAction) {
            return executeTaskAction.isJustCall(context);
        }
        return super.showAble(context);
    }

    @Override
    public boolean linkAble(Task context) {
        if (linkAble()) {
            return showAble(context);
        }
        return false;
    }
}
