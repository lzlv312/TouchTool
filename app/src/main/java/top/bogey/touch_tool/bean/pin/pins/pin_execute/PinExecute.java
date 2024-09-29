package top.bogey.touch_tool.bean.pin.pins.pin_execute;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pins.PinBase;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.bean.pin.pins.PinType;

public class PinExecute extends PinBase {

    public PinExecute() {
        super(PinType.EXECUTE);
    }

    protected PinExecute(PinSubType subType) {
        super(PinType.EXECUTE, subType);
    }

    public PinExecute(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void reset() {

    }
}
