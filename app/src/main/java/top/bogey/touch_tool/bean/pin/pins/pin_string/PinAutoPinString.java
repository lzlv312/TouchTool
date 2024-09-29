package top.bogey.touch_tool.bean.pin.pins.pin_string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;

public class PinAutoPinString extends PinString{

    public PinAutoPinString() {
        super(PinSubType.AUTO_PIN);
    }

    public PinAutoPinString(String str) {
        super(PinSubType.AUTO_PIN, str);
    }

    public PinAutoPinString(JsonObject jsonObject) {
        super(jsonObject);
    }
}
