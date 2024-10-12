package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinNumber;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public abstract class FindExecuteAction extends ExecuteAction {
    protected final transient Pin elsePin = new Pin(new PinExecute(), R.string.if_action_else, true);
    private final transient Pin timeoutPin = new Pin(new PinInteger(3000), R.string.wait_if_action_timeout, false, false, true);
    private final transient Pin intervalPin = new Pin(new PinInteger(100), R.string.find_execute_action_interval, false, false, true);

    public FindExecuteAction(ActionType type) {
        super(type);
        addPins(elsePin, timeoutPin, intervalPin);
    }

    public FindExecuteAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(elsePin, timeoutPin, intervalPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        boolean found = find(runnable);

        PinNumber<?> timeout = getPinValue(runnable, timeoutPin);
        PinNumber<?> interval = getPinValue(runnable, intervalPin);
        long startTime = System.currentTimeMillis();
        while (!found) {
            runnable.sleep(interval.intValue());
            if (runnable.isInterrupt()) return;
            if (timeout.intValue() < System.currentTimeMillis() - startTime) break;
            found = find(runnable);
        }

        if (found) {
            executeNext(runnable, outPin);
        } else {
            executeNext(runnable, elsePin);
        }
    }

    public abstract boolean find(TaskRunnable runnable);
}
