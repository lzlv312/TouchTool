package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.ActionMap;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.task.CustomEndAction;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.utils.callback.ResultCallback;

public class SelectActionByPinDialog extends SelectActionDialog {
    private final Pin touchedPin;

    public SelectActionByPinDialog(@NonNull Context context, Task task, Pin touchedPin, ResultCallback<Action> callback) {
        super(context, task, callback);
        this.touchedPin = touchedPin;
        binding.tabBox.removeOnTabSelectedListener(tabListener);
        initGroup();
    }

    @Override
    public void calculateShowData() {
        dataMap.clear();
        if (touchedPin == null) return;
        // 第一部分：预设Action
        Map<String, List<Object>> preset = new LinkedHashMap<>();
        for (ActionMap.ActionGroupType groupType : ActionMap.ActionGroupType.values()) {
            List<Object> types = new ArrayList<>();
            for (ActionType type : ActionMap.getTypes(groupType)) {
                ActionInfo info = ActionInfo.getActionInfo(type);
                if (info == null) continue;
                Action action = info.getAction();
                if (action == null) continue;
                Pin pin = action.findConnectToAblePin(touchedPin);
                if (pin == null) continue;
                types.add(type);
            }
            if (types.isEmpty()) continue;
            preset.put(groupType.getName(), types);
        }
        if (!preset.isEmpty()) dataMap.put(GroupType.PRESET, preset);

        // 第二部分：带CustomStartAction的Task
        Map<String, List<Object>> tasks = new LinkedHashMap<>();

        // 公共任务
        List<Object> publicTasks = new ArrayList<>();
        for (Task task : Saver.getInstance().getTasks()) {
            if (isConnectAbleTask(task)) publicTasks.add(task);
        }
        if (!publicTasks.isEmpty()) tasks.put(getContext().getString(R.string.select_action_group_global), publicTasks);

        // 私有任务
        List<Object> privateTasks = new ArrayList<>();
        for (Task task : task.getTasks()) {
            if (isConnectAbleTask(task)) privateTasks.add(task);
        }
        if (!privateTasks.isEmpty()) tasks.put(getContext().getString(R.string.select_action_group_private), privateTasks);

        // 父任务
        Task parent = task.getParent();
        while (parent != null) {
            List<Object> list = new ArrayList<>();
            if (isConnectAbleTask(parent)) list.add(parent);
            if (!list.isEmpty()) tasks.put(parent.getTitle(), list);
            parent = parent.getParent();
        }
        if (!tasks.isEmpty()) dataMap.put(GroupType.TASK, tasks);

        // 第三部分：变量Variable
        Map<String, List<Object>> vars = new LinkedHashMap<>();

        List<Object> publicVars = new ArrayList<>();
        for (Variable var : Saver.getInstance().getVars()) {
            if (touchedPin.getValue().isInstance(var.getValue())) {
                publicVars.add(var);
            }
        }
        if (!publicVars.isEmpty()) vars.put(getContext().getString(R.string.select_action_group_global), publicVars);

        List<Object> privateVars = new ArrayList<>(task.getVars());
        for (Variable var : task.getVars()) {
            if (touchedPin.getValue().isInstance(var.getValue())) {
                privateVars.add(var);
            }
        }
        if (!privateVars.isEmpty()) vars.put(getContext().getString(R.string.select_action_group_private), privateVars);

        parent = task.getParent();
        while (parent != null) {
            List<Object> list = new ArrayList<>();
            for (Variable var : parent.getVars()) {
                if (touchedPin.getValue().isInstance(var.getValue())) {
                    list.add(var);
                }
            }
            if (!list.isEmpty()) vars.put(parent.getTitle(), list);
            parent = parent.getParent();
        }
        if (!vars.isEmpty()) dataMap.put(GroupType.TASK, vars);
    }

    private boolean isConnectAbleTask(Task task) {
        for (Action action : task.getActions(CustomStartAction.class)) {
            if (action.findConnectToAblePin(touchedPin) != null) return true;
        }
        for (Action action : task.getActions(CustomEndAction.class)) {
            if (action.findConnectToAblePin(touchedPin) != null) return true;
        }
        return false;
    }
}
