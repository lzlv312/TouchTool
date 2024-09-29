package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinDate;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinPeriodic;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinTime;
import top.bogey.touch_tool.utils.AppUtil;

public class TimeStartAction extends StartAction {
    private final transient Pin datePin = new Pin(new PinDate(), R.string.time_start_action_date) {
        @Override
        public boolean linkAble() {
            return false;
        }
    };
    private final transient Pin timePin = new Pin(new PinTime(), R.string.time_start_action_time) {
        @Override
        public boolean linkAble() {
            return false;
        }
    };
    private final transient Pin periodic = new Pin(new PinPeriodic(), R.string.time_start_action_periodic) {
        @Override
        public boolean linkAble() {
            return false;
        }
    };

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
