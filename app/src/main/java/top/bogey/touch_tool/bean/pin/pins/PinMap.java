package top.bogey.touch_tool.bean.pin.pins;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.utils.GsonUtil;

public class PinMap extends PinObject {
    protected PinType keyType = PinType.OBJECT;
    protected PinType valueType = PinType.OBJECT;
    protected Map<PinObject, PinObject> valueMap = new HashMap<>();
    protected boolean changeAble;

    public PinMap() {
        super(PinType.MAP);
    }

    public PinMap(PinType keyType, PinType valueType) {
        this();
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public PinMap(PinType keyType, PinType valueType, boolean changeAble) {
        this();
        this.keyType = keyType;
        this.valueType = valueType;
        this.changeAble = changeAble;
    }

    public PinMap(JsonObject jsonObject) {
        super(jsonObject);
        keyType = GsonUtil.getAsObject(jsonObject, "keyType", PinType.class, PinType.OBJECT);
        valueType = GsonUtil.getAsObject(jsonObject, "valueType", PinType.class, PinType.OBJECT);
        changeAble = GsonUtil.getAsBoolean(jsonObject, "changeAble", false);
        valueMap = GsonUtil.getAsObject(jsonObject, "valueMap", TypeToken.getParameterized(HashMap.class, PinBase.class, PinBase.class).getType(), new HashMap<>());
    }

    @Override
    public void reset() {
        super.reset();
        valueMap.clear();
    }

    @Override
    public PinMap copy() {
        PinMap pinMap = new PinMap(keyType, valueType, changeAble);
        valueMap.forEach((key, value) -> pinMap.valueMap.put((PinObject) key.copy(), (PinObject) value.copy()));
        return pinMap;
    }

    @Override
    public boolean isInstance(PinBase pin) {
        if (super.isInstance(pin)) {
            if (pin instanceof PinMap pinMap) {
                if (keyType == PinType.OBJECT) {
                    if (valueType == PinType.OBJECT) {
                        return true;
                    }
                    return valueType == pinMap.valueType;
                }

                if (valueType == PinType.OBJECT) {
                    return keyType == pinMap.keyType;
                }

                return keyType == pinMap.keyType && valueType == pinMap.valueType;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return valueMap.toString();
    }

    public PinType getKeyType() {
        return keyType;
    }

    public void setKeyType(PinType keyType) {
        this.keyType = keyType;
        reset();
    }

    public PinType getValueType() {
        return valueType;
    }

    public void setValueType(PinType valueType) {
        this.valueType = valueType;
        reset();
    }

    public Map<PinObject, PinObject> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<PinObject, PinObject> valueMap) {
        this.valueMap = valueMap;
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

        PinMap pinMap = (PinMap) o;

        if (isChangeAble() != pinMap.isChangeAble()) return false;
        if (getKeyType() != pinMap.getKeyType()) return false;
        if (getValueType() != pinMap.getValueType()) return false;
        return getValueMap().equals(pinMap.getValueMap());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getKeyType().hashCode();
        result = 31 * result + getValueType().hashCode();
        result = 31 * result + getValueMap().hashCode();
        result = 31 * result + (isChangeAble() ? 1 : 0);
        return result;
    }
}
