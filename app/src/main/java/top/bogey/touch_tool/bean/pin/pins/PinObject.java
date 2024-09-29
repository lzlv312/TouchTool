package top.bogey.touch_tool.bean.pin.pins;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

public class PinObject extends PinBase {

    public PinObject() {
        super(PinType.OBJECT);
    }

    protected PinObject(PinType type) {
        super(type);
    }

    protected PinObject(PinSubType subType) {
        super(PinType.OBJECT, subType);
    }

    protected PinObject(PinType type, PinSubType subType) {
        super(type, subType);
    }

    public PinObject(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void reset() {

    }

    public boolean cast(String value) {
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
