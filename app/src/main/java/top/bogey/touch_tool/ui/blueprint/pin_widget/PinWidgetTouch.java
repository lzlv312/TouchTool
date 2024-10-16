package top.bogey.touch_tool.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinTouchPath;
import top.bogey.touch_tool.databinding.PinWidgetTouchBinding;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.picker.TouchPickerPreview;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.ui.custom.TouchPathView;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class PinWidgetTouch extends PinWidget<PinTouchPath> {
    private final PinWidgetTouchBinding binding;

    public PinWidgetTouch(@NonNull Context context, ActionCard card, PinView pinView, PinTouchPath pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetTouchBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    protected void initBase() {
        binding.pathView.setPath(pinBase.getValue());

        binding.pathView.setOnClickListener(v -> {
            TouchPathView view = new TouchPathView(getContext(), pinBase.getValue(), true);
            Point size = DisplayUtil.getScreenSize(getContext());
            DisplayUtil.setViewWidth(view, size.x / 2);
            DisplayUtil.setViewHeight(view, size.y / 2);

            new MaterialAlertDialogBuilder(getContext())
                    .setPositiveButton(R.string.enter, null)
                    .setView(view)
                    .show();
        });

        binding.pickButton.setOnClickListener(v -> new TouchPickerPreview(getContext(), result -> {
            pinBase.setValue(result.getValue());
            pinBase.setAnchor(result.getAnchor());
            binding.pathView.setPath(result.getValue());
        }, pinBase).show());
    }

    @Override
    protected void initCustom() {

    }
}
