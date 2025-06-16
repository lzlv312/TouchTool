package top.bogey.touch_tool.utils.tree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.utils.DisplayUtil;

public abstract class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.ViewHolder> {
    protected final List<TreeNode> treeNodes = new ArrayList<>();

    @NonNull
    @Override
    public abstract TreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refresh(treeNodes.get(position));
    }

    @Override
    public final int getItemCount() {
        return treeNodes.size();
    }

    public void setTreeNodes(List<TreeNode> treeNodes) {
        int size = this.treeNodes.size();
        this.treeNodes.clear();
        notifyItemRangeRemoved(0, size);

        this.treeNodes.addAll(treeNodes);
        notifyItemRangeInserted(0, this.treeNodes.size());
    }

    public void addTreeNode(TreeNode treeNode) {
        treeNodes.add(treeNode);
        notifyItemInserted(treeNodes.size() - 1);
    }

    public void collapseAll() {
        for (int i = treeNodes.size() - 1; i >= 0; i--) {
            TreeNode treeNode = treeNodes.get(i);
            if (treeNode.getDepth() == 0) {
                treeNode.setExpanded(false, true);
                notifyItemChanged(i);
            } else {
                treeNodes.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void collapseNode(TreeNode node) {
        if (!node.isExpanded()) return;
        node.setExpanded(false);
        int index = treeNodes.indexOf(node);
        notifyItemChanged(index);

        int nextIndex = index + 1;
        List<TreeNode> nodes = node.getExpandedChildren();
        treeNodes.subList(nextIndex, nextIndex + nodes.size()).clear();
        notifyItemRangeRemoved(nextIndex, nodes.size());
    }

    public void expandAll() {
        if (treeNodes.isEmpty()) return;
        int index = 0;
        while (index < treeNodes.size()) {
            TreeNode node = treeNodes.get(index);
            if (!node.isExpanded()) {
                node.setExpanded(true);
                notifyItemChanged(index);

                List<TreeNode> children = node.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    TreeNode child = children.get(i);
                    treeNodes.add(index + i + 1, child);
                    notifyItemInserted(index + i + 1);
                }
            }
            index++;
        }
    }

    public void expandNode(TreeNode node) {
        int index = treeNodes.indexOf(node);
        if (index >= 0) {
            notifyItemChanged(index);

            if (node.isExpanded()) return;
            node.setExpanded(true);

            int nextIndex = index + 1;
            List<TreeNode> children = node.getExpandedChildren();
            for (int i = 0; i < children.size(); i++) {
                TreeNode child = children.get(i);
                treeNodes.add(nextIndex + i, child);
            }
            notifyItemRangeInserted(nextIndex, children.size());
        } else {
            TreeNode parent = node.getParent();
            if (parent == null) return;
            node.setExpanded(true);
            expandNode(parent);
        }
    }

    public void switchNodeExpand(TreeNode node) {
        if (node.isExpanded()) {
            collapseNode(node);
        } else {
            expandNode(node);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected final TreeAdapter adapter;
        protected final Context context;
        protected final int padding;
        protected TreeNode node;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(TreeAdapter adapter, @NonNull View itemView) {
            super(itemView);
            this.adapter = adapter;
            context = itemView.getContext();
            padding = (int) DisplayUtil.dp2px(context, 8);

            itemView.setOnClickListener(v -> {
                if (node.isExpanded()) {
                    adapter.collapseNode(node);
                } else {
                    adapter.expandNode(node);
                }
                onClicked(v);
            });

            itemView.setOnLongClickListener(this::onLongClicked);
        }

        public void onClicked(View view) {
        }

        public boolean onLongClicked(View view) {
            return false;
        }

        public Rect getPadding() {
            return new Rect(node.getDepth() * padding, 0, 0, padding / 4);
        }

        public void refresh(TreeNode node) {
            this.node = node;
            Rect rect = getPadding();
            DisplayUtil.setViewMargin(itemView, rect.left, rect.top, rect.right, rect.bottom);
        }
    }
}
