package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.utils.callback.ResultCallback;

public class SelectActionByCustomActionDialog extends SelectActionDialog {

    public SelectActionByCustomActionDialog(@NonNull Context context, Task task, ResultCallback<Action> callback) {
        super(context, task, callback);
    }

    @Override
    protected GroupType[] getGroupTypes() {
        return new GroupType[]{GroupType.TASK};
    }

    @Override
    protected Map<String, List<Object>> getGroupData(GroupType groupType) {
        Map<String, List<Object>> map = new LinkedHashMap<>();
        if (groupType == GroupType.TASK) {
            // 私有任务
            List<Object> privateTasks = new ArrayList<>();
            for (Task task : task.getTasks()) {
                if (!task.getActions(CustomStartAction.class).isEmpty()) privateTasks.add(task);
            }
            map.put(PRIVATE, privateTasks);

            // 公共任务
            List<Object> publicTasks = new ArrayList<>();
            for (Task task : Saver.getInstance().getTasks()) {
                if (!task.getActions(CustomStartAction.class).isEmpty()) publicTasks.add(task);
            }
            map.put(GLOBAL, publicTasks);

            // 父任务
            Task parent = task.getParent();
            while (parent != null) {
                List<Object> list = new ArrayList<>();
                if (!task.getActions(CustomStartAction.class).isEmpty()) list.add(parent);
                for (Task task : parent.getTasks()) {
                    if (!task.getActions(CustomStartAction.class).isEmpty()) list.add(task);
                }
                if (!list.isEmpty()) map.put(PARENT_PREFIX + parent.getTitle(), list);
                parent = parent.getParent();
            }

            // 子任务
            Queue<Task> queue = new LinkedList<>(task.getTasks());
            while (!queue.isEmpty()) {
                Task poll = queue.poll();
                if (poll == null) continue;
                List<Task> tasks = poll.getTasks();
                if (!tasks.isEmpty()) {
                    map.put(CHILD_PREFIX + poll.getTitle(), new ArrayList<>(tasks));
                    subGroupMap.put(CHILD_PREFIX + poll.getTitle(), poll);
                    queue.addAll(tasks);
                }
            }
        }
        return map;
    }
}
