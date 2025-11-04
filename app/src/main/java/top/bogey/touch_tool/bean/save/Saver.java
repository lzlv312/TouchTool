package top.bogey.touch_tool.bean.save;

import android.os.Handler;
import android.os.Looper;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.other.Usage;
import top.bogey.touch_tool.bean.save.log.LogInfo;
import top.bogey.touch_tool.bean.save.log.LogSave;
import top.bogey.touch_tool.bean.save.log.LogSaveListener;
import top.bogey.touch_tool.bean.save.task.TaskSave;
import top.bogey.touch_tool.bean.save.task.TaskSaveListener;
import top.bogey.touch_tool.bean.save.variable.VariableSave;
import top.bogey.touch_tool.bean.save.variable.VariableSaveListener;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.AppUtil;

public class Saver {
    private static Saver instance;

    public static Saver getInstance() {
        synchronized (Saver.class) {
            if (instance == null) {
                instance = new Saver();
            }
        }
        return instance;
    }

    private static final String EMPTY_TAG = MainApplication.getInstance().getString(R.string.tag_empty);

    public static boolean matchTag(String tag, List<String> tags) {
        boolean emptyTags = tags == null || tags.isEmpty();
        if (Objects.equals(tag, EMPTY_TAG) && emptyTags) {
            return true;
        }
        if (emptyTags) return false;
        return tags.contains(tag);
    }

    private final Handler handler;

    private final Map<String, TaskSave> taskSaves = new HashMap<>();
    private final MMKV taskMMKV = MMKV.mmkvWithID("TASK_DB", MMKV.SINGLE_PROCESS_MODE);
    private final Set<TaskSaveListener> taskListeners = new HashSet<>();

    private final Map<String, VariableSave> variableSaves = new HashMap<>();
    private final MMKV variableMMKV = MMKV.mmkvWithID("VARIABLE_DB", MMKV.SINGLE_PROCESS_MODE);
    private final Set<VariableSaveListener> variableListeners = new HashSet<>();

    private final MMKV tagMMKV = MMKV.mmkvWithID("TAG_DB", MMKV.SINGLE_PROCESS_MODE);
    private final MMKV searchHistoryMMKV = MMKV.mmkvWithID("SEARCH_HISTORY_DB", MMKV.SINGLE_PROCESS_MODE);

    private final static String LOG_DIR = MainApplication.getInstance().getCacheDir().getAbsolutePath() + "/" + AppUtil.LOG_DIR_NAME;
    private final Map<String, LogSave> loggers = new HashMap<>();
    private final Set<LogSaveListener> logListeners = new HashSet<>();


    private Saver() {
        handler = new Handler(Looper.getMainLooper());
        recycle();
        loadTasks();
        loadVars();
    }

    private void recycle() {
        taskSaves.forEach((k, v) -> v.recycle());
        variableSaves.forEach((k, v) -> v.recycle());
        new HashMap<>(loggers).forEach((k, v) -> {
            if (v.recycle()) {
                loggers.remove(k);
            }
        });
        handler.postDelayed(this::recycle, 5 * 60 * 1000);
    }

    private void loadTasks() {
        String[] keys = taskMMKV.allKeys();
        if (keys == null) return;

        for (String key : keys) {
            TaskSave taskSave = new TaskSave(taskMMKV, key);
            if (taskSave.getTask() == null) continue;
            taskSaves.put(key, taskSave);
        }
    }

    private void loadVars() {
        String[] keys = variableMMKV.allKeys();
        if (keys == null) return;

        for (String key : keys) {
            VariableSave variableSave = new VariableSave(variableMMKV, key);
            if (variableSave.getVar() == null) continue;
            variableSaves.put(key, variableSave);
        }
    }

