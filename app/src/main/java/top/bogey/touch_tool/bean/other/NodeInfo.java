package top.bogey.touch_tool.bean.other;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.utils.AppUtil;

public class NodeInfo {
    public String clazz;
    public String id;
    public String text;
    public String desc;
    public boolean usable;
    public boolean visible;
    public Rect area;

    public NodeInfo parent;
    public int index = -1;

    public final List<NodeInfo> children = new ArrayList<>();

    public final AccessibilityNodeInfo nodeInfo;

    public NodeInfo(AccessibilityNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;

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
                info.index = i;
                children.add(info);
            }
        }
    }

    public List<NodeInfo> findChildrenById(String id) {
        List<NodeInfo> result = new ArrayList<>();
        for (NodeInfo child : children) {
            if (child.id != null && child.id.contains(id)) result.add(child);
            result.addAll(child.findChildrenById(id));
        }
        return result;
    }

    public List<NodeInfo> findChildrenByText(String text) {
        List<NodeInfo> result = new ArrayList<>();
        for (NodeInfo child : children) {

            if (child.text != null && !child.text.isEmpty()) {
                Pattern pattern = AppUtil.getPattern(text);
                if (pattern == null) {
                    if (child.text.contains(text)) result.add(child);
                } else {
                    if (pattern.matcher(child.text).find()) {
                        result.add(child);
                    }
                }
            }

            result.addAll(child.findChildrenById(id));
        }
        return result;
    }

    public List<NodeInfo> findChildrenInArea(Rect area) {
        List<NodeInfo> result = new ArrayList<>();
        for (NodeInfo child : children) {
            if (Rect.intersects(area, child.area)) result.add(child);
            result.addAll(child.findChildrenInArea(area));
        }
        return result;
    }

    public NodeInfo findUsableChild(int x, int y) {
        for (int i = children.size() - 1; i >= 0; i--) {
            NodeInfo child = children.get(i);
            NodeInfo result = child.findUsableChild(x, y);
            if (result != null) return result;
        }
        if (area.contains(x, y) && usable && visible) return this;
        return null;
    }

    public AccessibilityNodeInfo findUsableParentNode() {
        if (usable) return nodeInfo;
        return parent == null ? null : parent.findUsableParentNode();
    }
}
