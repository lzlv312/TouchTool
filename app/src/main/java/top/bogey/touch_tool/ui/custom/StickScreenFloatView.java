package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinColor;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.databinding.FloatStickScreenBinding;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class StickScreenFloatView extends FrameLayout implements FloatInterface {
    private final FloatStickScreenBinding binding;
    private final String tag;

    private boolean needDelete = false;

    public static String showStick(PinObject object, EAnchor anchor, Point location) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return null;
        String tag = UUID.randomUUID().toString();
        new Handler(Looper.getMainLooper()).post(() -> {
            StickScreenFloatView stickView = new StickScreenFloatView(keepView.getContext(), tag);
            stickView.show();
            stickView.innerShowStick(object, anchor, location);
        });
        return tag;
    }

    private StickScreenFloatView(@NonNull Context context, String tag) {
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

    private void innerShowStick(PinObject object, EAnchor anchor, Point location) {
        if (object instanceof PinImage pinImage) {
            binding.image.setVisibility(VISIBLE);
            binding.image.setImageBitmap(pinImage.getImage());
        } else if (object instanceof PinColor pinColor) {
            binding.cardLayout.setCardBackgroundColor(pinColor.getValue().getColor());

            binding.title.setVisibility(VISIBLE);
            binding.title.setText(pinColor.getValue().getColorString());
            binding.title.setTextColor(DisplayUtil.getTextColor(pinColor.getValue().getColor()));
            binding.title.setTextSize(9);
        } else {
            binding.title.setVisibility(VISIBLE);
            binding.title.setText(object.toString());
        }
        post(() -> {
            if (location.x < 0 || location.y < 0) {
                FloatWindow.setLocation(StickScreenFloatView.class.getName(), EAnchor.CENTER, new Point(0, 0));
            } else {
                FloatWindow.setLocation(StickScreenFloatView.class.getName(), anchor, location);
            }
        });
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setSpecial(true)
                .setLocation(EAnchor.CENTER, 0, 0)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(tag);
    }
}
