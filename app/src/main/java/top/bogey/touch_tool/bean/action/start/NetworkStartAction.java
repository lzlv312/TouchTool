package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class NetworkStartAction extends StartAction {
    private final transient Pin statePin = new Pin(new PinList(PinType.STRING, false), R.string.network_start_action_state, true);

    public NetworkStartAction() {
        super(ActionType.NETWORK_START);
        addPins(statePin);
    }

    public NetworkStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(statePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinList states = statePin.getValue();
        List<TaskInfoSummary.NotworkState> networkState = TaskInfoSummary.getInstance().getNetworkState();
        states.clear();
        for (TaskInfoSummary.NotworkState state : networkState) {
            states.add(new PinString(state.name()));
        }
        super.execute(runnable, pin);
    }
}
