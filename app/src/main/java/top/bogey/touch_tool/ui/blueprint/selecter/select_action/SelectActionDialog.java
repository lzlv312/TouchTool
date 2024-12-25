package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.databinding.DialogSelectActionBinding;
import top.bogey.touch_tool.databinding.WidgetSettingSelectButtonBinding;
import top.bogey.touch_tool.ui.custom.EditTaskDialog;
import top.bogey.touch_tool.ui.custom.EditVariableDialog;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class SelectActionDialog extends BottomSheetDialog {
    private final static int KEY = R.string.title;
    private final static int VALUE = R.string.des;

    protected final DialogSelectActionBinding binding;
    private final SelectActionPageAdapter adapter;
    protected final Map<GroupType, Map<String, List<Object>>> dataMap = new LinkedHashMap<>();
    protected final Task task;

    protected final TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String GLOBAL = getContext().getString(R.string.select_action_group_global);
            String PRIVATE = getContext().getString(R.string.select_action_group_private);

            int buttonId = binding.group.getCheckedButtonId();
            View view = binding.group.findViewById(buttonId);
            GroupType groupType = (GroupType) view.getTag(KEY);
            if (groupType != GroupType.PRESET && (Objects.equals(tab.getText(), GLOBAL) || Objects.equals(tab.getText(), PRIVATE))) {
                binding.addButton.setVisibility(View.VISIBLE);
            } else {
                binding.addButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    public SelectActionDialog(@NonNull Context context, Task task, ResultCallback<Action> callback) {
        super(context);
        this.task = task;

        binding = DialogSelectActionBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });

        adapter = new SelectActionPageAdapter(callback);
        binding.actionsBox.setAdapter(adapter);

        new TabLayoutMediator(binding.tabBox, binding.actionsBox, (tab, position) -> {
            if (position < adapter.currentTag.size()) {
                tab.setText(adapter.currentTag.get(position));
            }
        }).attach();

        binding.group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                GroupType groupType = (GroupType) view.getTag(KEY);
                binding.addButton.setTag(groupType);
                Map<String, List<Object>> map = (Map<String, List<Object>>) view.getTag(VALUE);
                adapter.setData(map, binding.group.indexOfChild(view) != 0);
            }
        });

        binding.searchButton.setOnClickListener(v -> {
            if (binding.searchBox.getVisibility() == View.VISIBLE) {
                binding.searchBox.setVisibility(View.GONE);
            } else {
                binding.searchBox.setVisibility(View.VISIBLE);
                binding.searchEdit.requestFocus();
            }
        });

        binding.tabBox.addOnTabSelectedListener(tabListener);

        binding.addButton.setOnClickListener(v -> {
            GroupType groupType = (GroupType) binding.addButton.getTag();
            switch (groupType) {
                case TASK -> {
                    Task newTask = new Task();
                    EditTaskDialog dialog = new EditTaskDialog(getContext(), newTask);
                    dialog.setTitle(R.string.task_add);
                    dialog.setCallback(result -> {
                        if (result) {
                            TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
                            if (tab != null) {
                                String string = getContext().getString(R.string.select_action_group_private);
                                if (Objects.equals(tab.getText(), string)) {
                                    task.addTask(newTask);
                                }
                            }
                            newTask.save();
                        }
                    });
                    dialog.show();
                }
                case VARIABLE -> {
                    Variable variable = new Variable(new PinObject());
                    EditVariableDialog dialog = new EditVariableDialog(getContext(), variable);
                    dialog.setTitle(R.string.variable_add);
                    dialog.setCallback(result -> {
                        if (result) {
                            TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
                            if (tab != null) {
                                String string = getContext().getString(R.string.select_action_group_private);
                                if (Objects.equals(tab.getText(), string)) {
                                    task.addVar(variable);
                                }
                            }
                            variable.save();
                        }
                    });
                    dialog.show();
                }
            }
        });
        initGroup();
    }

    protected void initGroup() {
        calculateShowData();
        if (dataMap == null || dataMap.isEmpty()) return;

        binding.group.removeAllViews();
        String[] groupName = getContext().getResources().getStringArray(R.array.group_type);
        dataMap.forEach((key, value) -> {
            WidgetSettingSelectButtonBinding buttonBinding = WidgetSettingSelectButtonBinding.inflate(LayoutInflater.from(getContext()), binding.group, true);
            buttonBinding.getRoot().setId(View.generateViewId());
            buttonBinding.getRoot().setText(groupName[key.ordinal()]);
            buttonBinding.getRoot().setTag(KEY, key);
            buttonBinding.getRoot().setTag(VALUE, value);
        });

        View child = binding.group.getChildAt(0);
        binding.group.check(child.getId());
        Map<String, List<Object>> map = (Map<String, List<Object>>) child.getTag(VALUE);
        adapter.setData(map, binding.group.indexOfChild(child) != 0);
    }

    protected void calculateShowData() {
        dataMap.clear();
        // 第一部分：预设Action
        Map<String, List<Object>> preset = new LinkedHashMap<>();
        for (ActionMap.ActionGroupType groupType : ActionMap.ActionGroupType.values()) {
            List<Object> types = new ArrayList<>(ActionMap.getTypes(groupType));
            preset.put(groupType.getName(), types);
        }
        dataMap.put(GroupType.PRESET, preset);


        // 第二部分：带CustomStartAction的Task
        Map<String, List<Object>> tasks = new LinkedHashMap<>();

        // 公共任务
        List<Object> publicTasks = new ArrayList<>(Saver.getInstance().getTasks());
        tasks.put(getContext().getString(R.string.select_action_group_global), publicTasks);

        // 私有任务
        List<Object> privateTasks = new ArrayList<>(task.getTasks());
        tasks.put(getContext().getString(R.string.select_action_group_private), privateTasks);

        // 父任务
        Task parent = task.getParent();
        while (parent != null) {
            List<Object> list = new ArrayList<>(parent.getTasks());
            if (!list.isEmpty()) tasks.put(parent.getTitle(), list);
            parent = parent.getParent();
        }
        dataMap.put(GroupType.TASK, tasks);


        // 第三部分：变量Variable
        Map<String, List<Object>> vars = new LinkedHashMap<>();

        List<Object> publicVars = new ArrayList<>(Saver.getInstance().getVars());
        vars.put(getContext().getString(R.string.select_action_group_global), publicVars);

        List<Object> privateVars = new ArrayList<>(task.getVars());
        vars.put(getContext().getString(R.string.select_action_group_private), privateVars);

        parent = task.getParent();
        while (parent != null) {
            List<Object> list = new ArrayList<>(parent.getVars());
            if (!list.isEmpty()) vars.put(parent.getTitle(), list);
            parent = parent.getParent();
        }
        dataMap.put(GroupType.VARIABLE, vars);
    }

    public void search() {
        if (adapter == null) return;
        Editable text = binding.searchEdit.getText();
        if (text == null || text.length() == 0) {
            adapter.search(null);
        } else {
            adapter.search(text.toString());
        }
    }

    protected enum GroupType {
        PRESET, TASK, VARIABLE
    }
}
