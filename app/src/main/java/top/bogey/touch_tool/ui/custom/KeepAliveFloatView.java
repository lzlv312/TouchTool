package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.task.TaskListener;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;

public class KeepAliveFloatView extends FrameLayout implements FloatInterface, TaskListener {

    public KeepAliveFloatView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onStart(TaskRunnable runnable) {

    }

    @Override
    public void onExecute(TaskRunnable runnable, Action action, int progress) {

    }

    @Override
    public void onCalculate(TaskRunnable runnable, Action action) {

    }

    @Override
    public void onFinish(TaskRunnable runnable) {

    }

    @Override
    public void show() {

    }

    @Override
    public void dismiss() {

    }
}
