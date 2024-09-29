package top.bogey.touch_tool.bean.pin.pins.pin_scale_able;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.bean.pin.pins.PinType;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.GsonUtil;

public abstract class PinScaleAble<T> extends PinObject {
    protected int screen;
    protected EAnchor anchor = EAnchor.TOP_LEFT;
    protected T value;

    protected PinScaleAble(PinType type) {
        super(type);
    }

    protected PinScaleAble(PinType type, PinSubType subType) {
        super(type, subType);
    }

    protected PinScaleAble(JsonObject jsonObject) {
        super(jsonObject);
        screen = GsonUtil.getAsInt(jsonObject, "screen", 0);
        anchor = GsonUtil.getAsObject(jsonObject, "anchor", EAnchor.class, EAnchor.TOP_LEFT);
    }

    protected float getScale() {
        if (screen == 0) return 1f;
        return DisplayUtil.getScreenWidth(MainApplication.getInstance()) * 1f / screen;
    }

    protected void setScale() {
        screen = DisplayUtil.getScreenWidth(MainApplication.getInstance());
    }

    @Override
    public void reset() {
        screen = 0;
        anchor = EAnchor.TOP_LEFT;
    }

    @Override
    public boolean cast(String value) {
        try {
            screen = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(screen);
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public EAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(EAnchor anchor) {
        this.anchor = anchor;
    }

    public T getValue() {
        return value;
    }

    public abstract T getValue(EAnchor anchor);

    public void setValue(T value) {
        setScale();
        this.value = value;
    }

    public abstract void setValue(EAnchor anchor, T value);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinScaleAble<?> that = (PinScaleAble<?>) o;

        if (getScreen() != that.getScreen()) return false;
        if (getAnchor() != that.getAnchor()) return false;
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getScreen();
        result = 31 * result + getAnchor().hashCode();
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
