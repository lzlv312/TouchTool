package top.bogey.touch_tool.ui.tool.task_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.DialogTaskManagerBinding;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.GsonUtil;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class ExportTaskDialog extends FrameLayout {
    private static void showDialog(Context context, ExportTaskDialog view) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.task_export)
                .setView(view)
                .setPositiveButton(R.string.export_task, (dialog, which) -> view.export())
                .setNegativeButton(R.string.save, (dialog, which) -> view.save())
                .setNeutralButton(R.string.cancel, null)
                .show();
    }

    public static void showDialog(Context context) {
        List<Task> tasks = Saver.getInstance().getTasks();
        ExportTaskDialog view = new ExportTaskDialog(context, tasks, false);
        showDialog(context, view);
    }

    public static void showDialog(Context context, List<Task> tasks) {
        ExportTaskDialog view = new ExportTaskDialog(context, tasks, true);
        showDialog(context, view);
    }

    private final ExportTaskDialogAdapter adapter;

    public ExportTaskDialog(@NonNull Context context, List<Task> tasks, boolean selectAll) {
        super(context);
        DialogTaskManagerBinding binding = DialogTaskManagerBinding.inflate(LayoutInflater.from(context), this, true);

        adapter = new ExportTaskDialogAdapter();
        binding.selectionBox.setAdapter(adapter);

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();
                if (searchString.isEmpty()) {
                    adapter.refreshTasks(tasks);
                } else {
                    List<Task> searchTasks = new ArrayList<>();
                    for (Task task : tasks) {
                        if (AppUtil.isStringContains(task.getFullDescription(), searchString)) searchTasks.add(task);
                    }
                    adapter.refreshTasks(searchTasks);
                }
            }
        });

        adapter.refreshTasks(tasks);
        if (selectAll) adapter.selectAll();
    }

    public void export() {
        Context context = getContext();
        AppUtil.showEditDialog(context, R.string.file_name, "TT_" + AppUtil.formatDateTime(context, System.currentTimeMillis(), false, true), fileName -> {
            TaskRecord taskRecord = adapter.getTaskRecord();
            String json = GsonUtil.toJson(taskRecord);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("text/*");

            File file = AppUtil.writeFile(context, AppUtil.TASK_DIR_NAME, fileName + ".tt", json.getBytes());
            if (file != null) {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(intent);
            }
        });
    }

    public void save() {
        Context context = getContext();
        TaskRecord taskRecord = adapter.getTaskRecord();
        String json = GsonUtil.toJson(taskRecord);

        MainActivity activity = MainApplication.getInstance().getActivity();
        AppUtil.exportFile(activity, "TT_" + AppUtil.formatDateTime(context, System.currentTimeMillis(), false, true) + ".tt", json.getBytes());
    }
}
