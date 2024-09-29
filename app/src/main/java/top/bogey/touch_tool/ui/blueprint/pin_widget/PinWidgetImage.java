package top.bogey.touch_tool.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.bean.pin.pins.pin_scale_able.PinImage;
import top.bogey.touch_tool.databinding.PinWidgetImageBinding;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.picker.ImagePickerPreview;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;

@SuppressLint("ViewConstructor")
public class PinWidgetImage extends PinWidget<PinImage>{
    private final PinWidgetImageBinding binding;
    public PinWidgetImage(@NonNull Context context, ActionCard card, PinView pinView, PinImage pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetImageBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    protected void initBase() {
        binding.pickButton.setOnClickListener(v -> new ImagePickerPreview(getContext(), image -> {
            pinBase.setImage(image);
            binding.image.setImageBitmap(image);
        }, pinBase.getImage()).show());
        binding.image.setImageBitmap(pinBase.getImage());
    }

    @Override
    protected void initCustom() {

    }
}
