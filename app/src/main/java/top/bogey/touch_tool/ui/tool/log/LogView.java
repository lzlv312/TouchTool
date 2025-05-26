package top.bogey.touch_tool.ui.tool.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNodeManager;

import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.DialogLogBinding;

@SuppressLint("ViewConstructor")
public class LogView extends FrameLayout {

    public LogView(@NonNull Context context, Task task, boolean detailLog) {
        super(context);

        DialogLogBinding binding = DialogLogBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setAdapter(new LogViewAdapter(new TreeNodeManager(), Saver.getInstance().getLog(task.getId()), detailLog));
    }
}
