package top.bogey.touch_tool.bean.pin.pin_objects;

import com.google.gson.JsonObject;

public class PinCommon extends PinObject {
    public PinCommon() {
        super(PinType.COMMON);
    }

    public PinCommon(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public boolean isInstance(PinBase pin) {
        return false;
    }
}
