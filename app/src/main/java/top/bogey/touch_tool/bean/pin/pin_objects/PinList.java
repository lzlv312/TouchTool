package top.bogey.touch_tool.bean.pin.pin_objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import top.bogey.touch_tool.utils.GsonUtil;

public class PinList extends PinObject implements List<PinObject> {
    protected PinType valueType = PinType.OBJECT;
    protected List<PinObject> values = new ArrayList<>();
    protected boolean changeAble = false;
    protected boolean dynamic = true;

    public PinList() {
        super(PinType.LIST);
    }

    public PinList(PinType valueType) {
        this();
        this.valueType = valueType;
        dynamic = false;
    }

    public PinList(PinType valueType, boolean changeAble, boolean dynamic) {
        this();
        this.valueType = valueType;
        this.changeAble = changeAble;
        this.dynamic = dynamic;
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
        dynamic = GsonUtil.getAsBoolean(jsonObject, "dynamic", false);
        values = GsonUtil.getAsObject(jsonObject, "values", TypeToken.getParameterized(ArrayList.class, PinBase.class).getType(), new ArrayList<>());
    }

    @Override
    public void reset() {
        super.reset();
        clear();
    }

    @Override
    public PinList copy() {
        PinList pinList = new PinList(subType, valueType, changeAble);
        for (PinObject value : values) {
            pinList.values.add((PinObject) value.copy());
        }
        return pinList;
    }

    @Override
    public boolean isInstance(PinBase pin) {
        if (super.isInstance(pin)) {
            if (pin instanceof PinList pinList) {
                // 自己为OBJECT，且是动态的，那么可以任意类型连上我
                if (dynamic && valueType == PinType.OBJECT) return true;
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
    }

    public boolean isChangeAble() {
        return changeAble;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PinList that)) return false;
        if (!super.equals(object)) return false;

        return isChangeAble() == that.isChangeAble() && getValueType() == that.getValueType() && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getValueType().hashCode();
        result = 31 * result + values.hashCode();
        result = 31 * result + Boolean.hashCode(isChangeAble());
        return result;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return values.contains(o);
    }

    @NonNull
    @Override
    public Iterator<PinObject> iterator() {
        return values.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return values.toArray(a);
    }

    @Override
    public boolean add(PinObject pinObject) {
        return values.add(pinObject);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return values.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return new HashSet<>(values).containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends PinObject> c) {
        return values.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends PinObject> c) {
        return values.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public PinObject get(int index) {
        return values.get(index);
    }

    @Override
    public PinObject set(int index, PinObject element) {
        return values.set(index, element);
    }

    @Override
    public void add(int index, PinObject element) {
        values.add(index, element);
    }

    @Override
    public PinObject remove(int index) {
        return values.remove(index);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return values.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return values.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<PinObject> listIterator() {
        return values.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<PinObject> listIterator(int index) {
        return values.listIterator(index);
    }

    @NonNull
    @Override
    public List<PinObject> subList(int fromIndex, int toIndex) {
        return values.subList(fromIndex, toIndex);
    }

    public static class PinListSerializer implements JsonSerializer<PinList> {
        @Override
        public JsonElement serialize(PinList src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", src.getType().name());
            jsonObject.addProperty("subType", src.getSubType().name());
            jsonObject.addProperty("valueType", src.getValueType().name());
            jsonObject.add("values", context.serialize(src.values));
            jsonObject.addProperty("changeAble", src.isChangeAble());
            jsonObject.addProperty("dynamic", src.dynamic);
            return jsonObject;
        }
    }
}
