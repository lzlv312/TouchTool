package top.bogey.touch_tool.bean.task;

import java.util.List;

public interface ITaskManager {
    void addTask(Task task);

    void removeTask(String id);

    Task getTask(String id);

    List<Task> getTasks();

    List<Task> getTasks(String tag);

    Task findChildTask(String id);

    Task getParentTask(String id);
}
