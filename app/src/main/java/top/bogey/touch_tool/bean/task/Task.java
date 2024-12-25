package top.bogey.touch_tool.bean.task;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionCheckResult;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.base.Identity;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;

public class Task extends Identity {
    private final long createTime;

    private List<Task> tasks;
    private List<Action> actions = new ArrayList<>();
    private List<Variable> vars;
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
        if (tasks != null) tasks.forEach(task -> task.parent = this);

        actions = GsonUtil.getAsObject(jsonObject, "actions", TypeToken.getParameterized(ArrayList.class, Action.class).getType(), new ArrayList<>());

        vars = GsonUtil.getAsObject(jsonObject, "vars", TypeToken.getParameterized(ArrayList.class, Variable.class).getType(), null);
        if (vars != null) vars.forEach(var -> var.owner = this);

        tags = GsonUtil.getAsObject(jsonObject, "tags", TypeToken.getParameterized(ArrayList.class, String.class).getType(), null);
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        task.parent = this;
    }

    public void removeTask(String id) {
        if (tasks != null) {
            Task task = getTask(id);
            tasks.remove(task);
            if (tasks.isEmpty()) tasks = null;
        }
    }

    public Task getTask(String id) {
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.getId().equals(id)) {
                    return task;
                }
            }
        }
        return null;
    }

    public List<Task> getTasks(String tag) {
        if (tasks == null) return null;
        return tasks.stream().filter(task -> task.getTags().contains(tag)).collect(Collectors.toList());
    }

    public Task findTask(String taskId) {
        Task task = getTask(taskId);
        if (task != null) return task;
        if (parent != null) return parent.findTask(taskId);
        return null;
    }

    public Task findTaskParent(String taskId) {
        Task task = getTask(taskId);
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

    public void addVar(Variable value) {
        Variable var = getVar(value.getTitle());
        if (var != null) return;
        if (vars == null) {
            vars = new ArrayList<>();
        }
        vars.add(value);
        value.owner = this;
    }

    public void removeVar(String id) {
        if (vars != null) {
            Variable var = getVar(id);
            vars.remove(var);
            if (vars.isEmpty()) vars = null;
        }
    }

    public Variable getVar(String id) {
        if (vars != null) {
            for (Variable var : vars) {
                if (id.equals(var.getId())) return var;
            }
        }
        return null;
    }

    public Variable findVar(String id) {
        Variable var = getVar(id);
        if (var != null) return var;
        if (parent != null) return parent.findVar(id);
        return null;
    }

    public Task findVarParent(String key) {
        Variable var = getVar(key);
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
        actions.stream().filter(Objects::nonNull).forEach(action -> action.check(result, this));
        if (tasks != null) tasks.forEach(task -> task.check(result));
    }

    public void save() {
        if (parent != null) parent.save();
        else Saver.getInstance().saveTask(this);
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

    public void execute(TaskRunnable runnable, StartAction startAction, BooleanResultCallback callback) {
        Task copy = copy();
        Action action = copy.getAction(startAction.getId());
        runnable.pushStack(copy, action);
        callback.onResult(true);
        action.execute(runnable, null);
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
        if (tasks == null) return Collections.emptyList();
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

    public List<Variable> getVars() {
        if (vars == null) return Collections.emptyList();
        return vars;
    }

    public void setVars(List<Variable> vars) {
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
