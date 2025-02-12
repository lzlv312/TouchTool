package top.bogey.touch_tool.bean.pin.pin_objects.pin_string;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinString extends PinObject {
    protected String value;

    public PinString() {
        super(PinType.STRING);
    }

    public PinString(String value) {
        this();
        this.value = value;
    }

    protected PinString(PinSubType subType) {
        super(PinType.STRING, subType);
    }

    protected PinString(PinSubType subType, String value) {
        this(subType);
        this.value = value;
    }

    public PinString(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsString(jsonObject, "value", null);
    }

    @Override
    public void reset() {
        super.reset();
        value = null;
    }

    @Override
    public void sync(PinBase value) {
        if (value instanceof PinString pinString) {
            this.value = pinString.value;
        }
    }

    @Override
    public boolean isInstance(PinBase pin) {
        return pin instanceof PinObject;
    }

    @Override
    public boolean cast(String value) {
        this.value = value;
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return value == null ? "" : value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinString pinString = (PinString) o;

        return getValue() != null ? getValue().equals(pinString.getValue()) : pinString.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
