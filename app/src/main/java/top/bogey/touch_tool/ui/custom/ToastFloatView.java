package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.FloatToastBinding;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;


@SuppressLint("ViewConstructor")
public class ToastFloatView extends FrameLayout implements FloatInterface {
    private final FloatToastBinding binding;
    private final Handler handler;

    public static void showToast(String msg, EAnchor anchor,  Point pos) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            ToastFloatView toastView = (ToastFloatView) FloatWindow.getView(ToastFloatView.class.getName());
            if (toastView == null) {
                toastView = new ToastFloatView(keepView.getContext());
                toastView.show();
            }
            toastView.innerShowToast(msg, anchor, pos);
        });
    }

    public ToastFloatView(@NonNull Context context) {
        super(context);
        binding = FloatToastBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
    }

    public void innerShowToast(String msg, EAnchor anchor, Point pos) {
        if (pos.x < 0 || pos.y < 0) {
            Point screenSize = DisplayUtil.getScreenSize(getContext());
            FloatWindow.setLocation(ToastFloatView.class.getName(), EAnchor.BOTTOM_CENTER, new Point(0, -screenSize.y / 5));
        } else {
            FloatWindow.setLocation(ToastFloatView.class.getName(), anchor, pos);
        }
        handler.removeCallbacksAndMessages(null);
        binding.title.setText(msg);
        handler.postDelayed(this::dismiss, 1500);
    }

    @Override
    public void show() {
        Point screenSize = DisplayUtil.getScreenSize(getContext());
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(ToastFloatView.class.getName())
                .setLocation(EAnchor.BOTTOM_CENTER, 0, -screenSize.y / 5)
                .setSpecial(true)
                .setDragAble(false)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(ToastFloatView.class.getName());
    }
}
