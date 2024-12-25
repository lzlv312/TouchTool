package top.bogey.touch_tool.ui.blueprint.pin_slot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.databinding.PinSlotMapBinding;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class MapPinSlotView extends PinSlotView {
    private final PinSlotMapBinding binding;

    public MapPinSlotView(@NonNull Context context, Pin pin) {
        super(context, pin);
        binding = PinSlotMapBinding.inflate(LayoutInflater.from(context), this, true);
        binding.keySlot.setStrokeColor(getPinColor());
        binding.valueSlot.setStrokeColor(getPinValueColor());
    }

    @Override
    public void setLinked(boolean linked) {
        int keyColor = linked ? getPinColor() : DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant);
        binding.keySlot.setCardBackgroundColor(keyColor);
        binding.keySlot.setStrokeColor(getPinColor());

        int valueColor = linked ? getPinColor() : DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant);
        binding.valueSlot.setCardBackgroundColor(valueColor);
        binding.valueSlot.setStrokeColor(getPinValueColor());
    }

    @Override
    public int getPinColor() {
        if (pin.linkAble()) {
            PinBase pinBase = pin.getValue();
            PinMap pinMap = (PinMap) pinBase;
            return PinInfo.getPinInfo(pinMap.getKeyType()).getColor();
        }
        return Color.GRAY;
    }

    private @ColorInt int getPinValueColor() {
        if (pin.linkAble()) {
            PinBase pinBase = pin.getValue();
            PinMap pinMap = (PinMap) pinBase;
            return PinInfo.getPinInfo(pinMap.getValueType()).getColor();
        }
        return Color.GRAY;
    }
}
