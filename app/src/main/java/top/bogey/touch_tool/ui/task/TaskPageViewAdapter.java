package top.bogey.touch_tool.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.save.TaskSaver;
import top.bogey.touch_tool.databinding.ViewTaskPageBinding;

public class TaskPageViewAdapter extends RecyclerView.Adapter<TaskPageViewAdapter.ViewHolder> {

    private final TaskView taskView;
    final List<String> tags = new ArrayList<>();
    private boolean search = false;

    public TaskPageViewAdapter(TaskView taskView) {
        this.taskView = taskView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewTaskPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        this.tags.clear();
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }

    public void search(String name) {
        search = true;
        tags.clear();
        tags.add(name);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskPageItemRecyclerViewAdapter adapter;

        public ViewHolder(@NonNull ViewTaskPageBinding binding) {
            super(binding.getRoot());

            adapter = new TaskPageItemRecyclerViewAdapter(taskView);
            binding.getRoot().setAdapter(adapter);
        }

        public void refresh(String tag) {
            if (search) {
                adapter.setTasks(tag, TaskSaver.getInstance().searchTasks(tag));
            } else {
                adapter.setTasks(tag, TaskSaver.getInstance().getTasks(tag));
            }
        }
    }
}
