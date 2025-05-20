package top.bogey.touch_tool.bean.pin.pin_objects;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import top.bogey.touch_tool.bean.base.Copyable;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.utils.GsonUtil;

public abstract class PinBase implements Copyable {
    protected final PinType type;
    protected final PinSubType subType;

    protected PinBase() {
        this(PinType.NONE);
    }

    protected PinBase(PinType type) {
        this(type, PinSubType.NORMAL);
    }

    protected PinBase(PinType type, PinSubType subType) {
        this.type = type;
        this.subType = subType;
    }

    protected PinBase(JsonObject jsonObject) {
        type = GsonUtil.getAsObject(jsonObject, "type", PinType.class, PinType.NONE);
        subType = GsonUtil.getAsObject(jsonObject, "subType", PinSubType.class, PinSubType.NORMAL);
    }

    public abstract void reset();

    public abstract void sync(PinBase value);

    // 是否为动态值
    public abstract boolean isDynamic();

    @Override
    public PinBase copy() {
        return GsonUtil.copy(this, PinBase.class);
    }

    @Override
    public PinBase newCopy() {
        PinBase copy = copy();
        copy.reset();
        return copy;
    }

    public boolean linkFromAble(PinBase pin) {
        return getClass().isInstance(pin);
    }

    public boolean linkToAble(PinBase pin) {
        return pin.getClass().isInstance(this);
    }

    public PinType getType() {
        return type;
    }

    public PinSubType getSubType() {
        return subType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PinBase pinBase)) return false;

        return getType() == pinBase.getType() && getSubType() == pinBase.getSubType();
    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + getSubType().hashCode();
        return result;
    }

    public static class PinBaseDeserializer implements JsonDeserializer<PinBase> {
        @Override
        public PinBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            PinType type = GsonUtil.getAsObject(jsonObject, "type", PinType.class, PinType.NONE);
            PinSubType subType = GsonUtil.getAsObject(jsonObject, "subType", PinSubType.class, PinSubType.NORMAL);
            PinInfo pinInfo = PinInfo.getPinInfo(type, subType);
            if (pinInfo == null)
                return null;
            try {
                Constructor<? extends PinBase> constructor = pinInfo.getClazz().getConstructor(JsonObject.class);
                return constructor.newInstance(jsonObject);
            } catch (Exception e) {
                Log.d("TAG", "deserialize pin: " + pinInfo);
                Log.d("TAG", "deserialize pin json: " + json);
                throw new JsonParseException(e);
            }
        }
    }
}
