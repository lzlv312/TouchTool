package top.bogey.touch_tool.bean.other;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class NodeInfo {
    public String clazz;
    public String id;
    public String text;
    public String desc;
    public boolean usable;
    public boolean visible;
    public Rect area;
    public NodeInfo parent;
    public final List<NodeInfo> children = new ArrayList<>();

    public NodeInfo(AccessibilityNodeInfo nodeInfo) {
        CharSequence className = nodeInfo.getClassName();
        if (className != null) clazz = className.toString();

        id = nodeInfo.getViewIdResourceName();

        CharSequence nodeText = nodeInfo.getText();
        if (nodeText != null) text = nodeText.toString();

        CharSequence nodeDesc = nodeInfo.getContentDescription();
        if (nodeDesc != null) desc = nodeDesc.toString();

        usable = nodeInfo.isEnabled() && (nodeInfo.isCheckable() || nodeInfo.isClickable() || nodeInfo.isLongClickable() || nodeInfo.isEditable());
        visible = nodeInfo.isVisibleToUser();
        area = new Rect();
        nodeInfo.getBoundsInScreen(area);

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                NodeInfo info = new NodeInfo(child);
                info.parent = this;
                children.add(info);
            }
        }
    }

    public List<NodeInfo> findChildren(String id) {
        List<NodeInfo> result = new ArrayList<>();
        for (NodeInfo child : children) {
            if (id.equals(child.id)) result.add(child);
            result.addAll(child.findChildren(id));
        }
        return result;
    }

    public static NodeInfo findChild(NodeInfo nodeInfo, int x, int y) {
        if (nodeInfo.area.contains(x, y) && nodeInfo.usable && nodeInfo.visible) return nodeInfo;
        for (NodeInfo child : nodeInfo.children) {
            NodeInfo result = findChild(child, x, y);
            if (result != null) return result;
        }
        return null;
    }

    public static NodeInfo findChildReverse(NodeInfo nodeInfo, int x, int y) {
        List<NodeInfo> nodeInfos = nodeInfo.children;
        for (int i = nodeInfos.size() - 1; i >= 0; i--) {
            NodeInfo child = nodeInfos.get(i);
            NodeInfo result = findChildReverse(child, x, y);
            if (result != null) return result;
        }
        if (nodeInfo.area.contains(x, y) && nodeInfo.usable && nodeInfo.visible) return nodeInfo;
        return null;
    }
}
