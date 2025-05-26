package top.bogey.touch_tool.ui.tool.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.LogInfo;
import top.bogey.touch_tool.bean.save.LogSave;
import top.bogey.touch_tool.databinding.DialogLogItemBinding;

public class LogViewAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final LogSave logSave;
    private final boolean detailLog;

    public LogViewAdapter(TreeNodeManager manager, LogSave logSave, boolean detailLog) {
        super(null, manager);
        this.manager = manager;
        this.logSave = logSave;
        this.detailLog = detailLog;
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogLogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {

    }

    private void updateTree() {
        int count;
        if (detailLog) {
            count = logSave.getDetailLogCount();
        } else {
            count = logSave.getLogCount();
        }
        for (int i = count - 1; i >= 0; i--) {
            TreeNode treeNode = new TreeNode(i, R.layout.float_log_item);

        }
    }

    public static class ViewHolder extends TreeViewHolder {
        private final Context context;

        public ViewHolder(@NonNull DialogLogItemBinding binding) {
            super(binding.getRoot());
            context = binding.getRoot().getContext();
        }

        public void refresh(LogInfo logInfo) {

        }
    }
}
