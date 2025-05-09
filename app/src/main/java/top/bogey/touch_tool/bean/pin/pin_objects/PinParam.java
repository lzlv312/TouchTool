package top.bogey.touch_tool.bean.pin.pin_objects;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.PinInfo;

public class PinParam extends PinBase {
    public PinParam() {
        super(PinType.PARAM);
    }

    public PinParam(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void reset() {

    }

    @Override
    public void sync(PinBase value) {

    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean linkFromAble(PinBase pin) {
        return PinInfo.isCaseAblePin(pin);
    }

    @Override
    public boolean linkToAble(PinBase pin) {
        return PinInfo.isCaseAblePin(pin);
    }
}
