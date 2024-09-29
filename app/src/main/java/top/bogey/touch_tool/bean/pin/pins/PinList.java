package top.bogey.touch_tool.bean.pin.pins;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.utils.GsonUtil;

public class PinList extends PinObject {
    protected PinType valueType = PinType.OBJECT;
    protected List<PinObject> values = new ArrayList<>();
    protected boolean changeAble;

    public PinList() {
        super(PinType.LIST);
        changeAble = true;
    }

    public PinList(PinType valueType) {
        this();
        this.valueType = valueType;
    }

    public PinList(PinType valueType, boolean changeAble) {
        this();
        this.valueType = valueType;
        this.changeAble = changeAble;
    }

    protected PinList(PinSubType subType) {
        super(PinType.LIST, subType);
    }

    protected PinList(PinSubType subType, PinType valueType) {
        super(PinType.LIST, subType);
        this.valueType = valueType;
    }

    protected PinList(PinSubType subType, PinType valueType, boolean changeAble) {
        super(PinType.LIST, subType);
        this.valueType = valueType;
        this.changeAble = changeAble;
    }

    public PinList(JsonObject jsonObject) {
        super(jsonObject);
        valueType = GsonUtil.getAsObject(jsonObject, "valueType", PinType.class, PinType.OBJECT);
        changeAble = GsonUtil.getAsBoolean(jsonObject, "changeAble", true);
        values = GsonUtil.getAsObject(jsonObject, "list", TypeToken.getParameterized(ArrayList.class, PinBase.class).getType(), new ArrayList<>());
    }

    public boolean contains(PinObject pinObject) {
        return values.contains(pinObject);
    }

    @Override
    public void reset() {
        super.reset();
        values.clear();
    }

    @Override
    public PinList copy() {
        PinList pinList = new PinList(valueType, changeAble);
        for (PinObject value : values) {
            pinList.values.add((PinObject) value.copy());
        }
        return pinList;
    }

    @Override
    public boolean isInstance(PinBase pin) {
        if (super.isInstance(pin)) {
            if (pin instanceof PinList pinList) {
                // 自己为OBJECT，那么可以任意类型连上我
                if (valueType == PinType.OBJECT) return true;
                return pinList.valueType == valueType;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return values.toString();
    }

    public PinType getValueType() {
        return valueType;
    }

    public void setValueType(PinType valueType) {
        this.valueType = valueType;
        reset();
    }

    public List<PinObject> getValues() {
        return values;
    }

    public void setValues(List<PinObject> values) {
        this.values = values;
    }

    public boolean isChangeAble() {
        return changeAble;
    }

    public void setChangeAble(boolean changeAble) {
        this.changeAble = changeAble;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinList pinList = (PinList) o;

        if (isChangeAble() != pinList.isChangeAble()) return false;
        if (getValueType() != pinList.getValueType()) return false;
        return getValues().equals(pinList.getValues());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getValueType().hashCode();
        result = 31 * result + getValues().hashCode();
        result = 31 * result + (isChangeAble() ? 1 : 0);
        return result;
    }
}
