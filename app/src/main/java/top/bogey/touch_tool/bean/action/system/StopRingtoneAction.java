package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class StopRingtoneAction extends ExecuteAction {
    private final transient Pin ringPin = new Pin(new PinString(), R.string.play_ringtone_action_ringtone);

    public StopRingtoneAction() {
        super(ActionType.STOP_RINGTONE);
        addPins(ringPin);
    }

    public StopRingtoneAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(ringPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinString ring = getPinValue(runnable, ringPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        service.stopSound(ring.getValue());
        executeNext(runnable, outPin);
    }
}
