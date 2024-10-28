package top.bogey.touch_tool.bean.action.point;

import android.os.Build;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinFloat;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinTouchPath;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class TouchAction extends ExecuteAction {
    private final transient Pin touchPin = new Pin(new PinTouchPath(), R.string.pin_touch);
    private final transient Pin timePin = new Pin(new PinFloat(), R.string.touch_action_time, false, false, true);
    private final transient Pin offsetPin = new Pin(new PinInteger(), R.string.touch_action_offset);

    public TouchAction() {
        super(ActionType.TOUCH);
        addPins(touchPin, timePin, offsetPin);
    }

    public TouchAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(touchPin, timePin, offsetPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinTouchPath path = getPinValue(runnable, touchPin);
        PinNumber<?> time = getPinValue(runnable, timePin);
        PinNumber<?> offset = getPinValue(runnable, offsetPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.runGesture(path.getStrokesList(time.floatValue(), offset.intValue()), result -> runnable.resume());
        } else {
            service.runGesture(path.getStrokes(time.floatValue(), offset.intValue()), result -> runnable.resume());
        }
        runnable.pause();
        executeNext(runnable, outPin);
    }
}
