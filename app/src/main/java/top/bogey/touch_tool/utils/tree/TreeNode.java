package top.bogey.touch_tool.utils.tree;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private final List<TreeNode> children = new ArrayList<>();
    private boolean initChildren = false;
    private TreeNode parent;

    private ITreeNodeDataLoader loader;
    private Object flag;

    private ITreeNodeData treeNode;

    private boolean expand = false;
    private int depth = 0;

    public TreeNode(ITreeNodeDataLoader loader, Object flag) {
        this.loader = loader;
        this.flag = flag;
    }

    public TreeNode(ITreeNodeData treeNode) {
        this(treeNode, true);
    }

    public TreeNode(ITreeNodeData treeNode, boolean originChildren) {
        this.treeNode = treeNode;
        initChildren = !originChildren;
    }

    @Nullable
    public ITreeNodeData getData() {
        if (loader != null && flag != null) {
            treeNode = loader.loadData(flag);
        }
        return treeNode;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        setExpand(expand, false);
    }

    public void setExpand(boolean expand, boolean children) {
        this.expand = expand;
        if (children) {
            for (TreeNode child : this.children) {
                child.setExpand(expand, true);
            }
        }
    }

    public List<TreeNode> getChildren() {
        if (initChildren) return children;
        getData();
        if (treeNode == null) return children;
        for (ITreeNodeData data : treeNode.getChildren()) {
            addChild(new TreeNode(data));
        }
        initChildren = true;
        return children;
    }

    public void addChild(TreeNode node) {
        children.add(node);
        node.parent = this;
        node.depth = depth + 1;
    }

    public TreeNode getParent() {
        return parent;
    }

    public List<TreeNode> getExpandChildren() {
        List<TreeNode> list = new ArrayList<>();
        for (TreeNode child : getChildren()) {
            list.add(child);
            if (child.isExpand()) list.addAll(child.getExpandChildren());
        }
        return list;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
        for (TreeNode child : getChildren()) {
            child.setDepth(depth + 1);
        }
    }
}
