package top.bogey.touch_tool.bean.pin.pins.pin_string;

import androidx.annotation.ArrayRes;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pins.PinBase;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinSingleSelect extends PinString {
    private List<String> options = new ArrayList<>();
    private boolean dynamic = false;

    public PinSingleSelect() {
        super(PinSubType.SINGLE_SELECT);
    }

    public PinSingleSelect(boolean dynamic) {
        this();
        this.dynamic = dynamic;
    }

    public PinSingleSelect(@ArrayRes int optionsResId) {
        this();
        String[] strings = MainApplication.getInstance().getResources().getStringArray(optionsResId);
        options.addAll(Arrays.asList(strings));
    }

    public PinSingleSelect(@ArrayRes int optionsResId, int defaultIndex) {
        this();
        String[] strings = MainApplication.getInstance().getResources().getStringArray(optionsResId);
        options.addAll(Arrays.asList(strings));
        setValue(options.get(defaultIndex));
    }

    public PinSingleSelect(List<String> options) {
        this();
        this.options = options;
    }

    public PinSingleSelect(JsonObject jsonObject) {
        super(jsonObject);
        options = GsonUtil.getAsObject(jsonObject, "options", TypeToken.getParameterized(ArrayList.class, String.class).getType(), new ArrayList<>());
    }

    @Override
    public void reset() {
        super.reset();
        if (dynamic) options.clear();
    }

    @Override
    public boolean isInstance(PinBase pin) {
        if (super.isInstance(pin)) {
            if (pin instanceof PinSingleSelect pinSingleSelect) {
                if (dynamic) return true;
                return options.equals(pinSingleSelect.getOptions());
            }
        }
        return false;
    }

    public String getOptionsString() {
        StringBuilder builder = new StringBuilder();
        for (String s : getOptions()) {
            builder.append(s);
            builder.append(",");
        }
        if (builder.length() == 0) return "";
        return builder.substring(0, builder.length() - 1);
    }

    public int getIndex() {
        if (value == null || value.isEmpty()) return 0;
        int index = options.indexOf(value);
        return Math.max(index, 0);
    }

    public void setIndex(int index) {
        if (index < 0 || index >= options.size()) return;
        setValue(options.get(index));
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
        reset();
    }

    @Override
    public String getValue() {
        if (options.isEmpty()) return "";
        String string = super.getValue();
        if (string == null || string.isEmpty()) return getOptions().get(0);
        return string;
    }

    @Override
    public void setValue(String value) {
        if (options.contains(value)) super.setValue(value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        PinSingleSelect pinSingleSelect = (PinSingleSelect) object;
        return Objects.equals(getOptions(), pinSingleSelect.getOptions());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getOptions());
        return result;
    }
}
