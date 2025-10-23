package top.bogey.touch_tool.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.databinding.ViewTaskPageBinding;
import top.bogey.touch_tool.utils.DisplayUtil;

public class TaskPageViewAdapter extends RecyclerView.Adapter<TaskPageViewAdapter.ViewHolder> {

    private final TaskView taskView;
    final List<String> tags = new ArrayList<>();
    private boolean search = false;
    private final Set<TaskPageItemRecyclerViewAdapter> adapters = new HashSet<>();

    public TaskPageViewAdapter(TaskView taskView) {
        this.taskView = taskView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(ViewTaskPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        Saver.getInstance().addListener(viewHolder.adapter);
        adapters.add(viewHolder.adapter);
        return viewHolder;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        for (TaskPageItemRecyclerViewAdapter adapter : adapters) {
            if (adapter == null) continue;
            Saver.getInstance().removeListener(adapter);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refresh(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void setTags(List<String> tags) {
        search = false;
        int oldSize = this.tags.size();
        int newSize = tags.size();
        this.tags.clear();
        this.tags.addAll(tags);
        notifyItemRangeChanged(0, Math.min(oldSize, newSize));
        if (oldSize < newSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize);
        } else if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize);
        }
    }

    public void search(String name) {
        search = true;
        int oldSize = this.tags.size();
        int newSize = 1;
        tags.clear();
        tags.add(name);
        notifyItemRangeChanged(0, Math.min(oldSize, newSize));
        if (oldSize < newSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize);
        } else if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskPageItemRecyclerViewAdapter adapter;

        public ViewHolder(@NonNull ViewTaskPageBinding binding) {
            super(binding.getRoot());

            adapter = new TaskPageItemRecyclerViewAdapter(taskView);
            binding.getRoot().setAdapter(adapter);
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) binding.getRoot().getLayoutManager();
            if (layoutManager == null) return;
            if (DisplayUtil.isPortrait(binding.getRoot().getContext())) {
                layoutManager.setSpanCount(2);
            } else {
                layoutManager.setSpanCount(4);
            }
        }

        public void refresh(String tag) {
            if (search) {
                adapter.setTasks(tag, Saver.getInstance().searchTasks(tag));
            } else {
                adapter.setTasks(tag, Saver.getInstance().getTasks(tag));
            }
        }
    }
}
