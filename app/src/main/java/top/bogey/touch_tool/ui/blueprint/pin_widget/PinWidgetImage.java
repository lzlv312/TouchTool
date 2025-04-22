package top.bogey.touch_tool.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.databinding.PinWidgetImageBinding;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.picker.ImagePickerPreview;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.ui.blueprint.selecter.select_icon.SelectIconDialog;

@SuppressLint("ViewConstructor")
public class PinWidgetImage extends PinWidget<PinImage> {
    private final PinWidgetImageBinding binding;

    public PinWidgetImage(@NonNull Context context, ActionCard card, PinView pinView, PinImage pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetImageBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    protected void initBase() {
        binding.image.setImageBitmap(pinBase.getImage());
        switch (pinBase.getSubType()) {
            case NORMAL -> binding.pickButton.setOnClickListener(v -> new ImagePickerPreview(getContext(), image -> {
                pinBase.setImage(image);
                pinView.getPin().notifyValueUpdated();
                binding.image.setImageBitmap(image);
            }, pinBase.getImage()).show());
            case WITH_ICON -> binding.pickButton.setOnClickListener(v -> new SelectIconDialog(getContext(), result -> {
                pinBase.setImage(result);
                pinView.getPin().notifyValueUpdated();
                binding.image.setImageBitmap(result);
            }).show());
        }
    }

    @Override
    protected void initCustom() {

    }
}
