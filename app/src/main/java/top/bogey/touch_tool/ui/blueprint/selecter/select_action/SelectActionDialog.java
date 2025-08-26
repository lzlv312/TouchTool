package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionMap;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.ITagManager;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.databinding.DialogSelectActionBinding;
import top.bogey.touch_tool.databinding.WidgetSettingSelectButton2Binding;
import top.bogey.touch_tool.databinding.WidgetSettingSelectButtonBinding;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.custom.EditTaskDialog;
import top.bogey.touch_tool.ui.custom.EditVariableDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class SelectActionDialog extends BottomSheetDialog {
    protected final String GLOBAL = getContext().getString(R.string.select_action_group_global);
    protected final String PRIVATE = getContext().getString(R.string.select_action_group_private);
    protected final static String PARENT_PREFIX = "👨";
    protected final static String CHILD_PREFIX = "👶";
    protected final static String TAG_PREFIX = "🔗";
    public final static String GLOBAL_FLAG = "🌍 ";

    public static Object copyObject;

    protected final DialogSelectActionBinding binding;
    protected final Task task;
    protected SelectActionItemRecyclerViewAdapter adapter;

    protected GroupType groupType = GroupType.PRESET;
    protected Map<String, List<Object>> dataMap = new HashMap<>();
    protected String subGroupTag;
    protected Map<String, Object> subGroupMap = new HashMap<>();
    protected List<Object> dataList = new ArrayList<>();

    public SelectActionDialog(@NonNull Context context, Task task, ResultCallback<Action> callback) {
        super(context);
        this.task = task;

        binding = DialogSelectActionBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        BottomSheetBehavior<FrameLayout> behavior = getBehavior();
        behavior.setDraggable(false);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        initAdapter(callback);

        binding.group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                SettingSaver.getInstance().setLastGroup(((MaterialButton) view).getText().toString());
                groupType = (GroupType) view.getTag();
                binding.addButton.setTag(groupType);
                dataMap = getGroupData(groupType);
                refreshSubGroup(dataMap);
                setCopyObject(copyObject);
            }
        });

        binding.subGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                SettingSaver.getInstance().setLastSubGroup(((MaterialButton) view).getText().toString());
                subGroupTag = (String) view.getTag();
                dataList = dataMap.get(subGroupTag);
                adapter.setData(dataList, groupType != GroupType.PRESET);
                binding.addButton.setVisibility(Objects.equals(subGroupTag, GLOBAL) || Objects.equals(subGroupTag, PRIVATE) ? View.VISIBLE : View.GONE);
            }
        });

        String[] groupName = getContext().getResources().getStringArray(R.array.group_type);
        String lastGroup = SettingSaver.getInstance().getLastGroup();
        int index = 0;
        GroupType[] groupTypes = getGroupTypes();
        for (int i = 0; i < groupTypes.length; i++) {
            GroupType groupType = groupTypes[i];
            WidgetSettingSelectButtonBinding buttonBinding = WidgetSettingSelectButtonBinding.inflate(LayoutInflater.from(getContext()), binding.group, true);
            buttonBinding.getRoot().setId(View.generateViewId());
            buttonBinding.getRoot().setText(groupName[groupType.ordinal()]);
            buttonBinding.getRoot().setTag(groupType);
            if (lastGroup.equals(groupName[groupType.ordinal()])) index = i;
        }
        if (binding.group.getChildCount() > index) binding.group.check(binding.group.getChildAt(index).getId());

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });

        binding.pasteButton.setOnClickListener(v -> {
            if (copyObject == null) return;
            if (groupType == GroupType.TASK && copyObject instanceof Task copy) {
                Object o = subGroupMap.get(subGroupTag);
                if (o instanceof Task parent) {
                    parent.addTask(copy);
                } else if (o instanceof String tag) {
                    if (GLOBAL.equals(tag)) {
                        Saver.getInstance().saveTask(copy);
                    }
                } else if (o == null) {
                    copy.getTags().clear();
                    copy.addTag(subGroupTag.replace(TAG_PREFIX, ""));
                    Saver.getInstance().saveTask(copy);
                }
            } else if (groupType == GroupType.VARIABLE && copyObject instanceof Variable copy) {
                Object o = subGroupMap.get(subGroupTag);
                if (o instanceof Task parent) {
                    parent.addVariable(copy);
                } else if (o instanceof String tag) {
                    if (GLOBAL.equals(tag)) {
                        Saver.getInstance().saveVar(copy);
                    }
                } else if (o == null) {
                    copy.getTags().clear();
                    copy.addTag(subGroupTag.replace(TAG_PREFIX, ""));
                    Saver.getInstance().saveVar(copy);
                }
            }
            adapter.addData(copyObject);
            setCopyObject(null);
        });

        binding.searchButton.setOnClickListener(v -> {
            if (binding.searchBox.getVisibility() == View.VISIBLE) {
                binding.searchBox.setVisibility(View.GONE);
                binding.searchEdit.setText("");
            } else {
                binding.searchBox.setVisibility(View.VISIBLE);
                binding.searchEdit.requestFocus();
            }
        });

        binding.addButton.setOnClickListener(v -> {
            GroupType groupType = (GroupType) binding.addButton.getTag();
            switch (groupType) {
                case TASK -> showNewTaskDialog();
                case VARIABLE -> showNewVariableDialog();
            }
        });

        setCopyObject(copyObject);
        MainActivity activity = MainApplication.getInstance().getActivity();
        View decorView = activity.getWindow().getDecorView();
        int width = decorView.getWidth();
        int height = decorView.getHeight();

        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) binding.actionsBox.getLayoutManager();
        boolean portrait = DisplayUtil.isPortrait(context);
        if (layoutManager != null) {
            if (portrait) {
                DisplayUtil.setViewHeight(binding.getRoot(), (int) (height * 0.7f));
                DisplayUtil.setViewWidth(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT);
                layoutManager.setSpanCount(2);
            } else {
                DisplayUtil.setViewHeight(binding.getRoot(), (int) (height * 0.8f));
                behavior.setMaxWidth(width);
                layoutManager.setSpanCount(4);
            }
        }
    }

    protected void initAdapter(ResultCallback<Action> callback) {
        adapter = new SelectActionItemRecyclerViewAdapter(this, callback);
        binding.actionsBox.setAdapter(adapter);
    }

    private void showNewVariableDialog() {
        Variable variable = new Variable(new PinString());
        EditVariableDialog dialog = new EditVariableDialog(getContext(), variable);
        dialog.setTitle(R.string.variable_add);
        dialog.setCallback(result -> {
            if (result) {
                View view = binding.subGroup.findViewById(binding.subGroup.getCheckedButtonId());
                String tag = (String) view.getTag();
                if (PRIVATE.equals(tag)) task.addVariable(variable);
                variable.save();
                dataList.add(0, variable);
                adapter.notifyItemInserted(0);
            }
        });
        dialog.show();
    }

    private void showNewTaskDialog() {
        Task newTask = new Task();
        EditTaskDialog dialog = new EditTaskDialog(getContext(), newTask);
        dialog.setTitle(R.string.task_add);
        dialog.setCallback(result -> {
            if (result) {
                View view = binding.subGroup.findViewById(binding.subGroup.getCheckedButtonId());
                String tag = (String) view.getTag();
                if (PRIVATE.equals(tag)) task.addTask(newTask);
                newTask.save();
                dataList.add(0, newTask);
                adapter.notifyItemInserted(0);
            }
        });
        dialog.show();
    }

    private Map<String, List<Object>> calculateTagGroup(Map<String, List<Object>> dataMap) {
        Map<String, List<Object>> map = new HashMap<>();
        dataMap.forEach((key, value) -> {
            if (GLOBAL.equals(key) || PRIVATE.equals(key)) {
                for (Object o : value) {
                    if (o instanceof ITagManager) {
                        List<String> tags = ((ITagManager) o).getTags();
                        for (String tag : tags) {
                            List<Object> objects = map.computeIfAbsent(tag, k -> new ArrayList<>());
                            objects.add(o);
                        }
                    }
                }
            }
        });

        ArrayList<String> keys = new ArrayList<>(map.keySet());
        AppUtil.chineseSort(keys, tag -> tag);
        LinkedHashMap<String, List<Object>> linkedHashMap = new LinkedHashMap<>();
        for (String key : keys) {
            List<Object> objects = map.get(key);
            linkedHashMap.put(TAG_PREFIX + key, objects);
        }
        return linkedHashMap;
    }

    protected void refreshSubGroup(Map<String, List<Object>> dataMap) {
        Map<String, List<Object>> tagGroup = calculateTagGroup(dataMap);
        dataMap.putAll(tagGroup);
        refreshSubGroup(dataMap.keySet().toArray(new String[0]));
    }

    private void refreshSubGroup(String[] chips) {
        binding.subGroup.clearChecked();
        binding.subGroup.removeAllViews();
        String subGroup = SettingSaver.getInstance().getLastSubGroup();
        int index = 0;
        for (int i = 0; i < chips.length; i++) {
            String s = chips[i];
            WidgetSettingSelectButton2Binding buttonBinding = WidgetSettingSelectButton2Binding.inflate(LayoutInflater.from(getContext()), binding.subGroup, true);
            MaterialButton button = buttonBinding.getRoot();
            button.setId(View.generateViewId());
            button.setText(s);
            button.setTag(s);
            if (subGroup.equals(s)) index = i;
        }
        if (chips.length > 0) binding.subGroup.check(binding.subGroup.getChildAt(index).getId());
    }

    protected GroupType[] getGroupTypes() {
        return new GroupType[]{GroupType.PRESET, GroupType.TASK, GroupType.VARIABLE};
    }

    protected void deleteSameObject(Object object) {
        dataMap.forEach((key, value) -> {
            for (int i = value.size() - 1; i >= 0; i--) {
                Object o = value.get(i);
                if (o.equals(object)) {
                    value.remove(i);
                }
            }
        });
    }

    protected Map<String, List<Object>> getGroupData(GroupType groupType) {
        Map<String, List<Object>> map = new LinkedHashMap<>();
        subGroupMap.clear();
        switch (groupType) {
            case PRESET -> {
                for (ActionMap.ActionGroupType actionGroupType : ActionMap.ActionGroupType.values()) {
                    List<Object> types = new ArrayList<>(ActionMap.getTypes(actionGroupType));
                    map.put(actionGroupType.getName(), types);
                    subGroupMap.put(actionGroupType.getName(), actionGroupType.getName());
                }
            }
            case TASK -> {
                // 私有任务
                List<Object> privateTasks = new ArrayList<>(task.getTasks());
                map.put(PRIVATE, privateTasks);
                subGroupMap.put(PRIVATE, task);

                // 公共任务
                List<Object> publicTasks = new ArrayList<>(Saver.getInstance().getTasks());
                map.put(GLOBAL, publicTasks);
                subGroupMap.put(GLOBAL, GLOBAL);

                // 父任务
                Task parent = task.getParent();
                while (parent != null) {
                    List<Object> list = new ArrayList<>(parent.getTasks());
                    if (!list.isEmpty()) {
                        map.put(PARENT_PREFIX + parent.getTitle(), list);
                        subGroupMap.put(PARENT_PREFIX + parent.getTitle(), parent);
                    }
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
            case VARIABLE -> {
                // 私有变量
                List<Object> privateVars = new ArrayList<>(task.getVariables());
                map.put(PRIVATE, privateVars);
                subGroupMap.put(PRIVATE, task);

                // 全局变量
                List<Object> publicVars = new ArrayList<>(Saver.getInstance().getVars());
                map.put(GLOBAL, publicVars);
                subGroupMap.put(GLOBAL, GLOBAL);

                // 父级变量
                Task parent = task.getParent();
                while (parent != null) {
                    List<Object> list = new ArrayList<>(parent.getVariables());
                    if (!list.isEmpty()) {
                        map.put(PARENT_PREFIX + parent.getTitle(), list);
                        subGroupMap.put(PARENT_PREFIX + parent.getTitle(), parent);
                    }
                    parent = parent.getParent();
                }

                // 子级变量
                Queue<Task> queue = new LinkedList<>(task.getTasks());
                while (!queue.isEmpty()) {
                    Task poll = queue.poll();
                    if (poll == null) continue;
                    List<Object> list = new ArrayList<>(poll.getVariables());
                    if (!list.isEmpty()) {
                        map.put(CHILD_PREFIX + poll.getTitle(), list);
                        subGroupMap.put(CHILD_PREFIX + poll.getTitle(), poll);
                    }
                    List<Task> tasks = poll.getTasks();
                    queue.addAll(tasks);
                }
            }
        }
        return map;
    }

    public void search() {
        if (adapter == null) return;
        Editable text = binding.searchEdit.getText();
        if (text == null || text.length() == 0) {
            dataMap = getGroupData(groupType);
            refreshSubGroup(dataMap);
        } else {
            List<Object> data = new ArrayList<>();
            for (Map.Entry<String, List<Object>> entry : dataMap.entrySet()) {
                List<Object> list = entry.getValue();
                for (Object object : list) {
                    String name = SelectActionItemRecyclerViewAdapter.getObjectTitle(object);
                    if (AppUtil.isStringContains(name, text.toString())) {
                        data.add(object);
                    }
                }
            }
            dataMap = new HashMap<>();
            dataMap.put(text.toString(), data);
            refreshSubGroup(dataMap);
        }
    }

    public void setCopyObject(Object object) {
        copyObject = object;

        binding.pasteButton.setVisibility(View.GONE);
        if (groupType == GroupType.TASK && object instanceof Task) {
            binding.pasteButton.setVisibility(View.VISIBLE);
        }
        if (groupType == GroupType.VARIABLE && object instanceof Variable) {
            binding.pasteButton.setVisibility(View.VISIBLE);
        }
    }

    protected enum GroupType {
        PRESET, TASK, VARIABLE
    }
}
