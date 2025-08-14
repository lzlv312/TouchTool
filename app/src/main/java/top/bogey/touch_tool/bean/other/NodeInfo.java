package top.bogey.touch_tool.bean.other;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.tree.ILazyTreeNodeData;
import top.bogey.touch_tool.utils.tree.ITreeNodeData;
import top.bogey.touch_tool.utils.tree.ITreeNodeDataLoader;

public class NodeInfo extends SimpleNodeInfo implements ITreeNodeData, ITreeNodeDataLoader, ILazyTreeNodeData {
    public final transient AccessibilityNodeInfo node;
    private final transient List<NodeInfo> children = new ArrayList<>();

    public String text;
    public String desc;
    public boolean usable;
    public boolean visible;
    public Rect area;


    public NodeInfo(AccessibilityNodeInfo node) {
        this.node = node;

        CharSequence className = node.getClassName();
        if (className != null) clazz = className.toString();

        id = node.getViewIdResourceName();

        CharSequence nodeText = node.getText();
        if (nodeText != null) text = nodeText.toString();

        CharSequence nodeDesc = node.getContentDescription();
        if (nodeDesc != null) desc = nodeDesc.toString();

        usable = node.isEnabled() && (node.isCheckable() || node.isClickable() || node.isLongClickable() || node.isEditable());
        visible = node.isVisibleToUser();
        area = new Rect();
        node.getBoundsInScreen(area);

        for (int i = 0; i < node.getChildCount(); i++) {
            children.add(null);
        }
    }

    public int getChildCount() {
        return node.getChildCount();
    }

