package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskListener;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class SinglePlayView extends PlayFloatItemView implements TaskListener, FloatInterface {
    private final String tag = UUID.randomUUID().toString();

    public SinglePlayView(@NonNull Context context, Task task, StartAction action) {
        super(context, task, action);
        int px = (int) DisplayUtil.dp2px(context, 36);
        DisplayUtil.setViewWidth(binding.cardLayout, px);
        DisplayUtil.setViewHeight(binding.cardLayout, px);
        DisplayUtil.setViewWidth(binding.title, px);
        DisplayUtil.setViewHeight(binding.title, px);
        binding.playButton.setIndicatorSize(px);
    }

    @Override
    public void tryRemoveFromParent() {
        if (playing) remove = true;
        else dismiss();
    }

    @Override
    public void onFinish(TaskRunnable runnable) {
        post(() -> {
            stop();
            if (remove) dismiss();
        });
    }

    @Override
    public void show() {
        ManualStartAction action = (ManualStartAction) startAction;
        FloatWindow.with(MainApplication.getInstance().getService())
                .setTag(tag)
                .setLayout(this)
                .setLocation(action.getAnchor(), action.getShowPos().x, action.getShowPos().y)
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(tag);
    }

    public Action getStartAction() {
        return startAction;
    }
}
