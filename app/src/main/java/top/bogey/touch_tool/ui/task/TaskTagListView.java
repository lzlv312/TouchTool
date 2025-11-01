package top.bogey.touch_tool.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.ViewTagListBinding;
import top.bogey.touch_tool.databinding.ViewTagListItemBinding;
import top.bogey.touch_tool.utils.AppUtil;

public class TaskTagListView extends BottomSheetDialogFragment {
    private final TaskView taskView;
    private final Set<String> tags = new HashSet<>();
    private ViewTagListBinding binding;

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
        binding = ViewTagListBinding.inflate(inflater, container, false);

        binding.addButton.setOnClickListener(v -> AppUtil.showEditDialog(requireContext(), R.string.task_tag_add, "", result -> {
            if (result != null && !result.isEmpty()) {
                Saver.getInstance().addTag(result);
                binding.tagBox.removeAllViews();
                List<String> tags = Saver.getInstance().getAllTags();
                tags.forEach(this::addTagChip);
            }
        }));

        List<String> tags = Saver.getInstance().getAllTags();
        tags.forEach(this::addTagChip);

        return binding.getRoot();
    }

    private void addTagChip(String tag) {
        ViewTagListItemBinding itemBinding = ViewTagListItemBinding.inflate(LayoutInflater.from(requireContext()), binding.tagBox, true);
        Chip chip = itemBinding.getRoot();
        chip.setText(tag);
        chip.setOnCloseIconClickListener(v -> AppUtil.showDialog(requireContext(), R.string.tag_remove, result -> {
            if (result) {
                Saver.getInstance().removeTag(tag);
                binding.tagBox.removeView(chip);
            }
        }));

        if (taskView.selecting) {
            chip.setCheckable(true);
            chip.setChecked(tags.contains(tag));
            chip.setOnClickListener(v -> {
                if (tags.remove(tag)) {
                    for (String id : taskView.selected) {
                        Task task = Saver.getInstance().getTask(id);
                        task.removeTag(tag);
                        task.save();
                    }
                } else {
                    tags.add(tag);
                    for (String id : taskView.selected) {
                        Task task = Saver.getInstance().getTask(id);
                        task.addTag(tag);
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

    @Override
    public void onDestroyView() {
        taskView.unselectAll();
        taskView.hideBottomBar();
        super.onDestroyView();
    }
}
