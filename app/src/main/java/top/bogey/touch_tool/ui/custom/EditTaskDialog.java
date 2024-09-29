package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.TaskSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.DialogCreateTaskBinding;
import top.bogey.touch_tool.databinding.ViewTagListItemBinding;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;

public class EditTaskDialog extends MaterialAlertDialogBuilder {
    private final DialogCreateTaskBinding binding;
    private final List<String> selectedTags = new ArrayList<>();
    private BooleanResultCallback callback;

    public EditTaskDialog(@NonNull Context context, Task task) {
        super(context);

        binding = DialogCreateTaskBinding.inflate(LayoutInflater.from(context), null, false);
        setView(binding.getRoot());

        binding.titleEdit.setText(task.getTitle());
        binding.desEdit.setText(task.getDescription());

        List<String> tags = TaskSaver.getInstance().getAllTags();

        List<String> currTags = task.getTags();
        if (currTags != null) {
            for (String tag : currTags) {
                if (tags.contains(tag)) selectedTags.add(tag);
            }
        }

        for (String tag : tags) {
            ViewTagListItemBinding itemBinding = ViewTagListItemBinding.inflate(LayoutInflater.from(context), binding.tagBox, true);
            Chip chip = itemBinding.getRoot();
            chip.setCloseIconVisible(false);

            chip.setText(tag);
            if (currTags != null) {
                chip.setChecked(currTags.contains(tag));
            } else {
                chip.setChecked(false);
            }
            chip.setOnClickListener(v -> {
                if (!selectedTags.remove(tag)) {
                    selectedTags.add(tag);
                }
            });
        }

        setPositiveButton(R.string.enter, (dialog, which) -> {
            dialog.dismiss();
            if (getTitle().isEmpty()) {
                callback.onResult(false);
                return;
            }
            task.setTitle(getTitle());
            task.setDescription(getDescription());
            task.setTags(selectedTags);
            callback.onResult(true);
        });

        setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            callback.onResult(false);
        });
    }

    public void setCallback(BooleanResultCallback callback) {
        this.callback = callback;
    }

    private String getTitle() {
        Editable text = binding.titleEdit.getText();
        if (text != null && text.length() > 0) return text.toString();
        return "";
    }

    private String getDescription() {
        Editable text = binding.desEdit.getText();
        if (text != null && text.length() > 0) return text.toString();
        return "";
    }
}
