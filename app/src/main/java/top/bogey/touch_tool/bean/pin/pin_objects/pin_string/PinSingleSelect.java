package top.bogey.touch_tool.bean.pin.pin_objects.pin_string;

import androidx.annotation.ArrayRes;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
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
    public boolean linkFromAble(PinBase pin) {
        if (getType().getGroup() == pin.getType().getGroup()) {
            if (isDynamic() || pin.isDynamic()) return true;
            if (pin instanceof PinSingleSelect pinSingleSelect) {
                return getOptions().equals(pinSingleSelect.getOptions());
            }
        }
        return false;
    }

    @Override
    public boolean linkToAble(PinBase pin) {
        if (getType().getGroup() == pin.getType().getGroup()) {
            if (isDynamic() || pin.isDynamic()) return true;
            if (pin instanceof PinSingleSelect pinSingleSelect) {
                return getOptions().equals(pinSingleSelect.getOptions());
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
        if (value != null && !value.isEmpty()) {
            if (!options.contains(value)) reset();
        }
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

    public boolean isDynamic() {
        return dynamic && options.isEmpty();
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PinSingleSelect that)) return false;
        if (!super.equals(object)) return false;

        return dynamic == that.dynamic && getOptions().equals(that.getOptions());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getOptions().hashCode();
        result = 31 * result + Boolean.hashCode(dynamic);
        return result;
    }
}
