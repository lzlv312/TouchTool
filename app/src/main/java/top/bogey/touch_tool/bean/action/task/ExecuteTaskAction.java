package top.bogey.touch_tool.bean.action.task;

import static top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog.GLOBAL_FLAG;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinTaskString;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.pin.special_pin.ShowAblePin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;

public class ExecuteTaskAction extends Action implements DynamicPinsAction, SyncAction {
    private final transient Pin inPin = new ShowAblePin(new PinExecute(), R.string.pin_execute);
    private final transient Pin outPin = new ShowAblePin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin taskPin = new NotLinkAblePin(new PinTaskString(), R.string.execute_task_action_task_id, false, false, true);

    private transient boolean synced = false;

    public ExecuteTaskAction() {
        super(ActionType.EXECUTE_TASK);
        addPins(inPin, outPin, taskPin);
    }

    public ExecuteTaskAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(inPin, outPin, taskPin);
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

        if (!isJustCall(runnable.getTask())) runnable.addDebugLog(this, 1);
        task.execute(runnable, this, params);

        if (!isJustCall(runnable.getTask())) executeNext(runnable, outPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        if (isJustCall(runnable.getTask())) execute(runnable, pin);
    }

    @Override
    public void resetReturnValue(TaskRunnable runnable) {
        if (isJustCall(runnable.getTask())) {
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

    public boolean isJustCall(Task context) {
        Task task = getTask(context);
        if (task == null) return false;
        List<Action> actions = task.getActions(CustomEndAction.class);
        if (actions.isEmpty()) return false;
        for (Action action : actions) {
            CustomEndAction endAction = (CustomEndAction) action;
            return endAction.isJustCall();
        }
        return false;
    }

    public Task getTask(Task context) {
        PinTaskString taskString = taskPin.getValue();
        Task task = Saver.getInstance().getTask(context, taskString.getValue());
        if (task == null) return null;
        return task.copy();
    }

    public void setTask(Task task) {
        PinTaskString taskString = taskPin.getValue();
        taskString.setValue(task.getId());
    }

    public String getTaskId() {
        return taskPin.getValue(PinTaskString.class).getValue();
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (start) pins.add(pin);
            if (pin == taskPin) start = true;
        }
        return pins;
    }

    @Override
    public void sync(Task context) {
        Task task = getTask(context);
        if (task == null) return;
        sync(context, task);
    }

    public void sync(Task context, Task task) {
        String globalFlag = task.getParent() == null ? GLOBAL_FLAG : "";
        setTitle(globalFlag + task.getTitle());

        if (isJustCall(context)) {
            inPin.clearLinks(context);
            outPin.clearLinks(context);
        }

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

        List<Pin> orderPins = new ArrayList<>();
        for (Pin syncPin : syncPins) {
            Pin dynamicPin = getPinByUid(syncPin.getUid());
            if (dynamicPin == null) {
                dynamicPin = syncPin.newCopy();
                // 同步过来的针脚不再是动态的了
                dynamicPin.setDynamic(false);
                // 方向需要反转一下
                dynamicPin.setOut(!dynamicPin.isOut());
                addPin(dynamicPin);
            } else {
                // 同步针脚默认值，只有当类型变更时才同步默认值
                if (!syncPin.isSameClass(dynamicPin)) {
                    dynamicPin.clearLinks(context);
                    dynamicPin.setValue(syncPin.getValue().copy());
                }
                dynamicPin.setTitle(syncPin.getTitle());
            }

            orderPins.add(dynamicPin);
        }

        // 对同步过来的针脚排序
        List<Pin> origin = getPins();
        boolean start = true;
        for (int i = origin.size() - 1; i >= 0; i--) {
            Pin pin = origin.get(i);
            if (pin == taskPin) start = false;
            if (start) origin.remove(i);
        }
        origin.addAll(orderPins);
    }

    @Override
    public String getTitle() {
        if (title == null) return super.getTitle();
        return title;
    }
}
