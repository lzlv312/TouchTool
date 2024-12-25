package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.databinding.DialogCreateTaskBinding;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;

public class EditVariableDialog extends MaterialAlertDialogBuilder {
    private final DialogCreateTaskBinding binding;
    private BooleanResultCallback callback;

    public EditVariableDialog(@NonNull Context context, Variable var) {
        super(context);

        binding = DialogCreateTaskBinding.inflate(LayoutInflater.from(context), null, false);
        setView(binding.getRoot());

        binding.titleEdit.setText(var.getTitle());
        binding.desEdit.setText(var.getDescription());

        setPositiveButton(R.string.enter, (dialog, which) -> {
            dialog.dismiss();
            if (getTitle().isEmpty()) {
                callback.onResult(false);
                return;
            }
            var.setTitle(getTitle());
            var.setDescription(getDescription());
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
