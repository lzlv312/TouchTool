package top.bogey.touch_tool.bean.action.variable;

import static top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog.GLOBAL_FLAG;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;

public class SetVariableAction extends ExecuteAction implements SyncAction {
    private final String varId;
    private final transient Pin varPin;
    private final transient Pin savePin = new NotLinkAblePin(new PinBoolean(false), R.string.set_value_action_save);

    public SetVariableAction(Variable variable) {
        super(ActionType.SET_VARIABLE);
        varId = variable.getId();
        varPin = new Pin(variable.getValue().copy());
        varPin.setUid(varId);
        addPins(varPin, savePin);
    }

    public SetVariableAction(JsonObject jsonObject) {
        super(jsonObject);
        varId = GsonUtil.getAsString(jsonObject, "varId", "");
        reAddPin(new Pin(new PinObject()), true);
        varPin = getPinByUid(varId);
        reAddPin(savePin);
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
        if (var != null && varPin != null) {
            PinObject value = getPinValue(runnable, varPin);
            var.setValue(value);
            if (savePin.getValue(PinBoolean.class).getValue()) var.save();
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
        if (varPin == null) return;
        varPin.setTitle(variable.getTitle());
        String globalFlag = variable.getParent() == null ? GLOBAL_FLAG : "";
        setTitle(MainApplication.getInstance().getString(R.string.set_value_action) + " - " + globalFlag + variable.getTitle());
    }
}
