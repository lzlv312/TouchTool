package top.bogey.touch_tool.ui.blueprint.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.databinding.FloatPickerNodeItemBinding;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;

public class NodePickerTreeAdapter extends TreeViewAdapter {
    private final List<TreeNode> treeNodes = new ArrayList<>();
    private final TreeNodeManager manager;
    private final List<NodeInfo> roots;
    private TreeNode selectedNode;

    public NodePickerTreeAdapter(TreeNodeManager manager, SelectNode picker, List<NodeInfo> roots) {
        super(null, manager);
        this.manager = manager;
        this.roots = roots;
        setTreeNodeLongClickListener((treeNode, view) -> {
            NodeInfo nodeInfo = (NodeInfo) treeNode.getValue();
            picker.selectNode(nodeInfo);
            setSelectedNode(nodeInfo);
            return true;
        });
        searchNodes(null);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.refreshNode(manager.get(position), selectedNode);
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        FloatPickerNodeItemBinding binding = FloatPickerNodeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    public void setSelectedNode(NodeInfo nodeInfo) {
        collapseAll();
        if (nodeInfo == null) selectedNode = null;
        else {
            selectedNode = findTreeNode(treeNodes, nodeInfo);
            if (selectedNode != null) {
                TreeNode parent = selectedNode.getParent();
                while (parent != null) {
                    TreeNode p = parent.getParent();
                    if (p == null) {
                        parent.setExpanded(false);
                        expandNode(parent);
                    } else {
                        parent.setExpanded(true);
                    }
                    parent = p;
                }
            }
        }
    }

    public void searchNodes(String search) {
        treeNodes.clear();
        Pattern pattern = AppUtil.getPattern(search);
        for (NodeInfo root : roots) {
            TreeNode tree;
            if (pattern == null) {
                tree = createTree(root, 0);
            } else {
                tree = createTree(root, 0, pattern);
            }
            if (tree != null) treeNodes.add(tree);
        }
        updateTreeNodes(treeNodes);
        if (pattern != null) expandAll();
    }

    private TreeNode createTree(NodeInfo node, int level) {
        TreeNode treeNode = new TreeNode(node, R.layout.float_picker_node_item);
        treeNode.setLevel(level);
        for (NodeInfo child : node.children) {
            treeNode.addChild(createTree(child, level + 1));
        }
        return treeNode;
    }

    private TreeNode createTree(NodeInfo node, int level, @NonNull Pattern pattern) {
        boolean found = false;
        if (node.text != null && pattern.matcher(node.text).find()) found = true;
        else if (node.id != null && pattern.matcher(node.id).find()) found = true;
        else if (node.clazz != null && pattern.matcher(node.clazz).find()) found = true;

        TreeNode treeNode = new TreeNode(node, R.layout.float_picker_node_item);
        treeNode.setLevel(level);

        for (NodeInfo child : node.children) {
            TreeNode tree = createTree(child, level + 1, pattern);
            if (tree != null) treeNode.addChild(tree);
        }

        if (treeNode.getChildren().isEmpty() && !found) return null;
        return treeNode;
    }

    private static TreeNode findTreeNode(List<TreeNode> treeNodes, Object value) {
        for (TreeNode treeNode : treeNodes) {
            if (treeNode.getValue().equals(value)) return treeNode;
            TreeNode childNode = findTreeNode(treeNode.getChildren(), value);
            if (childNode != null) return childNode;
        }
        return null;
    }

    public interface SelectNode {
        void selectNode(NodeInfo nodeInfo);
    }

    private static class ViewHolder extends TreeViewHolder {
        private final FloatPickerNodeItemBinding binding;
        private final Context context;

        public ViewHolder(@NonNull FloatPickerNodeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();
        }

        @Override
        public void bindTreeNode(TreeNode node) {
            int padding = (int) (node.getLevel() * DisplayUtil.dp2px(context, 8));
            binding.contentBox.setPaddingRelative(padding, 0, 0, 0);
        }

        public void refreshNode(TreeNode node, TreeNode selectedNode) {
            NodeInfo nodeInfo = (NodeInfo) node.getValue();

            binding.titleText.setText(getNodeTitle(nodeInfo));
            int color;

            if (node == selectedNode)
                color = DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryVariant);
            else {
                if (nodeInfo.usable && nodeInfo.visible) {
                    color = DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryVariant);
                } else {
                    color = DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorOnSurface);
                }
            }
            binding.titleText.setTextColor(color);

            binding.imageView.setImageTintList(ColorStateList.valueOf(color));
            binding.imageView.setVisibility(nodeInfo.children.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            binding.imageView.setImageResource(node.isExpanded() ? R.drawable.icon_up : R.drawable.icon_down);
        }

        private String getNodeTitle(NodeInfo nodeInfo) {
            StringBuilder builder = new StringBuilder();
            if (nodeInfo.text != null && !nodeInfo.text.isEmpty()) builder.append(nodeInfo.text).append(" | ");
            builder.append(nodeInfo);
            return builder.toString();
        }
    }
}
