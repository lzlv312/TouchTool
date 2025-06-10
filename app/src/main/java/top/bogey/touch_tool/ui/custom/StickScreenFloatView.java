package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinColor;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.databinding.FloatStickScreenBinding;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class StickScreenFloatView extends FrameLayout implements FloatInterface {
    private final FloatStickScreenBinding binding;
    private final String tag;

    private boolean needDelete = false;
    private float lastX = 0, lastY = 0, scale = 1;
    private int originWidth = 0, originHeight = 0;
    private boolean dragging = false;

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
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (originWidth == 0 || originHeight == 0) {
            originWidth = binding.scaleBox.getWidth();
            originHeight = binding.scaleBox.getHeight();
            originWidth = Math.max(1, originWidth);
            originHeight = Math.max(1, originHeight);
        }
    }

    private void innerShowStick(PinObject object, EAnchor anchor, Point location) {
        if (object instanceof PinImage pinImage) {
            binding.image.setImageBitmap(pinImage.getImage());
            binding.saveButton.setOnClickListener(v -> {
                AppUtil.saveImage(getContext(), pinImage.getImage());
                Toast.makeText(getContext(), R.string.save_image_action, Toast.LENGTH_SHORT).show();
            });
        } else {
            int textSize = 13;
            int textColor = DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorOnSurface);

            if (object instanceof PinColor pinColor) {
                binding.cardLayout.setCardBackgroundColor(pinColor.getValue().getColor());
                textSize = 9;
                textColor = DisplayUtil.getTextColor(pinColor.getValue().getColor());
            }
            int maxWidth = DisplayUtil.getScreenWidth(getContext()) * 2 / 3;
            Bitmap textBitmap = DisplayUtil.createTextBitmap(getContext(), object.toString(), textColor, textSize, maxWidth, 0, (int) DisplayUtil.dp2px(getContext(), 8));
            binding.image.setImageBitmap(textBitmap);

            binding.saveButton.setIconResource(R.drawable.icon_copy);
            binding.saveButton.setOnClickListener(v -> {
                ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(object.toString(), object.toString()));
                Toast.makeText(getContext(), R.string.copy_tips, Toast.LENGTH_SHORT).show();
            });
        }
        post(() -> {
            if (location.x < 0 || location.y < 0) {
                FloatWindow.setLocation(StickScreenFloatView.class.getName(), EAnchor.CENTER, new Point(0, 0));
            } else {
                FloatWindow.setLocation(StickScreenFloatView.class.getName(), anchor, location);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                int[] location = new int[2];
                binding.dragImage.getLocationOnScreen(location);
                if (new RectF(location[0], location[1], location[0] + binding.dragImage.getWidth(), location[1] + binding.dragImage.getHeight()).contains(x, y)) {
                    dragging = true;
                    FloatWindow.setDragAble(tag, false);
                    lastX = x;
                    lastY = y;
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    float dx = x - lastX;
                    float dy = y - lastY;
                    float ds = (dx + dy) / (originWidth + originHeight);
                    scale = Math.max(0.75f, Math.min(2, scale + ds));
                    binding.scaleBox.setScaleX(scale);
                    binding.scaleBox.setScaleY(scale);
                    ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
                    params.width = (int) (originWidth * scale);
                    params.height = (int) (originHeight * scale);
                    binding.getRoot().setLayoutParams(params);
                    FloatWindow.updateLayoutParam(tag);
                    lastX = x;
                    lastY = y;
                    return true;
                }
            }

            case MotionEvent.ACTION_UP -> {
                if (dragging) {
                    dragging = false;
                    FloatWindow.setDragAble(tag, true);
                } else {
                    if (needDelete) {
                        dismiss();
                    } else {
                        needDelete = true;
                        postDelayed(() -> needDelete = false, 1500);
                    }
                }
                return true;
            }
        }
        return false;
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
