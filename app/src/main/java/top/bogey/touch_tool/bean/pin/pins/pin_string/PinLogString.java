package top.bogey.touch_tool.bean.pin.pins.pin_string;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinBase;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinLogString extends PinString {

    public PinLogString() {
        super(PinSubType.LOG);
    }

    public PinLogString(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsString(jsonObject, "value", null);
    }

    @Override
    public boolean isInstance(PinBase pin) {
        return true;
    }
}
