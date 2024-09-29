package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.pin.pins.pin_scale_able.PinColor;
import top.bogey.touch_tool.bean.pin.pins.pin_scale_able.PinImage;
import top.bogey.touch_tool.databinding.FloatStickScreenBinding;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class StickScreenFloatView extends FrameLayout implements FloatInterface {
    private final FloatStickScreenBinding binding;
    private final String tag;

    private boolean needDelete = false;

    public static String showStick(PinObject object, Point location) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return null;
        String tag = UUID.randomUUID().toString();
        new Handler(Looper.getMainLooper()).post(() -> {
            StickScreenFloatView stickView = new StickScreenFloatView(keepView.getContext(), tag);
            stickView.show();
            stickView.innerShowStick(object, location);
        });
        return tag;
    }

    public StickScreenFloatView(@NonNull Context context, String tag) {
        super(context);
        binding = FloatStickScreenBinding.inflate(LayoutInflater.from(context), this, true);
        this.tag = tag;

        binding.getRoot().setOnClickListener(v -> {
            if (needDelete) {
                dismiss();
            } else {
                needDelete = true;
                postDelayed(() -> needDelete = false, 1500);
            }
        });
    }

    public void innerShowStick(PinObject object, Point location) {
        if (location.x == 0 && location.y == 0) {
            FloatWindow.setLocation(KeepAliveFloatView.class.getName(), EAnchor.CENTER, location);
        } else {
            FloatWindow.setLocation(KeepAliveFloatView.class.getName(), EAnchor.TOP_LEFT, location);
        }
        if (object instanceof PinImage pinImage) {
            binding.image.setVisibility(VISIBLE);
            binding.image.setImageBitmap(pinImage.getImage());
        } else if (object instanceof PinColor pinColor) {
            binding.image.setVisibility(VISIBLE);
            binding.image.setImageTintList(ColorStateList.valueOf(pinColor.getValue().getColor()));

            binding.title.setVisibility(VISIBLE);
            binding.title.setText(pinColor.getValue().getColorString());
            binding.title.setTextSize(9);
        } else {
            binding.title.setVisibility(VISIBLE);
            binding.title.setText(object.toString());
        }
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(tag);
    }
}
