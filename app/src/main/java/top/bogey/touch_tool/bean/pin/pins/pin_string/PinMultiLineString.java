package top.bogey.touch_tool.bean.pin.pins.pin_string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;

public class PinMultiLineString extends PinString{

    public PinMultiLineString() {
        super(PinSubType.MULTI_LINE);
    }

    public PinMultiLineString(String str) {
        super(PinSubType.MULTI_LINE, str);
    }

    public PinMultiLineString(JsonObject jsonObject) {
        super(jsonObject);
    }
}
