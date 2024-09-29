package top.bogey.touch_tool.bean.pin.pins.pin_number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;

public class PinTime extends PinLong{
    public PinTime() {
        super(PinSubType.TIME);
        value = System.currentTimeMillis();
    }

    public PinTime(long value) {
        super(PinSubType.TIME, value);
    }

    public PinTime(JsonObject jsonObject) {
        super(jsonObject);
    }
}
