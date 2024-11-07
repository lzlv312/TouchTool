package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.AppUtil;

public class GetScreenStatusAction extends CalculateAction {
    private final transient Pin screenPin = new Pin(new PinSingleSelect(R.array.screen_state), R.string.get_screen_status_action_state, true);

    public GetScreenStatusAction() {
        super(ActionType.GET_SCREEN_STATUS);
        addPin(screenPin);
    }

    public GetScreenStatusAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(screenPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        TaskInfoSummary.PhoneState state = AppUtil.getPhoneState(MainApplication.getInstance());
        screenPin.getValue(PinSingleSelect.class).setIndex(state.ordinal());
    }
}
