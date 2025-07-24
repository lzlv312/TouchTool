package top.bogey.touch_tool.bean.task;

import java.util.List;

public interface ITaskManager {
    void addTask(Task task);

    void removeTask(String id);

    Task getTask(String id);

    List<Task> getTasks();

    List<Task> getTasks(String tag);

    Task findChildTask(String id);

    /**
     * 获取传入任务id的父任务对象，可用于判断自身是否是传入任务的父任务
     * @param id 任务id
     * @return 父任务对象
     */
    Task getParentTask(String id);
}
