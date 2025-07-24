package top.bogey.touch_tool.bean.pin.pin_objects.pin_string;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.utils.AppUtil;

public class PinNodePathString extends PinString {

    public PinNodePathString() {
        super(PinSubType.NODE_PATH);
    }

    protected PinNodePathString(PinSubType subType) {
        super(subType);
    }

    public PinNodePathString(String str) {
        super(PinSubType.NODE_PATH, str);
    }

    public PinNodePathString(JsonObject jsonObject) {
        super(jsonObject);
    }

    public NodeInfo findNode(List<NodeInfo> nodes) {
        if (value == null || value.isEmpty()) return null;

        String[] strings = value.split("\n");
        for (NodeInfo node : nodes) {
            if (node == null) continue;
            NodeInfo result = null;
            for (String string : strings) {
                if (string == null || string.isEmpty()) continue;
                if (result == null) result = node;
                else {
                    NodeInfo nodeInfo = new NodeInfo(string);
                    result = nodeInfo.findSelfInNode(result);
                    if (result == null) break;
                }
            }
            if (result == node) return null;
            if (result != null) return result;
        }
        return null;
    }

    public List<NodeInfo> findNodes(List<NodeInfo> nodes) {
        if (value == null || value.isEmpty()) return null;

        String[] strings = value.split("\n");
        return findNodes(nodes, strings, 0);
    }

    private List<NodeInfo> findNodes(List<NodeInfo> nodes, String[] path, int index) {
        List<NodeInfo> result = new ArrayList<>();
        if (index >= path.length) return result;
        for (NodeInfo node : nodes) {
            Pattern pattern = AppUtil.getPattern(path[index]);
            if (pattern == null) continue;
            if (pattern.matcher(node.getPath()).find()) {
                if (index == path.length - 1) result.add(node);
                else result.addAll(findNodes(node.children, path, index + 1));
            }
        }
        return result;
    }

    public void setValue(NodeInfo node) {
        if (node == null) {
            value = null;
            return;
        }

        List<NodeInfo> nodes = new ArrayList<>();
        while (node != null) {
            nodes.add(node);
            node = node.parent;
            if (nodes.size() > Byte.MAX_VALUE) {
                value = null;
                return;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = nodes.size() - 1; i >= 0; i--) {
            NodeInfo nodeInfo = nodes.get(i);
            builder.append(nodeInfo).append("\n");
        }
        value = builder.toString().trim();
    }

    public NodeInfo getNodeInfo() {
        if (value == null) return null;
        String[] strings = value.split("\n");
        NodeInfo node = null;
        for (String string : strings) {
            NodeInfo nodeInfo = new NodeInfo(string);
            if (node != null) {
                node.children.add(nodeInfo);
                nodeInfo.parent = node;
            }
            node = nodeInfo;
        }
        return node;
    }
}
