package top.bogey.touch_tool.bean.pin.pins.pin_string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;

public class PinUrlString extends PinString{

    public PinUrlString() {
        super(PinSubType.URL);
    }

    public PinUrlString(String str) {
        super(PinSubType.URL, str);
    }

    public PinUrlString(JsonObject jsonObject) {
        super(jsonObject);
    }
}
