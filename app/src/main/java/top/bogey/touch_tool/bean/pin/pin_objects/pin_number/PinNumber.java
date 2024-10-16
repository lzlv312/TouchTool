package top.bogey.touch_tool.bean.pin.pin_objects.pin_number;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;

public abstract class PinNumber<T extends Number> extends PinObject {
    protected T value;

    protected PinNumber() {
        super(PinType.NUMBER);
    }

    protected PinNumber(T value) {
        this();
        this.value = value;
    }

    protected PinNumber(PinSubType subType) {
        super(PinType.NUMBER, subType);
    }

    protected PinNumber(PinSubType subType, T value) {
        this(subType);
        this.value = value;
    }

    protected PinNumber(JsonObject jsonObject) {
        super(jsonObject);
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinNumber<?> pinNumber = (PinNumber<?>) o;

        return Objects.equals(value, pinNumber.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
