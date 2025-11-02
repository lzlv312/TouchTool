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
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.normal.InputParamTemplateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatInputParamBinding;
import top.bogey.touch_tool.ui.blueprint.card.InputParamActionCard;
import top.bogey.touch_tool.ui.blueprint.picker.FloatBaseCallback;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;


@SuppressLint("ViewConstructor")
public class InputParamFloatView extends FrameLayout implements FloatInterface {
    private final FloatInputParamBinding binding;
    private BooleanResultCallback callback;

    private final Task task;
    private final Action action;

    public static void showInputParam(PinObject object, BooleanResultCallback callback) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            Point point = SettingSaver.getInstance().getManualChoiceViewPos();
            InputParamFloatView inputParamView = new InputParamFloatView(keepView.getThemeContext());
            inputParamView.show();
            inputParamView.innerShowToast(object, callback, EAnchor.CENTER, EAnchor.CENTER, point);
        });
    }

    public static void showInputParam(PinObject object, BooleanResultCallback callback, EAnchor anchor, EAnchor gravity, Point location) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            InputParamFloatView inputParamView = new InputParamFloatView(keepView.getThemeContext());
            inputParamView.show();
            inputParamView.innerShowToast(object, callback, anchor, gravity, location);
        });
    }

    private InputParamFloatView(@NonNull Context context) {
        super(context);
        binding = FloatInputParamBinding.inflate(LayoutInflater.from(context), this, true);

        binding.confirmButton.setOnClickListener(v -> {
            if (callback != null) callback.onResult(true);
            dismiss();
        });

        task = new Task();
        action = new InputParamTemplateAction();
        task.addAction(action);
    }

    private void innerShowToast(PinObject object, BooleanResultCallback callback, EAnchor anchor, EAnchor gravity, Point location) {
        FloatWindow.setLocation(InputParamFloatView.class.getName(), anchor, gravity, location);
        this.callback = callback;

        action.addPin(new Pin(object));
        InputParamActionCard card = new InputParamActionCard(getContext(), task, action);
        binding.contentBox.addView(card);
    }

    @Override
    public void show() {
        Point point = SettingSaver.getInstance().getManualChoiceViewPos();
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(InputParamFloatView.class.getName())
                .setLocation(EAnchor.CENTER, point.x, point.y)
                .setSpecial(true)
                .setExistEditText(true)
                .setCallback(new ActionFloatViewCallback(InputParamFloatView.class.getName()))
                .show();

        FloatBaseCallback.Block = true;
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(InputParamFloatView.class.getName());
        FloatBaseCallback.Block = false;
    }
}
