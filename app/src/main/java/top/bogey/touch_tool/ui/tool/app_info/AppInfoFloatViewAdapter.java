package top.bogey.touch_tool.ui.tool.app_info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.FloatAppInfoItemBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.tree.NormalTreeNode;
import top.bogey.touch_tool.utils.tree.ObjectTreeNodeData;
import top.bogey.touch_tool.utils.tree.TreeAdapter;
import top.bogey.touch_tool.utils.tree.TreeNode;

public class AppInfoFloatViewAdapter extends TreeAdapter {

    @NonNull
    @Override
    public TreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FloatAppInfoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void addPackageActivity(TaskInfoSummary.PackageActivity packageActivity) {
        TreeNode node = new NormalTreeNode(new PackageActivityInfo(packageActivity));
        addTreeNode(node);
    }

    public class ViewHolder extends TreeAdapter.ViewHolder {
        private final Context context;
        private final FloatAppInfoItemBinding binding;

        public ViewHolder(@NonNull FloatAppInfoItemBinding binding) {
            super(AppInfoFloatViewAdapter.this, binding.getRoot());
            context = binding.getRoot().getContext();
            this.binding = binding;
        }

        public void refresh(TreeNode node) {
            super.refresh(node);

            if (node.getData() instanceof PackageActivityInfo info) {
                binding.title.setText(info.getName());
                binding.activityName.setText(info.getActivityName());
                binding.activityName.setVisibility(View.VISIBLE);
                binding.expandButton.setOnClickListener(null);
                binding.expandButton.setIconResource(node.isExpanded() ? R.drawable.icon_keyboard_arrow_down : R.drawable.icon_keyboard_arrow_right);
            } else if (node.getData() instanceof ObjectTreeNodeData data) {
                binding.title.setText(data.toString());
                binding.activityName.setVisibility(View.GONE);
                binding.expandButton.setOnClickListener(v -> AppUtil.copyToClipboard(context, data.toString()));
                binding.expandButton.setIconResource(R.drawable.icon_content_copy);
            }
        }
    }
}
