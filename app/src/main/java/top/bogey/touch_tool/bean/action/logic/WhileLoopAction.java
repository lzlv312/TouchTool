package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool.bean.pin.pins.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.task.TaskRunnable;

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
                executeNext(runnable, completePin);
                condition = getPinValue(runnable, conditionPin);
            }
        } else {
            isBreak = true;
        }
    }
}
