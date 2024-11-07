package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import java.util.Calendar;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.service.TaskRunnable;

public class GetDateAction extends CalculateAction {
    protected final transient Pin yearPin = new Pin(new PinInteger(), R.string.get_date_action_year, true);
    protected final transient Pin monthPin = new Pin(new PinInteger(), R.string.get_date_action_month, true);
    protected final transient Pin dayPin = new Pin(new PinInteger(), R.string.get_date_action_day, true);
    protected final transient Pin weekPin = new Pin(new PinInteger(), R.string.get_date_action_week, true);

    public GetDateAction() {
        super(ActionType.GET_CURRENT_DATE);
        addPins(yearPin, monthPin, dayPin, weekPin);
    }

    public GetDateAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(yearPin, monthPin, dayPin, weekPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        yearPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.YEAR));
        monthPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.MONTH) + 1);
        dayPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.DAY_OF_MONTH));
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) + 1 - Calendar.MONDAY;
        if (weekDay <= 0) weekDay += 7;
        weekPin.getValue(PinInteger.class).setValue(weekDay);
    }
}
