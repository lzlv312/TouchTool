package top.bogey.touch_tool.service;

import top.bogey.touch_tool.bean.action.Action;

public interface ITaskListener {
    void onStart(TaskRunnable runnable);

    void onExecute(TaskRunnable runnable, Action action, int progress);

    void onCalculate(TaskRunnable runnable, Action action);

    void onFinish(TaskRunnable runnable);
}
