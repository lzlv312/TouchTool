package top.bogey.touch_tool.bean.action;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public abstract class CalculateAction extends Action{

    public CalculateAction(ActionType type) {
        super(type);
    }

    public CalculateAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public final void execute(TaskRunnable runnable, Pin pin) {

    }

    @Override
    public void resetReturnValue() {
        for (Pin pin : getPins()) {
            if (pin.isOut()) {
                if (pin.getValue() instanceof PinObject pinObject) {
                    pinObject.reset();
                }
            }
        }
    }
}
