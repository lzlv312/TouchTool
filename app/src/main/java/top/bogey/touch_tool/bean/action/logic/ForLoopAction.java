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

public class ForLoopAction extends ExecuteAction {
    private final transient Pin breakPin = new Pin(new PinExecute(), R.string.for_loop_action_break);
    private final transient Pin startPin = new Pin(new PinInteger(), R.string.for_loop_action_start);
    private final transient Pin endPin = new Pin(new PinInteger(), R.string.for_loop_action_end);
    private final transient Pin stepPin = new Pin(new PinInteger(), R.string.for_loop_action_step, false, false, true);
    private final transient Pin currentPin = new Pin(new PinInteger(), R.string.for_loop_action_current, true);
    private final transient Pin completePin = new Pin(new PinExecute(), R.string.for_loop_action_complete, true);

    private transient boolean isBreak = false;

    public ForLoopAction() {
        super(ActionType.FOR_LOGIC);
        addPins(breakPin, startPin, endPin, stepPin, currentPin, completePin);
    }

    public ForLoopAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(breakPin, startPin, endPin, stepPin, currentPin, completePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        if (pin == inPin) {
            PinNumber<?> start = getPinValue(runnable, startPin);
            PinNumber<?> end = getPinValue(runnable, endPin);
            PinNumber<?> step = getPinValue(runnable, stepPin);

            for (int i = start.intValue(); i <= end.intValue(); i += step.intValue()) {
                if (isBreak || runnable.isInterrupt()) break;
                currentPin.getValue(PinInteger.class).setValue(i);
                executeNext(runnable, outPin);
            }
            executeNext(runnable, completePin);
        } else {
            isBreak = true;
        }
    }
}
