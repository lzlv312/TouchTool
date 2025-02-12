package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import java.util.concurrent.atomic.AtomicBoolean;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;

public class CaptureSwitchAction extends ExecuteAction {
    private final transient Pin valuePin = new Pin(new PinBoolean(true), R.string.capture_switch_action_switch);
    private final transient Pin waitPin = new Pin(new PinBoolean(true), R.string.capture_switch_action_wait, false, false, true);

    public CaptureSwitchAction() {
        super(ActionType.SWITCH_CAPTURE);
        addPins(valuePin, waitPin);
    }

    public CaptureSwitchAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(valuePin, waitPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinBoolean value = getPinValue(runnable, valuePin);
        PinBoolean wait = getPinValue(runnable, waitPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        AtomicBoolean needPaused = new AtomicBoolean(true);
        if (value.getValue()) {
            if (wait.getValue()) {
                service.startCapture(result -> {
                    needPaused.set(false);
                    runnable.resume();
                });
                if (needPaused.get()) runnable.await();
            } else {
                service.startCapture(null);
            }
        } else {
            service.stopCapture();
        }
        executeNext(runnable, outPin);
    }
}
