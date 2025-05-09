package top.bogey.touch_tool.bean.save;

import top.bogey.touch_tool.bean.task.Task;

public interface TaskSaveListener {
    void onCreate(Task task);

    void onUpdate(Task task);

    void onRemove(Task task);
}
