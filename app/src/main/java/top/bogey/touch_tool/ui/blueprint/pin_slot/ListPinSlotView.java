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
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.databinding.PinSlotListBinding;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class ListPinSlotView extends PinSlotView {
    private final PinSlotListBinding binding;

    public ListPinSlotView(@NonNull Context context, Pin pin) {
        super(context, pin);
        binding = PinSlotListBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setStrokeColor(getPinColor());
    }

    @Override
    public void setLinked(boolean linked) {
        binding.getRoot().setCardBackgroundColor(linked ? getPinColor() : DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
        binding.getRoot().setStrokeColor(getPinColor());
    }

    @Override
    public @ColorInt int getPinColor() {
        if (pin.linkAble()) {
            PinBase pinBase = pin.getValue();
            PinList pinList = (PinList) pinBase;
            return PinInfo.getPinInfo(pinList.getValueType()).getColor();
        }
        return Color.GRAY;

    }
}
