package top.bogey.touch_tool.bean.pin.pins.pin_execute;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinStringExecute extends PinExecute {
    private String value;

    public PinStringExecute() {
        super(PinSubType.WITH_STRING);
    }

    public PinStringExecute(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsString(jsonObject, "value", null);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void reset() {
        super.reset();
        value = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinStringExecute that = (PinStringExecute) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }
}
