package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinDate;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinPeriodic;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinTime;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.utils.AppUtil;

public class TimeStartAction extends StartAction {
    private final transient Pin datePin = new NotLinkAblePin(new PinDate(), R.string.time_start_action_date);
    private final transient Pin timePin = new NotLinkAblePin(new PinTime(), R.string.time_start_action_time);
    private final transient Pin periodic = new NotLinkAblePin(new PinPeriodic(), R.string.time_start_action_periodic);

    public TimeStartAction() {
        super(ActionType.TIME_START);
        addPins(datePin, timePin, periodic);
    }

    public TimeStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(datePin, timePin, periodic);
    }

    public long getStartTime() {
        long date = datePin.getValue(PinDate.class).getValue();
        long time = datePin.getValue(PinTime.class).getValue();
        return AppUtil.mergeDateTime(date, time);
    }

    public long getPeriodic() {
        return periodic.getValue(PinPeriodic.class).getValue();
    }
}
