package top.bogey.touch_tool.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.IDragTouchHelperAdapter;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.ViewTagListItemBinding;
import top.bogey.touch_tool.utils.AppUtil;

public class TaskTagAdapter extends RecyclerView.Adapter<TaskTagAdapter.ViewHolder> implements IDragTouchHelperAdapter {
    private final TaskView taskView;
    private final Set<String> tags;
    private final List<String> allTags;
    private final Saver saver;

    public TaskTagAdapter(TaskView taskView, Set<String> tags) {
        this.taskView = taskView;
        this.tags = tags;
        this.saver = Saver.getInstance();
        this.allTags = saver.getAllTags();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载标签项布局
        ViewTagListItemBinding itemBinding = ViewTagListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(allTags.get(position));
    }

    @Override
    public int getItemCount() {
        return allTags.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= allTags.size() || toPosition < 0 || toPosition >= allTags.size()) {
            return;
        }
        Collections.swap(allTags, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDragEnded() {
        saver.updateTagOrder(allTags);
    }

    public void refreshTags() {
        List<String> newTags = saver.getAllTags();

        Set<String> oldTagsSet = new HashSet<>(tags);
        Set<String> newTagsSet = new HashSet<>(newTags);

        // 先移除已不存在的标签
        for (int i = allTags.size() - 1; i >= 0; i--) {
            String tag = allTags.get(i);
            if (!newTagsSet.contains(tag)) {
                allTags.remove(i);
                notifyItemRemoved(i);
            }
        }

        for (int i = 0; i < newTags.size(); i++) {
            String tag = newTags.get(i);
            int index = allTags.indexOf(tag);
            if (index == -1) {
                // 新标签，添加到对应位置
                allTags.add(i, tag);
                notifyItemInserted(i);
            } else if (index != i) {
                // 标签位置变化，移动位置
                allTags.remove(index);
                allTags.add(i, tag);
                notifyItemMoved(index, i);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewTagListItemBinding binding;

        public ViewHolder(ViewTagListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String tag) {
            Chip chip = binding.getRoot();
            chip.setText(tag);
            chip.setOnCloseIconClickListener(v -> AppUtil.showDialog(itemView.getContext(), R.string.tag_remove, result -> {
                if (result) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        saver.removeTag(tag);
                        allTags.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            }));
            if (taskView.selecting) {
                chip.setCheckable(true);
                chip.setChecked(tags.contains(tag));
                chip.setOnClickListener(v -> {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        boolean wasChecked = tags.contains(tag);
                        if (wasChecked) {
                            tags.remove(tag);
                        } else {
                            tags.add(tag);
                        }
                        notifyItemChanged(position);
                        for (String id : taskView.selected) {
                            Task task = Saver.getInstance().getTask(id);
                            if (wasChecked) {
                                task.removeTag(tag);
                            } else {
                                task.addTag(tag);
                            }
                            task.save();
                        }
                    }
                });
            } else {
                chip.setCheckable(false);
                chip.setChecked(false);
                chip.setOnClickListener(v -> taskView.gotoTargetTag(tag));
            }
        }
    }
}