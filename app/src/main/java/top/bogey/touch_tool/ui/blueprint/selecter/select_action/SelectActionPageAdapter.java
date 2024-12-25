package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.databinding.DialogSelectActionPageBinding;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;

public class SelectActionPageAdapter extends RecyclerView.Adapter<SelectActionPageAdapter.ViewHolder> {

    private final ResultCallback<Action> callback;
    private final Map<String, List<Object>> dataMap = new HashMap<>();
    private final List<String> tags = new ArrayList<>();
    final List<String> currentTag = new ArrayList<>();

    private boolean search = false;
    private boolean sort = true;


    public SelectActionPageAdapter(ResultCallback<Action> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogSelectActionPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refresh(currentTag.get(position));
    }

    @Override
    public int getItemCount() {
        return currentTag.size();
    }

    public void setData(Map<String, List<Object>> dataMap, boolean sort) {
        this.dataMap.clear();
        this.dataMap.putAll(dataMap);

        tags.clear();
        tags.addAll(dataMap.keySet());
        this.sort = sort;
        if (sort) AppUtil.chineseSort(tags, tag -> tag);

        search(null);
        notifyDataSetChanged();
    }

    public void search(String name) {
        search = name != null && !name.isEmpty();
        currentTag.clear();
        if (search) {
            currentTag.add(name);
        } else {
            currentTag.addAll(tags);
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final SelectActionPageItemRecyclerViewAdapter adapter;

        public ViewHolder(@NonNull DialogSelectActionPageBinding binding) {
            super(binding.getRoot());

            adapter = new SelectActionPageItemRecyclerViewAdapter(callback);
            binding.getRoot().setAdapter(adapter);
        }

        public void refresh(String tag) {
            if (search) {
                List<Object> data = new ArrayList<>();
                Pattern pattern = AppUtil.getPattern(tag);

                for (Map.Entry<String, List<Object>> entry : dataMap.entrySet()) {
                    List<Object> list = entry.getValue();
                    for (Object object : list) {
                        String name = SelectActionPageItemRecyclerViewAdapter.getObjectTitle(object);

                        if (pattern != null) {
                            if (pattern.matcher(name).find()) {
                                data.add(object);
                            }
                        } else {
                            if (name.contains(tag)) {
                                data.add(object);
                            }
                        }
                    }
                }
                adapter.setData(data, true);
            } else {
                adapter.setData(dataMap.get(tag), sort);
            }
        }
    }
}
