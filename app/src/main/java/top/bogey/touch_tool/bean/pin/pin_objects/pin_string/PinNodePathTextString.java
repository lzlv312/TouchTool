package top.bogey.touch_tool.bean.pin.pin_objects.pin_string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;

public class PinNodePathTextString extends PinNodePathString {

    public PinNodePathTextString() {
        super(PinSubType.NODE_PATH_TEXT);
    }

    public PinNodePathTextString(JsonObject jsonObject) {
        super(jsonObject);
    }

}
