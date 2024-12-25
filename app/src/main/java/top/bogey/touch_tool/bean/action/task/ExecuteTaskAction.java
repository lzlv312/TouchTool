package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinTaskString;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.pin.special_pin.ShowAblePin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;

public class ExecuteTaskAction extends Action implements DynamicPinsAction {
    private final transient Pin inPin = new ShowAblePin(new PinExecute(), R.string.pin_execute);
    private final transient Pin outPin = new ShowAblePin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin taskPin = new NotLinkAblePin(new PinTaskString(), R.string.execute_task_action_task_id);
    private final transient Pin justCallPin = new NotLinkAblePin(new PinBoolean(true), R.string.execute_task_action_just_cal, false, false, true);

    private transient boolean synced = false;

    public ExecuteTaskAction() {
        super(ActionType.EXECUTE_TASK);
        addPins(inPin, outPin, taskPin, justCallPin);
    }

    public ExecuteTaskAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(inPin, outPin, taskPin, justCallPin);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        Task task = getTask(runnable.getTask());
        if (task == null) return;

        if (!synced) {
            synced = true;
            sync(runnable.getTask(), task);
        }

        Map<String, PinObject> params = new HashMap<>();
        for (Pin p : getDynamicPins()) {
            if (!p.isOut()) {
                PinObject value = getPinValue(runnable, p);
                params.put(p.getUid(), value);
            }
        }

        task.execute(runnable, this, params);

        if (!isJustCall()) executeNext(runnable, outPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        if (isJustCall()) execute(runnable, pin);
    }

    @Override
    public void resetReturnValue() {
        if (isJustCall()) {
            for (Pin pin : getDynamicPins()) {
                if (pin.isOut()) {
                    pin.getValue().reset();
                }
            }
        }
    }

    public void setParams(Map<String, PinObject> params) {
        params.forEach((key, value) -> {
            Pin pin = getPinByUid(key);
            if (pin == null) return;
            pin.setValue(value);
        });
    }

    public Pin getJustCallPin() {
        return justCallPin;
    }

    public boolean isJustCall() {
        PinBoolean justCal = justCallPin.getValue();
        return justCal.getValue();
    }

    public Task getTask(Task context) {
        PinTaskString taskString = taskPin.getValue();
        return Saver.getInstance().getTask(context, taskString.getValue());
    }

    public void setTask(Task task) {
        PinTaskString taskString = taskPin.getValue();
        taskString.setValue(task.getId());
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == justCallPin) start = true;
            if (start) pins.add(pin);
        }
        return pins;
    }

    public void sync(Task context) {
        Task task = getTask(context);
        if (task == null) return;
        sync(context, task);
    }

    public void sync(Task context, Task task) {
        List<Pin> syncPins = new ArrayList<>();
        for (Action action : task.getActions(CustomStartAction.class)) {
            CustomStartAction startAction = (CustomStartAction) action;
            syncPins.addAll(startAction.getDynamicPins());
            break;
        }
        for (Action action : task.getActions(CustomEndAction.class)) {
            CustomEndAction endAction = (CustomEndAction) action;
            syncPins.addAll(endAction.getDynamicPins());
            break;
        }

        List<Pin> dynamicPins = getDynamicPins();
        for (Pin dynamicPin : dynamicPins) {
            boolean flag = true;
            for (Pin syncPin : syncPins) {
                if (dynamicPin.getUid().equals(syncPin.getUid())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                removePin(context, dynamicPin);
            }
        }

        for (Pin syncPin : syncPins) {
            Pin dynamicPin = getPinByUid(syncPin.getUid());
            if (dynamicPin == null) {
                addPin(syncPin.newCopy());
                continue;
            }

            if (!syncPin.isSameClass(dynamicPin)) {
                dynamicPin.clearLinks(context);
                dynamicPin.setValue(syncPin.getValue().copy());
            }
            dynamicPin.setTitle(syncPin.getTitle());
        }
    }
}
