package top.bogey.touch_tool.ui.tool.log;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.normal.LoggerAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.LogInfo;
import top.bogey.touch_tool.bean.save.LogSave;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatLogItemBinding;
import top.bogey.touch_tool.ui.blueprint.BlueprintView;
import top.bogey.touch_tool.utils.tree.TreeAdapter;
import top.bogey.touch_tool.utils.tree.TreeNode;

public class LogViewAdapter extends TreeAdapter {
    private Task task;

    @NonNull
    @Override
    public TreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FloatLogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void setLogSave(LogSave logSave) {
        task = Saver.getInstance().getTask(logSave.getKey());

        List<TreeNode> nodeList = new ArrayList<>();
        for (int i = 1; i < logSave.getLogCount() + 1; i++) {
            TreeNode node = new TreeNode(logSave, i);
            nodeList.add(node);
        }
        setTreeNodes(nodeList);
    }

    public void addLog(LogInfo log) {
        TreeNode node = new TreeNode(log);
        addTreeNode(node);
    }

    public class ViewHolder extends TreeAdapter.ViewHolder {
        private final FloatLogItemBinding binding;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull FloatLogItemBinding binding) {
            super(LogViewAdapter.this, binding.getRoot());
            this.binding = binding;

            binding.gotoButton.setOnClickListener(v -> {
                if (node == null) return;
                LogInfo logInfo = (LogInfo) node.getData();
                if (logInfo == null) return;
                BlueprintView.tryFocusAction(logInfo.getAction(task));
            });
        }

        @SuppressLint("SetTextI18n")
        public void refresh(TreeNode node) {
            super.refresh(node);
            this.node = null;
            binding.title.setText(null);
            binding.time.setText(null);
            binding.icon.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.INVISIBLE);
            binding.gotoButton.setVisibility(View.GONE);

            LogInfo logInfo = (LogInfo) node.getData();
            if (logInfo == null) return;
            Action action = logInfo.getAction(task);
            if (action == null) return;
            this.node = node;

            binding.icon.setVisibility(View.VISIBLE);
            binding.gotoButton.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();
            int size = node.getChildren().size();
            if (logInfo.getIndex() == -1 && action instanceof LoggerAction loggerAction) {
                Pin logPin = loggerAction.getLogPin();
                PinObject pinObject = logInfo.getValues().get(logPin.getId());
                if (pinObject != null) builder.append(": ").append(pinObject);
                binding.imageView.setVisibility(View.INVISIBLE);
            } else {
                builder.append("[").append(logInfo.getIndex()).append("] ");
                builder.append(action.getTitle());
                if (size > 0) builder.append("(").append(size).append(")");
                binding.imageView.setImageResource(node.isExpand() ? R.drawable.icon_arrow_up : R.drawable.icon_arrow_down);
                binding.imageView.setVisibility(size == 0 ? View.INVISIBLE : View.VISIBLE);
            }

            binding.title.setText(builder.toString());
            binding.time.setText(logInfo.getTime(context));
            binding.icon.setImageResource(logInfo.isExecute() ? R.drawable.icon_shuffle : R.drawable.icon_equal);
        }
    }
}
