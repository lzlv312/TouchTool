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
import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.FloatChoiceExecuteBinding;
import top.bogey.touch_tool.databinding.FloatChoiceExecuteItemBinding;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.callback.StringResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class ChoiceExecuteFloatView extends FrameLayout implements FloatInterface {
    private final FloatChoiceExecuteBinding binding;
    private StringResultCallback callback;

    public static String showChoice(List<Choice> choices, StringResultCallback callback, Point location) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return null;
        String tag = UUID.randomUUID().toString();
        new Handler(Looper.getMainLooper()).post(() -> {
            ChoiceExecuteFloatView choiceView = new ChoiceExecuteFloatView(keepView.getContext(), tag);
            choiceView.show();
            choiceView.innerShowChoice(choices, callback, location);
        });
        return tag;
    }

    public ChoiceExecuteFloatView(@NonNull Context context, String tag) {
        super(context);
        binding = FloatChoiceExecuteBinding.inflate(LayoutInflater.from(context), this, true);

        binding.closeButton.setOnClickListener(v -> {
            if (callback != null) callback.onResult(null);
            dismiss();
        });
    }

    public void innerShowChoice(List<Choice> choices, StringResultCallback callback, Point location) {
        if (location.x == 0 && location.y == 0) {
            FloatWindow.setLocation(KeepAliveFloatView.class.getName(), EAnchor.CENTER, location);
        } else {
            FloatWindow.setLocation(KeepAliveFloatView.class.getName(), EAnchor.TOP_LEFT, location);
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
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(ChoiceExecuteFloatView.class.getName())
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(ChoiceExecuteFloatView.class.getName());
    }

    public record Choice(String id, String title, Bitmap icon) {
    }
}
