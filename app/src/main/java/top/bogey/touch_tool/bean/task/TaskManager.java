package top.bogey.touch_tool.bean.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private transient Task parent;
    private final List<Task> tasks = new ArrayList<>();

    public TaskManager(Task parent) {
        this.parent = parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
        for (Task t : tasks) {
            t.setParent(parent);
        }
    }

    @Override
    public void addTask(Task task) {
        tasks.add(task);
        task.setParent(parent);
    }

    @Override
    public void removeTask(String id) {
        Task task = getTask(id);
        if (task == null) return;
        tasks.remove(task);
    }

    @Override
    public Task getTask(String id) {
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public List<Task> getTasks(String tag) {
        return tasks.stream().filter(task -> task.getTags().contains(tag)).collect(Collectors.toList());
    }

    @Override
    public Task findChildTask(String id) {
        Task task = getTask(id);
        if (task != null) return task;
        for (Task t : tasks) {
            Task target = t.findChildTask(id);
            if (target != null) return target;
        }
        return null;
    }

    @Override
    public Task getParentTask(String id) {
        // 检查自身是不是他的父任务
        Task task = getTask(id);
        if (task != null) return parent;
        // 检查父任务是不是它的父任务
        task = parent.getParent();
        if (task != null) return task.getParentTask(id);
        return null;
    }
}
