package top.bogey.touch_tool.bean.action.variable;

import static top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog.GLOBAL_FLAG;
import static top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog.NEED_SAVE_FLAG;

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
            if (var.isNeedSave()) var.save();
        }
        executeNext(runnable, outPin);
    }

    public String getVarId() {
        return varId;
    }

    @Override
    public void sync(Task context) {
        Variable variable = context.findVariable(varId);
        if (variable == null) variable = Saver.getInstance().getVar(varId);
        if (variable == null) return;
        varPin.setValue(variable.getValue());
        varPin.setTitle(variable.getTitle());
        String globalFlag = variable.getParent() == null ? GLOBAL_FLAG : "";
        String saveFlag = variable.isNeedSave() ? NEED_SAVE_FLAG : "";
        setTitle(MainApplication.getInstance().getString(R.string.set_value_action) + " - " + globalFlag + variable.getTitle() + saveFlag);
    }
}
