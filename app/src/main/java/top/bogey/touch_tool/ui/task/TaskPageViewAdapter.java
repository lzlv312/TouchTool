package top.bogey.touch_tool.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
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

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TaskItemTouchHelperCallback(adapter));
            itemTouchHelper.attachToRecyclerView(binding.getRoot());
            adapter.setItemTouchHelper(itemTouchHelper);

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
                adapter.setTasks(tag, Saver.getInstance().getOrderedTasks(tag));
            }
        }
    }

    private static class TaskItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final TaskTagListView.ItemTouchHelperAdapter adapter;

        public TaskItemTouchHelperCallback(TaskTagListView.ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            // 允许上下左右拖拽（网格布局需要支持左右拖拽）
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int swipeFlags = 0; // 不支持滑动删除
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getBindingAdapterPosition();
            int toPosition = target.getBindingAdapterPosition();
            if (fromPosition != RecyclerView.NO_POSITION && toPosition != RecyclerView.NO_POSITION) {
                adapter.onItemMove(fromPosition, toPosition);
                return true;
            }
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // 不处理滑动事件
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // 拖拽时放大视图效果
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder != null) {
                viewHolder.itemView.setScaleX(1.05f);
                viewHolder.itemView.setScaleY(1.05f);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            // 拖拽结束后恢复视图大小
            viewHolder.itemView.setScaleX(1f);
            viewHolder.itemView.setScaleY(1f);
            adapter.onItemDragEnded();
        }
    }
}
