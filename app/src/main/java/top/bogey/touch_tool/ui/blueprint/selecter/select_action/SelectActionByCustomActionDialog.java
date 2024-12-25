package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.utils.callback.ResultCallback;

public class SelectActionByCustomActionDialog extends SelectActionDialog {

    public SelectActionByCustomActionDialog(@NonNull Context context, Task task, ResultCallback<Action> callback) {
        super(context, task, callback);
        binding.tabBox.removeOnTabSelectedListener(tabListener);
    }

    @Override
    public void calculateShowData() {
        dataMap.clear();
        // 带CustomStartAction的Task
        Map<String, List<Object>> tasks = new LinkedHashMap<>();

        // 公共任务
        List<Object> publicTasks = new ArrayList<>();
        for (Task task : Saver.getInstance().getTasks()) {
            if (task.getActions(CustomStartAction.class).isEmpty()) continue;
            publicTasks.add(task);
        }
        if (!publicTasks.isEmpty()) tasks.put(getContext().getString(R.string.select_action_group_global), publicTasks);

        // 私有任务
        List<Object> privateTasks = new ArrayList<>();
        for (Task task : task.getTasks()) {
            if (task.getActions(CustomStartAction.class).isEmpty()) continue;
            privateTasks.add(task);
        }
        if (!privateTasks.isEmpty()) tasks.put(getContext().getString(R.string.select_action_group_private), privateTasks);

        // 父任务
        Task parent = task.getParent();
        while (parent != null) {
            List<Object> list = new ArrayList<>();
            for (Task task : parent.getTasks()) {
                if (task.getActions(CustomStartAction.class).isEmpty()) continue;
                list.add(task);
            }
            if (!list.isEmpty()) tasks.put(parent.getTitle(), list);
            parent = parent.getParent();
        }
        if (!tasks.isEmpty()) dataMap.put(GroupType.TASK, tasks);
    }
}
