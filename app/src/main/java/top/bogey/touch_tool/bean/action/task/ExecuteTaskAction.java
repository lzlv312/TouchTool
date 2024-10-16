package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinTaskString;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.pin.special_pin.ShowAblePin;
import top.bogey.touch_tool.bean.save.TaskSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class ExecuteTaskAction extends Action {
    private final transient Pin inPin = new ShowAblePin(new PinExecute(), R.string.pin_execute);
    private final transient Pin outPin = new ShowAblePin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin taskPin = new NotLinkAblePin(new PinTaskString(), R.string.execute_task_action_task_id);
    private final transient Pin withExecutePin = new NotLinkAblePin(new PinBoolean(true), R.string.execute_task_action_with_execute);

    public ExecuteTaskAction() {
        super(ActionType.EXECUTE_TASK);
        addPins(inPin, outPin, taskPin, withExecutePin);
    }

    public ExecuteTaskAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(inPin, outPin, taskPin, withExecutePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        Map<String, PinObject> params = new HashMap<>();
        for (Pin p : getPins()) {
            if (!p.isOut() && p.getValue() instanceof PinObject) {
                PinObject value = getPinValue(runnable, p);
                params.put(p.getUid(), value);
            }
        }

        PinTaskString taskString = taskPin.getValue();
        Task runnableTask = runnable.getTask();
        Task task = runnableTask.findTask(taskString.getValue());
        if (task == null) {
            task = TaskSaver.getInstance().getTask(taskString.getValue());
        }
        if (task == null) return;

        task.copy().execute(runnable, this, params);

        PinBoolean withExecute = withExecutePin.getValue();
        if (withExecute.getValue()) {
            executeNext(runnable, outPin);
        }
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinBoolean withExecute = withExecutePin.getValue();
        if (!withExecute.getValue()) {
            execute(runnable, pin);
        }
    }

    @Override
    public void resetReturnValue() {

    }

    public void setParams(Map<String, PinObject> params) {
        params.forEach((key, value) -> {
            Pin pin = getPinByUid(key);
            if (pin == null) return;
            pin.setValue(value);
        });
    }

    public Pin getWithExecutePin() {
        return withExecutePin;
    }
}
