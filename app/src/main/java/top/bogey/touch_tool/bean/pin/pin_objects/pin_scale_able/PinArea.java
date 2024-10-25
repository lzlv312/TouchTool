package top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinArea extends PinScaleAble<Rect> {
    private final static Rect screenArea = DisplayUtil.getScreenArea(MainApplication.getInstance());

    public PinArea() {
        super(PinType.AREA);
        value = screenArea;
    }

    public PinArea(Rect area) {
        this();
        value = area;
    }

    public PinArea(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsObject(jsonObject, "value", Rect.class, screenArea);
    }

    public boolean isFullScreen() {
        return value.isEmpty() || screenArea.equals(value);
    }

    @Override
    public void reset() {
        super.reset();
        value = screenArea;
    }

    @Override
    public boolean cast(String value) {
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+),(\\d+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            try {
                setScale();
                Rect area = new Rect();
                area.left = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
                area.top = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
                area.right = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
                area.bottom = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
                this.value = area;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        Rect area = getValue(EAnchor.TOP_LEFT);
        return super.toString() + "(" + area.left + "," + area.top + "," + area.right + "," + area.bottom + ")";
    }

    @Override
    public Rect getValue() {
        Rect area = super.getValue();
        float scale = getScale();
        if (scale != 1) {
            area = new Rect((int) (area.left * scale), (int) (area.top * scale), (int) (area.right * scale), (int) (area.bottom * scale));
        }
        return new Rect(area);
    }

    @Override
    public Rect getValue(EAnchor anchor) {
        Rect area = getValue();
        if (anchor == this.anchor) return area;
        Point anchorPoint = this.anchor.getAnchorPoint();
        area.offset(anchorPoint.x, anchorPoint.y);
        anchorPoint = anchor.getAnchorPoint();
        area.offset(-anchorPoint.x, -anchorPoint.y);
        return area;
    }

    @Override
    public void setValue(Rect value) {
        super.setValue(value);
    }

    @Override
    public void setValue(EAnchor anchor, Rect value) {
        Point anchorPoint = anchor.getAnchorPoint();
        value.offset(-anchorPoint.x, -anchorPoint.y);
        setValue(value);
        this.anchor = anchor;
    }
}
