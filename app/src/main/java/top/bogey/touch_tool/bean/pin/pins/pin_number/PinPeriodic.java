package top.bogey.touch_tool.bean.pin.pins.pin_number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;

public class PinPeriodic extends PinLong{
    public PinPeriodic() {
        super(PinSubType.PERIODIC);
    }

    public PinPeriodic(long value) {
        super(PinSubType.PERIODIC, value);
    }

    public PinPeriodic(JsonObject jsonObject) {
        super(jsonObject);
    }
}
