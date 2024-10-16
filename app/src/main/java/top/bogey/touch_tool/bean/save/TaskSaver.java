package top.bogey.touch_tool.bean.save;

import android.os.Handler;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.GsonUtil;

public class TaskSaver {
    private static TaskSaver instance;
    private static final String EMPTY_TAG = MainApplication.getInstance().getString(R.string.tag_empty);

    public static boolean matchTag(String tag, List<String> tags) {
        boolean emptyTags = tags == null || tags.isEmpty();
        if (Objects.equals(tag, EMPTY_TAG) && emptyTags) {
            return true;
        }
        if (emptyTags) return false;
        return tags.contains(tag);
    }

    private static final String LOG_DIR = MainApplication.getInstance().getCacheDir().getAbsolutePath() + "/log";

    public static TaskSaver getInstance() {
        synchronized (TaskSaver.class) {
            if (instance == null) {
                instance = new TaskSaver();
            }
        }
        return instance;
    }

    private final Handler handler;

    private final Map<String, TaskSave> taskSaves = new HashMap<>();
    private final MMKV taskMMKV = MMKV.mmkvWithID("TASK_DB", MMKV.SINGLE_PROCESS_MODE);
    private final Set<TaskSaveListener> listeners = new HashSet<>();

    private final MMKV tagMMKV = MMKV.mmkvWithID("TAG_DB", MMKV.SINGLE_PROCESS_MODE);

    private final MMKV commonMMKV = MMKV.mmkvWithID("COMMON_DB", MMKV.SINGLE_PROCESS_MODE);
    private final Map<String, Logger> loggers = new HashMap<>();


    private TaskSaver() {
        handler = new Handler();
        recycle();
        loadTasks();
    }

    private void recycle() {
        taskSaves.forEach((k, v) -> v.recycle());
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
        Pattern pattern = AppUtil.getPattern(title);
        for (Map.Entry<String, TaskSave> entry : taskSaves.entrySet()) {
            TaskSave v = entry.getValue();
            Task task = v.getTask();
            if (pattern != null) {
                if (pattern.matcher(task.getTitle()).find()) {
                    tasks.add(task);
                }
            } else {
                if (task.getFullDescription().contains(title)) {
                    tasks.add(task);
                }
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

    public Task getTask(String id) {
        TaskSave taskSave = taskSaves.get(id);
        if (taskSave == null) return null;
        return taskSave.getTask();
    }

    public Task getOriginTask(String id) {
        TaskSave taskSave = taskSaves.get(id);
        if (taskSave == null) return null;
        return taskSave.getOriginTask();
    }

    public void saveTask(Task task) {
        TaskSave taskSave = taskSaves.get(task.getId());
        if (taskSave == null) {
            taskSaves.put(task.getId(), new TaskSave(taskMMKV, task));
            listeners.stream().filter(Objects::nonNull).forEach(v -> v.onCreate(task));
        } else {
            taskSave.setTask(task);
            listeners.stream().filter(Objects::nonNull).forEach(v -> v.onUpdate(task));
        }
    }

    public void removeTask(String id) {
        TaskSave taskSave = taskSaves.remove(id);
        if (taskSave == null) return;
        Task task = taskSave.getTask();
        listeners.stream().filter(Objects::nonNull).forEach(v -> v.onRemove(task));
        taskSave.remove();
        Logger logger = loggers.get(id);
        if (logger == null) return;
        logger.destroy();
    }

    public void addListener(TaskSaveListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TaskSaveListener listener) {
        listeners.remove(listener);
    }


    public PinObject getSave(String key) {
        return (PinObject) GsonUtil.getAsObject(commonMMKV.decodeString(key), PinBase.class, null);
    }

    public void setSave(String key, PinObject value) {
        commonMMKV.encode(key, GsonUtil.toJson(value));
    }

    public String getLog(String key) {
        Logger logger = loggers.computeIfAbsent(key, k -> new Logger(LOG_DIR, key));
        return logger.getLog();
    }

    public void addLog(String key, String content) {
        Logger logger = loggers.computeIfAbsent(key, k -> new Logger(LOG_DIR, key));
        logger.addLog(content);
    }

    public void clearLog(String key) {
        Logger logger = loggers.get(key);
        if (logger == null) return;
        logger.clearLog();
    }

    public void addTag(String tag) {
        tagMMKV.encode(tag, true);
    }

    public void removeTag(String tag) {
        tagMMKV.remove(tag);
        taskSaves.forEach((id, taskSave) -> {
            Task task = taskSave.getTask();
            if (matchTag(tag, task.getTags())) {
                task.removeTag(tag);
                task.save();
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

}
