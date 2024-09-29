package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.ActionMap;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.databinding.DialogSelectActionBinding;
import top.bogey.touch_tool.databinding.WidgetSettingSelectButtonBinding;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class SelectActionDialog extends BottomSheetDialog {
    private final DialogSelectActionBinding binding;
    private final SelectActionPageAdapter adapter;
    private final Map<String, Map<String, List<Object>>> dataMap = new LinkedHashMap<>();

    public SelectActionDialog(@NonNull Context context, CardLayoutView cardLayoutView, VariableInfo variableInfo) {
        super(context);
        binding = DialogSelectActionBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });

        adapter = new SelectActionPageAdapter(cardLayoutView, variableInfo != null);
        binding.actionsBox.setAdapter(adapter);

        new TabLayoutMediator(binding.tabBox, binding.actionsBox, (tab, position) -> {
            if (position < adapter.currentTag.size()) {
                tab.setText(adapter.currentTag.get(position));
            }
        }).attach();

        calculateShowData(variableInfo);
        dataMap.forEach((key, value) -> {
            WidgetSettingSelectButtonBinding buttonBinding = WidgetSettingSelectButtonBinding.inflate(LayoutInflater.from(context), binding.group, true);
            buttonBinding.getRoot().setId(View.generateViewId());
            buttonBinding.getRoot().setText(key);
            buttonBinding.getRoot().setTag(value);
        });
        if (!dataMap.isEmpty()) binding.group.check(binding.group.getChildAt(0).getId());
        binding.group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                Map<String, List<Object>> map = (Map<String, List<Object>>) view.getTag();
                adapter.setData(map, binding.group.indexOfChild(view) != 0);

                int index = binding.group.indexOfChild(view);
                binding.addButton.setVisibility(index == 1 || index == 2 ? View.VISIBLE : View.GONE);
            }
        });
        View view = binding.group.getChildAt(0);
        Map<String, List<Object>> map = (Map<String, List<Object>>) view.getTag();
        adapter.setData(map, binding.group.indexOfChild(view) != 0);

        binding.searchButton.setOnClickListener(v -> {
            if (binding.searchBox.getVisibility() == View.VISIBLE) {
                binding.searchBox.setVisibility(View.GONE);
            } else {
                binding.searchBox.setVisibility(View.VISIBLE);
                binding.searchEdit.requestFocus();
            }
        });
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

    public void calculateShowData(VariableInfo variableInfo) {
        dataMap.clear();
        // 第一部分：预设Action
        Map<String, List<Object>> preset = new LinkedHashMap<>();
        if (variableInfo == null) {
            for (ActionMap.ActionGroupType groupType : ActionMap.ActionGroupType.values()) {
                List<Object> types = new ArrayList<>(ActionMap.getTypes(groupType));
                preset.put(groupType.getName(), types);
            }
        } else {
            for (ActionMap.ActionGroupType groupType : ActionMap.ActionGroupType.values()) {
                List<Object> types = new ArrayList<>();
                for (ActionType type : ActionMap.getTypes(groupType)) {
                    ActionInfo info = ActionInfo.getActionInfo(type);
                    if (info == null) continue;
                    Action action = info.getAction();
                    if (action == null) continue;
                    Pin pin = action.findConnectToAblePin(new Pin(variableInfo.getValue(), variableInfo.isOut()));
                    if (pin == null) continue;
                    types.add(type);
                }
                preset.put(groupType.getName(), types);
            }
        }
        dataMap.put(getContext().getString(R.string.select_action_group_preset), preset);
        // 第二部分：任务Action
        // 第三部分：变量Action
        // 第四部分：卡片Action
    }
}
