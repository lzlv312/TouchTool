package top.bogey.touch_tool.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.service.TaskRunnable;

public class InputParamTemplateAction extends CalculateAction {

    public InputParamTemplateAction() {
        super(ActionType.INPUT_PARAM);
    }

    public InputParamTemplateAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {

    }
}
