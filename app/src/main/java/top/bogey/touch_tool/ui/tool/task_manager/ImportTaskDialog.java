package top.bogey.touch_tool.ui.tool.task_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.databinding.DialogTaskManagerBinding;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.GsonUtil;

@SuppressLint("ViewConstructor")
public class ImportTaskDialog extends FrameLayout {
    private static void showDialog(Context context, ImportTaskDialog view) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.task_import)
                .setView(view)
                .setPositiveButton(R.string.import_task, (dialog, which) -> view.importTask())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public static void showDialog(Context context, Uri uri) {
        if (uri == null) return;

        byte[] bytes = AppUtil.readFile(context, uri);
        TaskRecord taskRecord = null;
        try {
            taskRecord = GsonUtil.getAsObject(new String(bytes), TaskRecord.class, null);
        } catch (Exception ignored) {
        }
        if (taskRecord == null) return;
        ImportTaskDialog view = new ImportTaskDialog(context, taskRecord);
        showDialog(context, view);
    }

    private final ImportTaskDialogAdapter adapter;

    public ImportTaskDialog(@NonNull Context context, TaskRecord taskRecord) {
        super(context);
        DialogTaskManagerBinding binding = DialogTaskManagerBinding.inflate(LayoutInflater.from(context), this, true);

        adapter = new ImportTaskDialogAdapter(taskRecord);
        binding.selectionBox.setAdapter(adapter);
    }

    public void importTask() {
        TaskRecord taskRecord = adapter.getTaskRecord();
        for (Task task : taskRecord.tasks()) {
            Saver.getInstance().saveTask(task);
        }
        for (Variable variable : taskRecord.variables()) {
            Saver.getInstance().saveVar(variable);
        }
    }
}
