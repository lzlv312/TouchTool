package top.bogey.touch_tool.bean.pin.pin_objects;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinAdd extends PinBase{
    private final Pin pin;

    public PinAdd(Pin pin) {
        super(PinType.ADD);
        this.pin = pin;
    }

    public PinAdd(JsonObject jsonObject) {
        super(jsonObject);
        pin = GsonUtil.getAsObject(jsonObject, "pin", Pin.class, null);
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isInstance(PinBase pin) {
        return false;
    }

    public Pin getPin() {
        return pin;
    }
}
