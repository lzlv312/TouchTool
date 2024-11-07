package top.bogey.touch_tool.bean.base;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.service.TaskRunnable;

public interface ExecuteAble {
    void execute(TaskRunnable runnable, Pin pin);

    void executeNext(TaskRunnable runnable, Pin pin);

    void calculate(TaskRunnable runnable, Pin pin);
}
