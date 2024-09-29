package top.bogey.touch_tool.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class DelayAction extends ExecuteAction {
    private final transient Pin delay = new Pin(new PinValueArea(10, 60000), R.string.delay_action_time);

    public DelayAction() {
        super(ActionType.DELAY);
        addPins(delay);
    }

    public DelayAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(delay);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinValueArea delayValue = getPinValue(runnable, delay);
        runnable.pause(delayValue.getRandomValue());
        executeNext(runnable, outPin);
    }
}
