package top.bogey.touch_tool.bean.pin.pin_objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import top.bogey.touch_tool.utils.GsonUtil;

public class PinMap extends PinObject implements Map<PinObject, PinObject> {
    protected PinType keyType = PinType.OBJECT;
    protected PinType valueType = PinType.OBJECT;
    protected Map<PinObject, PinObject> valueMap = new HashMap<>();
    protected boolean changeAble = true;
    protected boolean dynamic = false;

    public PinMap() {
        super(PinType.MAP);
    }

    public PinMap(boolean dynamic) {
        this();
        this.dynamic = dynamic;
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

    public PinMap(PinType keyType, PinType valueType, boolean changeAble, boolean dynamic) {
        this();
        this.keyType = keyType;
        this.valueType = valueType;
        this.changeAble = changeAble;
        this.dynamic = dynamic;
    }

    public PinMap(JsonObject jsonObject) {
        super(jsonObject);
        keyType = GsonUtil.getAsObject(jsonObject, "keyType", PinType.class, PinType.OBJECT);
        valueType = GsonUtil.getAsObject(jsonObject, "valueType", PinType.class, PinType.OBJECT);
        changeAble = GsonUtil.getAsBoolean(jsonObject, "changeAble", true);
        dynamic = GsonUtil.getAsBoolean(jsonObject, "dynamic", false);
        valueMap = GsonUtil.getAsObject(jsonObject, "valueMap", TypeToken.getParameterized(HashMap.class, PinBase.class, PinBase.class).getType(), new HashMap<>());
    }

    @Override
    public void reset() {
        super.reset();
        valueMap.clear();
    }

    @Override
    public void sync(PinBase value) {
        if (value instanceof PinMap pinMap) {
            keyType = pinMap.keyType;
            valueType = pinMap.valueType;
            changeAble = pinMap.changeAble;
            dynamic = pinMap.dynamic;
            valueMap = pinMap.valueMap;
        }
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
                if (dynamic && keyType == PinType.OBJECT) {
                    if (valueType == PinType.OBJECT) {
                        return true;
                    }
                    return valueType == pinMap.valueType;
                }

                if (dynamic && valueType == PinType.OBJECT) {
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

    public boolean isChangeAble() {
        return changeAble;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PinMap pinMap)) return false;
        if (!super.equals(object)) return false;

        return isChangeAble() == pinMap.isChangeAble() && getKeyType() == pinMap.getKeyType() && getValueType() == pinMap.getValueType() && valueMap.equals(pinMap.valueMap);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getKeyType().hashCode();
        result = 31 * result + getValueType().hashCode();
        result = 31 * result + valueMap.hashCode();
        result = 31 * result + Boolean.hashCode(isChangeAble());
        return result;
    }

    @Override
    public int size() {
        return valueMap.size();
    }

    @Override
    public boolean isEmpty() {
        return valueMap.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return valueMap.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return valueMap.containsValue(value);
    }

    @Nullable
    @Override
    public PinObject get(@Nullable Object key) {
        return valueMap.get(key);
    }

    @Nullable
    @Override
    public PinObject put(PinObject key, PinObject value) {
        return valueMap.put(key, value);
    }

    @Nullable
    @Override
    public PinObject remove(@Nullable Object key) {
        return valueMap.remove(key);
    }

    @Override
    public void putAll(@NonNull Map<? extends PinObject, ? extends PinObject> m) {
        valueMap.putAll(m);
    }

    @Override
    public void clear() {
        valueMap.clear();
    }

    @NonNull
    @Override
    public Set<PinObject> keySet() {
        return valueMap.keySet();
    }

    @NonNull
    @Override
    public Collection<PinObject> values() {
        return valueMap.values();
    }

    @NonNull
    @Override
    public Set<Entry<PinObject, PinObject>> entrySet() {
        return valueMap.entrySet();
    }

    public static class PinMapSerializer implements JsonSerializer<PinMap> {
        public JsonElement serialize(PinMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", src.getType().name());
            jsonObject.addProperty("subType", src.getSubType().name());
            jsonObject.addProperty("keyType", src.keyType.name());
            jsonObject.addProperty("valueType", src.valueType.name());
            jsonObject.add("valueMap", context.serialize(src.valueMap));
            jsonObject.addProperty("changeAble", src.isChangeAble());
            jsonObject.addProperty("dynamic", src.dynamic);
            return jsonObject;
        }
    }
}
