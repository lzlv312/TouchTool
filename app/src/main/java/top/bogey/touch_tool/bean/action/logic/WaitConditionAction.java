package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class WaitConditionAction extends ExecuteAction {
    private final transient Pin conditionPin = new Pin(new PinBoolean(), R.string.pin_boolean_condition);
    private final transient Pin timeoutPin = new Pin(new PinInteger(), R.string.wait_if_action_timeout);
    private final transient Pin elsePin = new Pin(new PinExecute(), R.string.if_action_else, true);

    public WaitConditionAction() {
        super(ActionType.WAIT_IF_LOGIC);
        addPins(conditionPin, timeoutPin, elsePin);
    }

    public WaitConditionAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(conditionPin, timeoutPin, elsePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinBoolean condition = getPinValue(runnable, conditionPin);
        PinNumber<?> timeout = getPinValue(runnable, timeoutPin);

        long startTime = System.currentTimeMillis();
        while (!condition.getValue()) {
            runnable.sleep(100);
            if (runnable.isInterrupt()) return;
            if (timeout.intValue() < System.currentTimeMillis() - startTime) break;
            condition = getPinValue(runnable, conditionPin);
        }

        if (condition.getValue()) {
            executeNext(runnable, outPin);
        } else {
            executeNext(runnable, elsePin);
        }
    }
}
