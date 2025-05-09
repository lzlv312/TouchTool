package top.bogey.touch_tool.ui.blueprint.pin_slot;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;

public abstract class PinSlotView extends FrameLayout {
    protected final Pin pin;

    public PinSlotView(@NonNull Context context, Pin pin) {
        super(context);
        this.pin = pin;
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
    }

    public abstract void setLinked(boolean linked);

    public @ColorInt int getPinColor() {
        if (pin.linkAble()) {
            PinBase pinBase = pin.getValue();
            return PinInfo.getPinInfo(pinBase.getType(), pinBase.getSubType()).getColor();
        }
        return Color.GRAY;
    }
}
