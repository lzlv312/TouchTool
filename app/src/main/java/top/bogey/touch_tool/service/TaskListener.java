package top.bogey.touch_tool.service;

import top.bogey.touch_tool.bean.action.Action;

public class TaskListener implements ITaskListener {
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
}