    public List<NodeInfo> getChildren() {
        List<NodeInfo> children = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            NodeInfo child = getChild(i);
            if (child != null) children.add(child);
        }
        return children;
    }

    public List<NodeInfo> getCacheChildren() {
        return children;
    }

    public NodeInfo getChild(int index) {
        NodeInfo nodeInfo = children.get(index);
        if (nodeInfo == null) {
            AccessibilityNodeInfo child = node.getChild(index);
            if (child != null) {
                nodeInfo = new NodeInfo(child);
                nodeInfo.index = index + 1;
                children.set(index, nodeInfo);
            }
        }
        return nodeInfo;
    }

    public NodeInfo findUsableChild(int x, int y) {
        for (int i = getChildCount(); i > 0; i--) {
            NodeInfo child = getChild(i);
            NodeInfo result = child.findUsableChild(x, y);
            if (result != null) return result;
        }
        if (area.contains(x, y) && usable && visible) return this;
        return null;
    }

    public NodeInfo getParent() {
        AccessibilityNodeInfo parent = node.getParent();
        return parent == null ? null : new NodeInfo(parent);
    }

    public NodeInfo findUsableParent() {
        NodeInfo parent = getParent();
        while (parent != null) {
            if (parent.usable) return parent;
            parent = parent.getParent();
        }
        return null;
    }

    public static List<NodeInfo> getWindows() {
        List<NodeInfo> rootNodes = new ArrayList<>();
        for (AccessibilityNodeInfo window : AppUtil.getWindows(MainApplication.getInstance().getService())) {
            rootNodes.add(new NodeInfo(window));
        }
        return rootNodes;
    }

    public NodeInfo findChild(SimpleNodeInfo nodeInfo, boolean fullPath) {
        Map<Integer, NodeInfo> children = new HashMap<>();
        // 先根据class，id，index一起查找
        if (nodeInfo.index > 0 && nodeInfo.index <= getChildCount()) {
            NodeInfo child = children.computeIfAbsent(nodeInfo.index, i -> getChild(i - 1));
            if (child != null) {
                if (nodeInfo.matchNodeClass(child) && nodeInfo.matchNodeId(child)) return child;
            }
        }

        // 如果没找到，再根据class，id查找
        for (int i = 0; i < getChildCount(); i++) {
            NodeInfo child = children.computeIfAbsent(i, this::getChild);
            if (child != null) {
                if (nodeInfo.matchNodeClass(child) && nodeInfo.matchNodeId(child)) return child;
            }
        }

        // 如果还是没找到，再根据class，index查找
        if (nodeInfo.index > 0 && nodeInfo.index <= getChildCount()) {
            NodeInfo child = children.computeIfAbsent(nodeInfo.index, i -> getChild(i - 1));
            if (child != null) {
                if (nodeInfo.matchNodeClass(child)) return child;
            }
        }

        //带标记，却没有找到，不再继续
        if (fullPath && (nodeInfo.index > 1 || nodeInfo.id != null)) return null;

        //如果还是没找到，再根据class查找
        for (int i = 0; i < getChildCount(); i++) {
            NodeInfo child = children.computeIfAbsent(i, this::getChild);
            if (child != null) {
                if (nodeInfo.matchNodeClass(child)) return child;
            }
        }

        return null;
    }

    public List<NodeInfo> findChildren(Rect area) {
        List<NodeInfo> nodes = new ArrayList<>();
        Queue<NodeInfo> queue = new LinkedList<>();
        if (area.contains(this.area) || Rect.intersects(area, this.area)) queue.add(this);
        while (!queue.isEmpty()) {
            NodeInfo node = queue.poll();
            if (node == null) continue;
            nodes.add(node);
            for (NodeInfo child : getChildren()) {
                if (area.contains(child.area) || Rect.intersects(area, child.area)) {
                    queue.add(child);
                }
            }
        }
        return nodes;
    }

    public void mapChildrenDepth(Map<NodeInfo, Integer> map, Rect area, int depth) {
        for (NodeInfo child : getChildren()) {
            if (area.contains(child.area) || Rect.intersects(area, child.area)) {
                map.put(child, depth);
                child.mapChildrenDepth(map, area, depth + 1);
            }
        }
    }

    public List<NodeInfo> findChildrenByText(String text, Rect area) {
        List<NodeInfo> nodes = new ArrayList<>();
        Queue<NodeInfo> queue = new LinkedList<>();
        Pattern pattern = AppUtil.getPattern(text);
        if (area.contains(this.area) || Rect.intersects(area, this.area)) queue.add(this);
        while (!queue.isEmpty()) {
            NodeInfo node = queue.poll();
            if (node == null) continue;
            if (pattern == null) {
                if (node.text.toLowerCase().contains(text.toLowerCase())) nodes.add(node);
            } else {
                if (pattern.matcher(node.text).find()) nodes.add(node);
            }

            for (NodeInfo child : getChildren()) {
                if (area.contains(child.area) || Rect.intersects(area, child.area)) {
                    queue.add(child);
                }
            }
        }
        return nodes;
    }

    public List<NodeInfo> findChildrenById(String id, Rect area) {
        List<NodeInfo> nodes = new ArrayList<>();
        Queue<NodeInfo> queue = new LinkedList<>();
        if (area.contains(this.area) || Rect.intersects(area, this.area)) queue.add(this);
        while (!queue.isEmpty()) {
            NodeInfo node = queue.poll();
            if (node == null) continue;
            if (id.equals(node.id)) nodes.add(node);

            for (NodeInfo child : getChildren()) {
                if (area.contains(child.area) || Rect.intersects(area, child.area)) {
                    queue.add(child);
                }
            }
        }
        return nodes;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(clazz);
        if (id != null && !id.isEmpty()) builder.append("[id=").append(id).append("]");
        if (index > 1) builder.append("[").append(index).append("]");
        return builder.toString();
    }

    public String getPath() {
        StringBuilder builder = new StringBuilder();
        builder.append(clazz);
        if (id != null && !id.isEmpty()) builder.append("[id=").append(id).append("]");
        builder.append("[").append(index).append("]");
        return builder.toString();
    }

    @Override
    public List<Object> getChildrenFlags() {
        List<Object> flags = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            flags.add(i);
        }
        return flags;
    }

    @Override
    public List<ITreeNodeData> getChildrenData() {
        return new ArrayList<>(getChildren());
    }

    @Override
    public ILazyTreeNodeData loadData(Object flag) {
        return getChild((Integer) flag);
    }
}
