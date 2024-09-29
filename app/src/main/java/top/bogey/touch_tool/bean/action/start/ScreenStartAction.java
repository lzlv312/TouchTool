package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class ScreenStartAction extends StartAction {
    private final transient Pin statePin = new Pin(new PinSingleSelect(R.array.screen_state), R.string.screen_start_action_state, true);

    public ScreenStartAction() {
        super(ActionType.SCREEN_START);
        addPins(statePin);
    }

    public ScreenStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(statePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        TaskInfoSummary.PhoneState phoneState = TaskInfoSummary.getInstance().getPhoneState();
        statePin.getValue(PinSingleSelect.class).setValue(phoneState.name());
        super.execute(runnable, pin);
    }
}
