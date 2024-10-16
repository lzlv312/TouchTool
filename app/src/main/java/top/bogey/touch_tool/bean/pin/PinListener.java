package top.bogey.touch_tool.bean.pin;

import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;

public interface PinListener {
    void onLinkedTo(Pin origin, Pin to);

    void onUnLinkedFrom(Pin origin, Pin from);

    void onTypeChanged(Pin origin, Class<? extends PinBase> type);

    void onValueChanged(Pin origin, PinBase value);

    void onTitleChanged(Pin origin, String title);
}