    // ====================================================================================================================
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        taskSaves.forEach((k, v) -> tasks.add(v.getTask()));
        return tasks;
    }

    public List<Task> getTasks(String tag) {
        List<Task> tasks = new ArrayList<>();
        taskSaves.forEach((k, v) -> {
            Task task = v.getTask();
            if (matchTag(tag, task.getTags())) {
                tasks.add(task);
            }
        });
        return tasks;
    }

    public List<Task> getTasks(Class<? extends Action> actionClass) {
        List<Task> tasks = new ArrayList<>();
        taskSaves.forEach((k, v) -> {
            Task task = v.getTask();
            List<Action> actions = task.getActions(actionClass);
            if (actions != null && !actions.isEmpty()) {
                tasks.add(task);
            }
        });
        return tasks;
    }

    public List<Task> searchTasks(String title) {
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<String, TaskSave> entry : taskSaves.entrySet()) {
            TaskSave v = entry.getValue();
            Task task = v.getTask();
            if (AppUtil.isStringContains(task.getFullDescription(), title)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<String> getTaskTags() {
        Set<String> tags = new HashSet<>();
        boolean emptyTag = false;
        for (Map.Entry<String, TaskSave> entry : taskSaves.entrySet()) {
            TaskSave v = entry.getValue();
            Task task = v.getTask();
            if (task.getTags() != null && !task.getTags().isEmpty()) {
                tags.addAll(task.getTags());
            } else {
                emptyTag = true;
            }
        }
        List<String> list = new ArrayList<>(tags);
        AppUtil.chineseSort(list, tag -> tag);
        if (emptyTag || taskSaves.isEmpty()) list.add(EMPTY_TAG);
        return list;
    }

    public Task getTask(String taskId) {
        TaskSave taskSave = taskSaves.get(taskId);
        if (taskSave == null) return null;
        return taskSave.getTask();
    }

    public Task downFindTask(Task context, String taskId) {
        if (context == null) return getTask(taskId);
        Task task = context.downFindTask(taskId);
        if (task == null) task = getTask(taskId);
        return task;
    }

    public Task upFindTask(Task context, String taskId) {
        if (context == null) return getTask(taskId);
        Task task = context.upFindTask(taskId);
        if (task == null) task = getTask(taskId);
        return task;
    }

    public Task getOriginTask(String taskId) {
        TaskSave taskSave = taskSaves.get(taskId);
        if (taskSave == null) return null;
        return taskSave.getOriginTask();
    }

    public void saveTask(Task task) {
        TaskSave taskSave = taskSaves.get(task.getId());
        if (taskSave == null) {
            taskSaves.put(task.getId(), new TaskSave(taskMMKV, task));
            taskListeners.stream().filter(Objects::nonNull).forEach(v -> v.onCreate(task));
        } else {
            taskSave.setTask(task);
            taskListeners.stream().filter(Objects::nonNull).forEach(v -> v.onUpdate(task));
        }

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.replaceAlarm(task);
        }
    }

    public void removeTask(String id) {
        TaskSave taskSave = taskSaves.remove(id);
        if (taskSave == null) return;
        Task task = taskSave.getTask();
        task.setEnable(false);
        taskListeners.stream().filter(Objects::nonNull).forEach(v -> v.onRemove(task));
        taskSave.remove();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.replaceAlarm(task);
        }

        LogSave logSave = loggers.get(id);
        if (logSave == null) return;
        logSave.destroy();
    }

    public List<Usage> getTaskUses(String id) {
        List<Usage> usages = new ArrayList<>();
        taskSaves.forEach((k, v) -> {
            Task task = v.getTask();
            usages.addAll(task.getTaskUses(id));
        });
        return usages;
    }

    public void addListener(TaskSaveListener listener) {
        taskListeners.add(listener);
    }

    public void removeListener(TaskSaveListener listener) {
        taskListeners.remove(listener);
    }

    // ====================================================================================================================

    public List<Variable> getVars() {
        List<Variable> vars = new ArrayList<>();
        variableSaves.forEach((k, v) -> vars.add(v.getVar()));
        return vars;
    }

    public Variable getVar(String id) {
        VariableSave varSave = variableSaves.get(id);
        if (varSave == null) return null;
        return varSave.getVar();
    }

    public Variable getOriginVar(String id) {
        VariableSave varSave = variableSaves.get(id);
        if (varSave == null) return null;
        return varSave.getOriginVar();
    }

    public void saveVar(Variable var) {
        VariableSave varSave = variableSaves.get(var.getId());
        if (varSave == null) {
            variableSaves.put(var.getId(), new VariableSave(variableMMKV, var));
            variableListeners.stream().filter(Objects::nonNull).forEach(v -> v.onCreate(var));
        } else {
            varSave.setVar(var);
            variableListeners.stream().filter(Objects::nonNull).forEach(v -> v.onUpdate(var));
        }
    }

    public void removeVar(String id) {
        VariableSave varSave = variableSaves.remove(id);
        if (varSave == null) return;
        Variable var = varSave.getVar();
        variableListeners.stream().filter(Objects::nonNull).forEach(v -> v.onRemove(var));
        varSave.remove();
    }

    public List<Usage> getVarUses(String id) {
        List<Usage> usages = new ArrayList<>();
        taskSaves.forEach((k, v) -> {
            Task task = v.getTask();
            usages.addAll(task.getVariableUses(id));
        });
        return usages;
    }

    public void addListener(VariableSaveListener listener) {
        variableListeners.add(listener);
    }

    public void removeListener(VariableSaveListener listener) {
        variableListeners.remove(listener);
    }

    // ====================================================================================================================
    public LogSave getLogSave(String key) {
        return loggers.computeIfAbsent(key, k -> new LogSave(key, LOG_DIR));
    }

    public void addLog(String key, LogInfo log, boolean autoUid) {
        LogSave logSave = getLogSave(key);
        logSave.addLog(log, autoUid);
        if (autoUid) logListeners.stream().filter(Objects::nonNull).forEach(v -> v.onNewLog(logSave, log));
    }

    public void clearLog(String key) {
        LogSave logSave = loggers.get(key);
        if (logSave == null) return;
        logSave.clearLog();
    }

    public void addListener(LogSaveListener listener) {
        logListeners.add(listener);
    }

    public void removeListener(LogSaveListener listener) {
        logListeners.remove(listener);
    }

    // ====================================================================================================================
    public void addTag(String tag) {
        tagMMKV.encode(tag, true);
    }

    public void removeTag(String tag) {
        tagMMKV.remove(tag);
        taskSaves.forEach((id, taskSave) -> {
            Task task = taskSave.getTask();
            task.removeInnerTag(tag);
            task.save();
        });

        variableSaves.forEach((id, variableSave) -> {
            Variable var = variableSave.getVar();
            if (matchTag(tag, var.getTags())) {
                var.removeTag(tag);
                var.save();
            }
        });
    }

    public List<String> getAllTags() {
        List<String> list = new ArrayList<>();
        String[] keys = tagMMKV.allKeys();
        if (keys == null) return list;
        for (String key : keys) {
            if (tagMMKV.decodeBool(key)) {
                list.add(key);
            }
        }
        AppUtil.chineseSort(list, tag -> tag);
        return list;
    }

    // ====================================================================================================================
    public void addSearchHistory(String history) {
        searchHistoryMMKV.encode(history, true);
    }

    public void removeSearchHistory(String history) {
        searchHistoryMMKV.remove(history);
    }

    public void cleanSearchHistory() {
        searchHistoryMMKV.clearAll();
    }

    public List<String> getSearchHistory() {
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        String[] keys = searchHistoryMMKV.allKeys();
        if (keys == null) return list;
        for (String key : keys) {
            if (searchHistoryMMKV.decodeBool(key) && !set.contains(key)) {
                list.add(key);
                set.add(key);
            }
            if (list.size() >= 10) break;
        }
        return list;
    }
}
