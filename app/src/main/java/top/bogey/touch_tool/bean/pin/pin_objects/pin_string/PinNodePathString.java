package top.bogey.touch_tool.bean.pin.pin_objects.pin_string;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.other.SimpleNodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;

public class PinNodePathString extends PinString {

    public PinNodePathString() {
        super(PinSubType.NODE_PATH);
    }

    protected PinNodePathString(PinSubType subType) {
        super(subType);
    }

    protected PinNodePathString(PinSubType subType, String value) {
        super(subType, value);
    }

    public PinNodePathString(String str) {
        super(PinSubType.NODE_PATH, str);
    }

    public PinNodePathString(JsonObject jsonObject) {
        super(jsonObject);
    }

    public NodeInfo findNode(List<NodeInfo> roots, boolean fullPath) {
        if (value == null || value.isEmpty()) return null;
        String[] paths = value.split("\n");
        if (paths.length == 0) return null;
        List<SimpleNodeInfo> pathNodes = new ArrayList<>();
        for (String string : paths) {
            SimpleNodeInfo nodeInfo = new SimpleNodeInfo(string);
            pathNodes.add(nodeInfo);
        }

        for (NodeInfo window : roots) {
            int index = 0;
            SimpleNodeInfo nodeInfo = pathNodes.get(index);
            if (nodeInfo.matchNode(window, fullPath)) {
                index++;

                while (index < pathNodes.size() && window != null) {
                    nodeInfo = pathNodes.get(index);
                    window = window.findChild(nodeInfo, fullPath);
                    index++;
                }
                if (window != null) return window;
            }
        }
        return null;
    }

    public void setValue(NodeInfo nodeInfo) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (nodeInfo != null) {
            builder.insert(0, nodeInfo + "\n");
            index++;
            if (index > Byte.MAX_VALUE) {
                setValue((String) null);
                return;
            }
            nodeInfo = nodeInfo.getParent();
        }
        setValue(builder.toString().trim());
    }

    public String getSimpleValue() {
        if (value == null) return "";
        String[] strings = value.split("\n");
        return strings[strings.length - 1];
    }
}
