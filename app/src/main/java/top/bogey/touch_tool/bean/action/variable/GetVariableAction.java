package top.bogey.touch_tool.bean.action.variable;

import static top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog.GLOBAL_FLAG;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;

public class GetVariableAction extends CalculateAction implements SyncAction {
    private final String varId;
    private final transient Pin varPin;

    public GetVariableAction(Variable variable) {
        super(ActionType.GET_VARIABLE);
        varId = variable.getId();
        varPin = new Pin(variable.getValue(), true);
        varPin.setUid(varId);
        addPin(varPin);
    }

    public GetVariableAction(JsonObject jsonObject) {
        super(jsonObject);
        varId = GsonUtil.getAsString(jsonObject, "varId", "");
        reAddPins(null, true);
        varPin = getPinByUid(varId);
    }

    @Override
    public String getTitle() {
        if (title == null) return super.getTitle();
        return title;
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        Task task = runnable.getTask();
        Variable var = task.findVariable(varId);
        if (var == null) var = Saver.getInstance().getVar(varId);
        if (var == null || varPin == null) return;
        varPin.setValue(var.getValue());
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
        setTitle(MainApplication.getInstance().getString(R.string.get_value_action) + " - " + globalFlag + variable.getTitle());
    }
}
