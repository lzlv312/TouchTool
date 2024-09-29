package top.bogey.touch_tool.bean.pin.pins.pin_number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinLong extends PinNumber<Long>{

    public PinLong() {
        super(PinSubType.LONG);
        value = 0L;
    }

    public PinLong(long value) {
        super(PinSubType.LONG, value);
    }

    protected PinLong(PinSubType subType) {
        super(subType);
        value = 0L;
    }

    protected PinLong(PinSubType subType, long value) {
        super(subType, value);
    }

    public PinLong(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsLong(jsonObject, "value", 0);
    }

    @Override
    public void reset() {
        super.reset();
        value = 0L;
    }

    @Override
    public boolean cast(String value) {
        try {
            this.value = Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
}
