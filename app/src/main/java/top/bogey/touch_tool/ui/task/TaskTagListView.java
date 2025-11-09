package top.bogey.touch_tool.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashSet;
import java.util.Set;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.ViewTagListBinding;
import top.bogey.touch_tool.utils.AppUtil;

public class TaskTagListView extends BottomSheetDialogFragment {
    private final TaskView taskView;
    private final Set<String> tags = new HashSet<>();
    private TaskTagAdapter taskTagAdapter;

    public TaskTagListView(TaskView taskView) {
        this.taskView = taskView;
        if (taskView.selecting) {
            for (String id : taskView.selected) {
                Task task = Saver.getInstance().getTask(id);
                if (tags.isEmpty()) {
                    if (task != null && task.getTags() != null) tags.addAll(task.getTags());
                } else {
                    if (task != null && task.getTags() != null) tags.retainAll(task.getTags());
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewTagListBinding binding = ViewTagListBinding.inflate(inflater, container, false);

        taskTagAdapter = new TaskTagAdapter(taskView, tags);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW); // 水平方向排列
        layoutManager.setJustifyContent(JustifyContent.FLEX_START); // 左对齐
        layoutManager.setFlexWrap(FlexWrap.WRAP); // 自动换行
        binding.tagBox.setLayoutManager(layoutManager);
        binding.tagBox.setAdapter(taskTagAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TagItemTouchHelperCallback(taskTagAdapter));
        itemTouchHelper.attachToRecyclerView(binding.tagBox);

        binding.addButton.setOnClickListener(v -> AppUtil.showEditDialog(requireContext(), R.string.task_tag_add, "", result -> {
            if (result != null && !result.isEmpty()) {
                Saver.getInstance().addTag(result);
                taskTagAdapter.refreshTags();
            }
        }));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        taskView.unselectAll();
        taskView.hideBottomBar();
        taskView.resetTags();
        super.onDestroyView();
    }

    public static class TagItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final ItemTouchHelperAdapter adapter;

        public TagItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT; // 允许上下拖拽
            int swipeFlags = 0;
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
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder != null) {
                ViewGroup parent = (ViewGroup) viewHolder.itemView.getParent();
                if (parent != null) {
                    parent.bringChildToFront(viewHolder.itemView);
                }
                viewHolder.itemView.setScaleX(1.05f);
                viewHolder.itemView.setScaleY(1.05f);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setScaleX(1f);
            viewHolder.itemView.setScaleY(1f);
            adapter.onItemDragEnded();
        }
    }

    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);

        void onItemDragEnded();
    }
}
