package top.bogey.touch_tool.bean.action.color;

import android.graphics.Point;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinColor;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class GetColorAction extends CalculateAction {
    private final transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);
    private final transient Pin colorPin = new Pin(new PinColor(), R.string.pin_color, true);

    public GetColorAction() {
        super(ActionType.GET_COLOR);
        addPins(posPin, colorPin);
    }

    public GetColorAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(posPin, colorPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinPoint pos = getPinValue(runnable, posPin);
        Point point = pos.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        service.tryGetScreenShot(bitmap -> {
            if (bitmap != null) {
                int pixel = bitmap.getPixel(point.x, point.y);
                colorPin.getValue(PinColor.class).setValue(new PinColor.ColorInfo(pixel, 0, Integer.MAX_VALUE));
            }
            runnable.resume();
        });
        runnable.pause();
    }
}
