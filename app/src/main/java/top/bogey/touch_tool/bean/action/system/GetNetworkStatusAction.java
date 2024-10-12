package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinList;
import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.pin.pins.PinType;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class GetNetworkStatusAction extends CalculateAction {
    private final transient Pin statusPin = new Pin(new PinList(PinType.STRING), R.string.get_screen_status_action_state, true);

    public GetNetworkStatusAction() {
        super(ActionType.GET_NETWORK_STATUS);
        addPins(statusPin);
    }

    public GetNetworkStatusAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(statusPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        List<PinObject> values = statusPin.getValue(PinList.class).getValues();
        List<TaskInfoSummary.NotworkState> state = TaskInfoSummary.getInstance().getNetworkState();
        for (TaskInfoSummary.NotworkState notworkState : state) {
            values.add(new PinString(notworkState.name()));
        }
    }
}
