package top.bogey.touch_tool.bean.action.variable;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;

public class GetVariableAction extends CalculateAction {
    private final String varId;
    private final transient Pin varPin;

    public GetVariableAction(Variable variable) {
        super(ActionType.GET_VARIABLE);
        varId = variable.getId();
        varPin = new Pin(variable.getValue(), true);
        varPin.setTitle(variable.getTitle());
        addPin(varPin);
    }

    public GetVariableAction(JsonObject jsonObject) {
        super(jsonObject);
        varId = GsonUtil.getAsString(jsonObject, "varId", "");
        reAddPins(null, true);
        varPin = getPins().get(0);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        Task task = runnable.getTask();
        Variable var = task.findVar(varId);
        if (var == null) var = Saver.getInstance().getVar(varId);
        if (var == null) return;
        varPin.setValue(var.getValue());
    }
}
