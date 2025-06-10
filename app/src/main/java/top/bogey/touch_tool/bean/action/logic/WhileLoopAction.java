package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.service.TaskRunnable;

public class WhileLoopAction extends ExecuteAction {
    private final transient Pin breakPin = new Pin(new PinExecute(), R.string.while_loop_action_break);
    private final transient Pin conditionPin = new Pin(new PinBoolean(), R.string.pin_boolean_condition);
    private final transient Pin completePin = new Pin(new PinExecute(), R.string.while_loop_action_complete, true);
    private transient boolean isBreak = false;

    public WhileLoopAction() {
        super(ActionType.WHILE_LOGIC);
        addPins(breakPin, conditionPin, completePin);
    }

    public WhileLoopAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(breakPin, conditionPin, completePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        if (pin == inPin) {
            PinBoolean condition = getPinValue(runnable, conditionPin);
            while (condition.getValue()) {
                if (isBreak || runnable.isInterrupt()) break;
                executeNext(runnable, outPin);
                condition = getPinValue(runnable, conditionPin);
            }
            executeNext(runnable, completePin);
        } else {
            isBreak = true;
        }
    }

    @Override
    public void onExecuteNext(TaskRunnable runnable, Pin pin) {
        if (pin == completePin) {
            super.onExecuteNext(runnable, pin);
        }
    }
}
