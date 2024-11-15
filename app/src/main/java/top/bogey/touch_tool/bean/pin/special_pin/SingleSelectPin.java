package top.bogey.touch_tool.bean.pin.special_pin;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;

public class SingleSelectPin extends Pin {

    public SingleSelectPin(JsonObject jsonObject) {
        super(jsonObject);
    }

    public SingleSelectPin(PinBase value) {
        super(value);
    }

    public SingleSelectPin(PinBase value, boolean out) {
        super(value, out);
    }

    public SingleSelectPin(PinBase value, int titleId) {
        super(value, titleId);
    }

    public SingleSelectPin(PinBase value, int titleId, boolean out) {
        super(value, titleId, out);
    }

    public SingleSelectPin(PinBase value, int titleId, boolean out, boolean dynamic) {
        super(value, titleId, out, dynamic);
    }

    public SingleSelectPin(PinBase value, int titleId, boolean out, boolean dynamic, boolean hide) {
        super(value, titleId, out, dynamic, hide);
    }

    @Override
    public void setValue(PinBase value) {
        if (value instanceof PinSingleSelect singleSelect && !singleSelect.isDynamic()) {
            PinSingleSelect pinSingleSelect = getValue();
            pinSingleSelect.setValue(singleSelect.getValue());
        } else {
            super.setValue(value);
        }
    }
}
