package top.bogey.touch_tool.bean.action.area;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.service.TaskRunnable;

public class GetAreaCenterAction extends CalculateAction {
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private final transient Pin xPin = new Pin(new PinInteger(), R.string.point_x, true);
    private final transient Pin yPin = new Pin(new PinInteger(), R.string.point_y, true);

    public GetAreaCenterAction() {
        super(ActionType.GET_AREA_CENTER);
        addPins(areaPin, xPin, yPin);
    }

    public GetAreaCenterAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(areaPin, xPin, yPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinArea area = getPinValue(runnable, areaPin);
        Rect areaRect = area.getValue();
        xPin.getValue(PinInteger.class).setValue(areaRect.left + areaRect.width() / 2);
        yPin.getValue(PinInteger.class).setValue(areaRect.top + areaRect.height() / 2);
    }
}
