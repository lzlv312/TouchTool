package top.bogey.touch_tool.bean.action.variable;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;

public class SetVariableAction extends ExecuteAction implements SyncAction {
    private final String varId;
    private final transient Pin varPin;

    public SetVariableAction(Variable variable) {
        super(ActionType.SET_VARIABLE);
        varId = variable.getId();
        varPin = new Pin(variable.getValue());
        varPin.setId(varId);
        varPin.setTitle(variable.getTitle());
        setTitle(MainApplication.getInstance().getString(R.string.set_value_action) + "-" + variable.getTitle());
        addPin(varPin);
    }

    public SetVariableAction(JsonObject jsonObject) {
        super(jsonObject);
        varId = GsonUtil.getAsString(jsonObject, "varId", "");
        reAddPins(null, true);
        varPin = getPinById(varId);
    }

    @Override
    public String getTitle() {
        if (title == null) return super.getTitle();
        return title;
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        Task task = runnable.getTask();
        Variable var = task.findVariable(varId);
        if (var == null) var = Saver.getInstance().getVar(varId);
        if (var != null) {
            PinObject value = getPinValue(runnable, varPin);
            var.setValue(value);
            // 全局变量才需要保存
            if (var.getParent() == null) var.save();
        }
        executeNext(runnable, outPin);
    }

    @Override
    public void sync(Task context) {
        Variable variable = context.findVariable(varId);
        if (variable == null) variable = Saver.getInstance().getVar(varId);
        if (variable == null) return;
        varPin.setValue(variable.getValue());
        varPin.setTitle(variable.getTitle());
        setTitle(MainApplication.getInstance().getString(R.string.set_value_action) + "-" + variable.getTitle());
    }
}
