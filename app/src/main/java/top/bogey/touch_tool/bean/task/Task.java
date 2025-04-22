package top.bogey.touch_tool.bean.task;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionCheckResult;
import top.bogey.touch_tool.bean.action.start.InnerStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.base.Identity;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;

public class Task extends Identity implements IActionManager, ITaskManager, IVariableManager, ITagManager {
    private final long createTime;

    private final ActionManager actionManager;

    private final TaskManager taskManager;
    private final VariableManager variableManager;

    private final TagManager tagManager;

    private boolean detailLog = false;

    private transient Task parent;
    private transient ExecuteTaskAction startAction = null;

    public Task() {
        super();
        createTime = System.currentTimeMillis();

        actionManager = new ActionManager();

        taskManager = new TaskManager(this);
        variableManager = new VariableManager(this);

        tagManager = new TagManager();
    }

    public Task(JsonObject jsonObject) {
        super(jsonObject);
        createTime = GsonUtil.getAsLong(jsonObject, "createTime", System.currentTimeMillis());

        actionManager = GsonUtil.getAsObject(jsonObject, "actionManager", ActionManager.class, new ActionManager());

        taskManager = GsonUtil.getAsObject(jsonObject, "taskManager", TaskManager.class, new TaskManager(this));
        taskManager.setParent(this);
        variableManager = GsonUtil.getAsObject(jsonObject, "variableManager", VariableManager.class, new VariableManager(this));
        variableManager.setParent(this);

        tagManager = GsonUtil.getAsObject(jsonObject, "tagManager", TagManager.class, new TagManager());

        detailLog = GsonUtil.getAsBoolean(jsonObject, "detailLog", false);
    }

    @Override
    public void addAction(Action action) {
        actionManager.addAction(action);
    }

    @Override
    public void removeAction(String id) {
        actionManager.removeAction(id);
    }

    @Override
    public Action getAction(String id) {
        return actionManager.getAction(id);
    }

    @Override
    public List<Action> getActions() {
        return actionManager.getActions();
    }

    @Override
    public List<Action> getActions(String uid) {
        return actionManager.getActions(uid);
    }

    @Override
    public List<Action> getActions(Class<? extends Action> actionClass) {
        return actionManager.getActions(actionClass);
    }


    @Override
    public void addTask(Task task) {
        taskManager.addTask(task);
    }

    @Override
    public void removeTask(String id) {
        taskManager.removeTask(id);
    }

    @Override
    public Task getTask(String id) {
        return taskManager.getTask(id);
    }

    @Override
    public List<Task> getTasks() {
        return taskManager.getTasks();
    }

    @Override
    public List<Task> getTasks(String tag) {
        return taskManager.getTasks(tag);
    }

    @Override
    public Task findChildTask(String id) {
        return taskManager.findChildTask(id);
    }

    @Override
    public Task getParentTask(String id) {
        return taskManager.getParentTask(id);
    }


    @Override
    public boolean addVariable(Variable variable) {
        return variableManager.addVariable(variable);
    }

    @Override
    public void removeVariable(String id) {
        variableManager.removeVariable(id);
    }

    @Override
    public Variable getVariable(String id) {
        return variableManager.getVariable(id);
    }

    @Override
    public List<Variable> getVariables() {
        return variableManager.getVariables();
    }

    @Override
    public Variable findVariable(String id) {
        return variableManager.findVariable(id);
    }

    @Override
    public void addTag(String tag) {
        tagManager.addTag(tag);
    }

    @Override
    public void removeTag(String tag) {
        tagManager.removeTag(tag);
    }

    @Override
    public List<String> getTags() {
        return tagManager.getTags();
    }

    @Override
    public void setTags(List<String> tags) {
        tagManager.setTags(tags);
    }

    @Override
    public String getTagString() {
        return tagManager.getTagString();
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
        getActions().stream().filter(Objects::nonNull).forEach(action -> action.check(result, this));
        if (getTasks() != null) getTasks().forEach(task -> task.check(result));
    }

    public void save() {
        if (parent != null) parent.save();
        else Saver.getInstance().saveTask(this);
    }

    @Override
    public Task copy() {
        Task copy = GsonUtil.copy(this, Task.class);
        copy.parent = parent;
        taskManager.setParent(copy);
        variableManager.setParent(copy);
        return copy;
    }

    @Override
    public Task newCopy() {
        Task copy = copy();
        copy.setId(UUID.randomUUID().toString());
        copy.parent = null;
        return copy;
    }

    public void execute(TaskRunnable runnable, StartAction startAction, BooleanResultCallback callback) {
        Task copy = copy();
        Action action = startAction;
        if (!(startAction instanceof InnerStartAction)) {
            action = copy.getAction(startAction.getId());
        }
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

    public Task getParent() {
        return parent;
    }

    void setParent(Task parent) {
        this.parent = parent;
    }

    public boolean isDetailLog() {
        return detailLog;
    }

    public void setDetailLog(boolean detailLog) {
        this.detailLog = detailLog;
    }

    public static class TaskDeserialize implements JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Task(json.getAsJsonObject());
        }
    }
}
