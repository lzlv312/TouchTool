package top.bogey.touch_tool.bean.pin.pin_objects.pin_string;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;

public class PinNodePathString extends PinString {

    public PinNodePathString() {
        super(PinSubType.NODE_PATH);
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
                    NodePath nodePath = new NodePath(string);
                    result = nodePath.findChild(node);
                    if (result == null) break;
                }
            }
            if (result == node) return null;
            if (result != null) return result;
        }
        return null;
    }

    public void setValue(NodeInfo node) {
        if (node == null) {
            value = null;
            return;
        }

        List<NodePath> nodes = new ArrayList<>();
        while (node != null) {
            nodes.add(new NodePath(node));
            node = node.parent;
            if (nodes.size() > Byte.MAX_VALUE) {
                value = null;
                return;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = nodes.size() - 1; i >= 0; i--) {
            NodePath nodePath = nodes.get(i);
            builder.append(nodePath).append("\n");
        }
        value = builder.toString().trim();
    }


    public static class NodePath {
        private String clazz;
        private String id;
        private int index = 1;

        public NodePath(NodeInfo nodeInfo) {
            clazz = nodeInfo.clazz;
            id = nodeInfo.id;

            if (nodeInfo.parent != null) {
                for (int i = 0; i < nodeInfo.parent.children.size(); i++) {
                    NodeInfo info = nodeInfo.parent.children.get(i);
                    if (info == nodeInfo) {
                        index = i + 1;
                        break;
                    }
                }
            }
        }

        public NodePath(String path) {
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

        private boolean checkClass(NodeInfo nodeInfo) {
            if (clazz == null) return false;
            return clazz.equals(nodeInfo.clazz);
        }

        private boolean checkId(NodeInfo nodeInfo) {
            if (id == null) return false;
            return id.equals(nodeInfo.id);
        }

        public NodeInfo findChild(NodeInfo nodeInfo) {
            List<NodeInfo> children = nodeInfo.children;
            NodeInfo child = null;
            // 先根据class,id,index一起查找
            if (index > 0 && index <= children.size()) {
                NodeInfo info = children.get(index);
                if (info != null && checkClass(info) && checkId(info)) child = info;
            }

            // 如果没找到，再根据class,id查找
            if (child == null) {
                for (NodeInfo info : children) {
                    if (info == null) continue;
                    if (checkClass(info) && checkId(info)) {
                        child = info;
                        break;
                    }
                }
            }

            // 如果还是没找到，再根据class,index查找
            if (child == null) {
                if (index > 0 && index <= children.size()) {
                    NodeInfo info = children.get(index);
                    if (info != null && checkClass(info)) child = info;
                }
            }

            // 如果还是没找到，再根据class查找
            if (child == null) {
                for (NodeInfo info : children) {
                    if (info == null) continue;
                    if (checkClass(info)) {
                        child = info;
                        break;
                    }
                }
            }

            return child;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(clazz);
            if (id != null) builder.append("[id=").append(id).append("]");
            if (index > 1) builder.append("[").append(index).append("]");
            return builder.toString();
        }
    }
}
