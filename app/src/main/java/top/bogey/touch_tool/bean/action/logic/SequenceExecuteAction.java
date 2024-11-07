package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.service.TaskRunnable;

public class SequenceExecuteAction extends ExecuteAction implements DynamicPinsAction {
    private final static Pin morePin = new Pin(new PinExecute(), R.string.pin_execute, true);

    private final transient Pin secondPin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin addPin = new Pin(new PinAdd(morePin), R.string.pin_add_execute, true);

    public SequenceExecuteAction() {
        super(ActionType.SEQUENCE_LOGIC);
        addPins(secondPin, addPin);
    }

    public SequenceExecuteAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(secondPin);
        reAddPins(morePin);
        reAddPin(addPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        for (Pin dynamicPin : getDynamicPins()) {
            if (runnable.isInterrupt()) break;
            executeNext(runnable, dynamicPin);
        }
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
