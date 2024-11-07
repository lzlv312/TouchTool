package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.FloatChoiceExecuteBinding;
import top.bogey.touch_tool.databinding.FloatChoiceExecuteItemBinding;
import top.bogey.touch_tool.ui.blueprint.picker.FloatBaseCallback;
import top.bogey.touch_tool.ui.setting.SettingSaver;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.callback.StringResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindowHelper;

@SuppressLint("ViewConstructor")
public class ChoiceExecuteFloatView extends FrameLayout implements FloatInterface {
    private final FloatChoiceExecuteBinding binding;
    private StringResultCallback callback;
    private boolean remember = false;

    public static void showChoice(List<Choice> choices, StringResultCallback callback, EAnchor anchor, Point location) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            ChoiceExecuteFloatView choiceView = new ChoiceExecuteFloatView(keepView.getContext());
            choiceView.show();
            choiceView.innerShowChoice(choices, callback, anchor, location);
        });
    }

    public ChoiceExecuteFloatView(@NonNull Context context) {
        super(context);
        binding = FloatChoiceExecuteBinding.inflate(LayoutInflater.from(context), this, true);

        binding.closeButton.setOnClickListener(v -> {
            if (callback != null) callback.onResult(null);
            dismiss();
        });
    }

    public void innerShowChoice(List<Choice> choices, StringResultCallback callback, EAnchor anchor, Point location) {
        if (location.x == -1 && location.y == -1) {
            remember = true;
            Point point = SettingSaver.getInstance().getChoiceViewPos();
            FloatWindow.setLocation(ChoiceExecuteFloatView.class.getName(), EAnchor.CENTER, point);
        } else {
            remember = false;
            FloatWindow.setLocation(ChoiceExecuteFloatView.class.getName(), anchor, location);
        }
        this.callback = callback;
        for (Choice choice : choices) {
            FloatChoiceExecuteItemBinding itemBinding = FloatChoiceExecuteItemBinding.inflate(LayoutInflater.from(getContext()), binding.flexBox, true);
            itemBinding.icon.setImageBitmap(choice.icon());
            itemBinding.titleText.setText(choice.title());
            itemBinding.getRoot().setOnClickListener(v -> {
                if (callback != null) callback.onResult(choice.id());
                dismiss();
            });
        }
    }

    @Override
    public void show() {
        Point point = SettingSaver.getInstance().getChoiceViewPos();
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(ChoiceExecuteFloatView.class.getName())
                .setSpecial(true)
                .setLocation(EAnchor.CENTER, point.x, point.y)
                .setCallback(new PlayFloatCallback())
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(ChoiceExecuteFloatView.class.getName());
    }

    public record Choice(String id, String title, Bitmap icon) {
    }

    private class PlayFloatCallback extends FloatBaseCallback {

        @Override
        public void onShow(String tag) {

        }

        @Override
        public void onDragEnd() {
            super.onDragEnd();
            FloatWindowHelper helper = FloatWindow.getHelper(ChoiceExecuteFloatView.class.getName());
            if (helper != null && remember) {
                Point point = helper.getRelativePoint();
                SettingSaver.getInstance().setChoiceViewPos(point);
            }
        }

        @Override
        public void onDismiss() {

        }
    }
}
