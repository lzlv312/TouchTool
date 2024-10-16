package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinIconExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.ui.custom.ChoiceExecuteFloatView;

public class ChoiceExecuteAction extends Action implements DynamicPinsAction {
    private final transient Pin inPin = new Pin(new PinExecute(), R.string.pin_execute);
    private final transient Pin outPin = new Pin(new PinIconExecute(), R.string.pin_execute, true);
    private final static Pin morePin = new Pin(new PinIconExecute(), R.string.pin_execute, true);

    private final transient Pin posPin = new Pin(new PinPoint(), R.string.Pin_point, false, false, true);

    private final transient Pin secondPin = new Pin(new PinIconExecute(), R.string.pin_execute, true);
    private final transient Pin addPin = new Pin(new PinAdd(morePin), R.string.pin_add_execute, true);
    private final transient Pin defaultPin = new Pin(new PinExecute(), R.string.choice_action_default, true);

    public ChoiceExecuteAction() {
        super(ActionType.CHOICE_LOGIC);
        addPins(inPin, outPin, posPin, secondPin, addPin, defaultPin);
    }

    public ChoiceExecuteAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(inPin, outPin, posPin, secondPin);
        reAddPins(morePin);
        reAddPins(addPin, defaultPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        List<ChoiceExecuteFloatView.Choice> choices = new ArrayList<>();
        for (Pin dynamicPin : getDynamicPins()) {
            Action nextAction = getNextAction(runnable, dynamicPin);
            if (nextAction == null) continue;
            choices.add(new ChoiceExecuteFloatView.Choice(dynamicPin.getId(), nextAction.getValidDescription(), dynamicPin.getValue(PinIconExecute.class).getImage()));
        }

        AtomicReference<String> nextPinId = new AtomicReference<>();
        PinPoint point = getPinValue(runnable, posPin);
        ChoiceExecuteFloatView.showChoice(choices, result -> {
            nextPinId.set(result);
            runnable.resume();
        }, point.getValue());
        runnable.pause();

        String pinId = nextPinId.get();
        if (pinId != null) {
            Pin nextPin = getPinById(pinId);
            if (nextPin != null) {
                executeNext(runnable, nextPin);
                return;
            }
        }
        executeNext(runnable, defaultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {

    }

    @Override
    public void resetReturnValue() {

    }

    private Action getNextAction(TaskRunnable runnable, Pin pin) {
        Task task = runnable.getTask();
        Pin linkedPin = pin.getLinkedPin(runnable.getTask());
        if (linkedPin == null) return null;
        return task.getAction(linkedPin.getOwnerId());
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        pins.add(outPin);
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == secondPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
