package top.bogey.touch_tool.bean.action.point;

import android.graphics.Point;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinTouchPath;
import top.bogey.touch_tool.service.TaskRunnable;

public class PointToTouchAction extends CalculateAction implements DynamicPinsAction {
    private final static Pin morePin = new Pin(new PinPoint(), R.string.pin_point);
    private final transient Pin pointPin = new Pin(new PinPoint(), R.string.pin_point);
    private final transient Pin addPin = new Pin(new PinAdd(morePin), R.string.pin_add_pin);
    private final transient Pin timePin = new Pin(new PinInteger(100), R.string.point_to_touch_action_time);
    private final transient Pin touchPin = new Pin(new PinTouchPath(), R.string.pin_touch, true);

    public PointToTouchAction() {
        super(ActionType.POINT_TO_TOUCH);
        addPins(pointPin, addPin, timePin, touchPin);
    }

    public PointToTouchAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(pointPin);
        reAddPins(morePin);
        reAddPins(addPin, timePin, touchPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        List<Pin> dynamicPins = getDynamicPins();
        PinNumber<?> time = getPinValue(runnable, timePin);
        int everyTime = time.intValue() / Math.max((dynamicPins.size() - 1), 1);
        List<PinTouchPath.PathPart> pathParts = new ArrayList<>();
        for (int i = 0; i < dynamicPins.size(); i++) {
            Pin dynamicPin = dynamicPins.get(i);
            PinPoint point = getPinValue(runnable, dynamicPin);
            Point pos = point.getValue();
            PinTouchPath.PathPart pathPart = new PinTouchPath.PathPart(everyTime, pos.x, pos.y);
            pathParts.add(pathPart);
        }
        PinTouchPath pinTouchPath = new PinTouchPath(pathParts);
        touchPin.setValue(pinTouchPath);
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == pointPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
