package top.bogey.touch_tool.bean.other;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
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
    public int index = 0;

    public final List<NodeInfo> children = new ArrayList<>();

    public AccessibilityNodeInfo nodeInfo = null;

    public NodeInfo(String path) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9.]+)$");
        // 代表没有任何额外信息的节点
        if (pattern.matcher(path).find()) {
            clazz = path;
        } else {
            pattern = Pattern.compile("^(.+?)(\\[.+])$");
            Matcher matcher = pattern.matcher(path);
            if (matcher.find()) {
                clazz = matcher.group(1);
                String detail = matcher.group(2);
                if (detail == null) return;

                String[] strings = detail.split("\\[");
                for (String string : strings) {
                    if (string.isEmpty()) continue;
                    List<String> regexes = Arrays.asList("id=(.+)]", "(\\d+)]");
                    for (int i = 0; i < regexes.size(); i++) {
                        String regex = regexes.get(i);
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(string);
                        if (matcher.find()) {
                            switch (i) {
                                case 0 -> id = matcher.group(1);
                                case 1 -> index = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

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
                info.index = i + 1;
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
                if (AppUtil.isStringContains(child.text, text)) {
                    result.add(child);
                }
            }

            result.addAll(child.findChildrenByText(text));
        }
        return result;
    }

    public List<NodeInfo> findChildrenInArea(Rect area) {
        List<NodeInfo> result = new ArrayList<>();
        if (!visible) return result;
        for (NodeInfo child : children) {
            if (Rect.intersects(area, child.area)) {
                result.add(child);
                result.addAll(child.findChildrenInArea(area));
            }
        }
        return result;
    }

    public void findChildrenInArea(Map<NodeInfo, Integer> map, Rect area, int depth) {
        if (!visible) return;
        for (NodeInfo child : children) {
            if (Rect.intersects(area, child.area)) {
                map.put(child, depth);
                child.findChildrenInArea(map, area, depth + 1);
            }
        }
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

    private boolean checkId(NodeInfo node) {
        if (id == null) return false;
        return id.equals(node.id);
    }

    private boolean checkClass(NodeInfo node) {
        return clazz.equals(node.clazz);
    }

    public NodeInfo findSelfInNode(NodeInfo node) {
        NodeInfo result = null;

        // 先根据class,id,index一起查找
        if (index > 0 && index <= node.children.size()) {
            NodeInfo child = node.children.get(index - 1);
            if (checkId(child) && checkClass(child)) result = child;
        }

        // 如果没找到，再根据class,id查找
        if (result == null) {
            for (NodeInfo child : node.children) {
                if (checkId(child) && checkClass(child)) {
                    result = child;
                    break;
                }
            }
        }

        // 如果还是没找到，再根据class,index查找
        if (result == null) {
            if (index > 0 && index <= node.children.size()) {
                NodeInfo child = node.children.get(index - 1);
                if (checkClass(child)) result = child;
            }
        }

        // 如果还是没找到，再根据class查找
        if (result == null) {
            for (NodeInfo child : node.children) {
                if (checkClass(child)) {
                    result = child;
                    break;
                }
            }
        }

        return result;
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
}
