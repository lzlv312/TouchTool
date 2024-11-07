package top.bogey.touch_tool.bean.task;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionCheckResult;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.base.Identity;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.TaskSaver;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;

public class Task extends Identity {
    private final long createTime;

    private List<Task> tasks;
    private List<Action> actions = new ArrayList<>();
    private Map<String, PinObject> vars;
    private List<String> tags;

    private transient Task parent;
    private transient ExecuteTaskAction startAction = null;

    public Task() {
        super();
        createTime = System.currentTimeMillis();
    }

    public Task(JsonObject jsonObject) {
        super(jsonObject);
        createTime = GsonUtil.getAsLong(jsonObject, "createTime", System.currentTimeMillis());
        tasks = GsonUtil.getAsObject(jsonObject, "tasks", TypeToken.getParameterized(ArrayList.class, Task.class).getType(), null);
        tasks.forEach(task -> task.parent = this);
        actions = GsonUtil.getAsObject(jsonObject, "actions", TypeToken.getParameterized(ArrayList.class, Action.class).getType(), new ArrayList<>());
        vars = GsonUtil.getAsObject(jsonObject, "vars", TypeToken.getParameterized(HashMap.class, String.class, PinBase.class).getType(), null);
        tags = GsonUtil.getAsObject(jsonObject, "tags", TypeToken.getParameterized(ArrayList.class, String.class).getType(), null);
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        task.parent = this;
    }

    public void removeTask(Task task) {
        if (tasks != null) {
            tasks.remove(task);
            if (tasks.isEmpty()) tasks = null;
        }
    }

    public Task getTaskById(String id) {
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.getId().equals(id)) {
                    return task;
                }
            }
        }
        if (parent != null) {
            return parent.getTaskById(id);
        }
        return null;
    }

    public List<Task> getTasksByTag(String tag) {
        if (tasks == null) return null;
        return tasks.stream().filter(task -> task.getTags().contains(tag)).collect(Collectors.toList());
    }

    public Task findTask(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null) return task;
        if (parent != null) return parent.findTask(taskId);
        return null;
    }

    public Task findTaskParent(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null) return this;
        if (parent != null) return parent.findTaskParent(taskId);
        return null;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void removeAction(Action action) {
        actions.remove(action);
    }

    public Action getAction(String id) {
        return actions.stream().filter(action -> action.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Action> getActions(Class<? extends Action> actionClass) {
        return actions.stream().filter(actionClass::isInstance).collect(Collectors.toList());
    }

    public void addVar(String key, PinObject value) {
        if (vars == null) {
            vars = new HashMap<>();
        }
        vars.put(key, value);
    }

    public void tryAddVar(String key, PinObject value) {
        Task task = findVarParent(key);
        if (task == null) return;
        task.addVar(key, value);
    }

    public void removeVar(String key) {
        if (vars != null) {
            vars.remove(key);
            if (vars.isEmpty()) vars = null;
        }
    }

    public PinObject getVar(String key) {
        if (vars != null) {
            return vars.get(key);
        }
        return null;
    }

    public PinObject findVar(String key) {
        if (vars != null) return vars.get(key);
        if (parent != null) return parent.findVar(key);
        return null;
    }

    public Task findVarParent(String key) {
        PinObject var = getVar(key);
        if (var != null) return this;
        if (parent != null) return parent.findVarParent(key);
        return null;
    }

    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public void removeTag(String tag) {
        if (tags != null) {
            tags.remove(tag);
            if (tags.isEmpty()) tags = null;
        }
    }

    public String getTagString() {
        if (tags == null || tags.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String tag : tags) {
            builder.append(tag).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }

    public boolean checkCapturePermission() {
        for (Action action : getActions()) {
            if (action.withCapture()) return false;
        }
        return true;
    }

    public boolean isEnable() {
        for (Action action : getActions(StartAction.class)) {
            if (((StartAction) action).isEnable()) return true;
        }
        return false;
    }

    public void setEnable(boolean enable) {
        for (Action action : getActions(StartAction.class)) {
            ((StartAction) action).setEnable(enable);
        }
        save();
    }

    public void check(ActionCheckResult result) {
        actions.forEach(action -> action.check(result, this));
        if (tasks != null) tasks.forEach(task -> task.check(result));
    }

    public void save() {
        if (parent != null) parent.save();
        else {
            TaskSaver.getInstance().saveTask(this);
        }
    }

    @Override
    public Task copy() {
        return GsonUtil.copy(this, Task.class);
    }

    @Override
    public Task newCopy() {
        Task copy = copy();
        copy.setId(UUID.randomUUID().toString());
        if (copy.tasks != null && !copy.tasks.isEmpty()) {
            copy.tasks.forEach(task -> task.parent = copy);
        }
        return copy;
    }

    public void execute(TaskRunnable runnable, ExecuteTaskAction startAction, Map<String, PinObject> params) {
        this.startAction = startAction;
        for (Action action : getActions(CustomStartAction.class)) {
            ((CustomStartAction) action).setParams(params);
            runnable.pushStack(this, action);
            action.execute(runnable, null);
            break;
        }
    }

    public void executeNext(TaskRunnable runnable, Map<String, PinObject> params) {
        runnable.popStack();
        if (startAction != null) {
            startAction.setParams(params);
        }
    }

    public long getCreateTime() {
        return createTime;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Map<String, PinObject> getVars() {
        return vars;
    }

    public void setVars(Map<String, PinObject> vars) {
        this.vars = vars;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Task getParent() {
        return parent;
    }
}
