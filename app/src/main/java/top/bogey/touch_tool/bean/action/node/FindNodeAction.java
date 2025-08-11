package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.FindExecuteAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_list.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathTextString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.pin.special_pin.ShowAblePin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.MarkTargetFloatView;
import top.bogey.touch_tool.utils.AppUtil;

public class FindNodeAction extends FindExecuteAction {
    private final transient Pin typePin = new NotLinkAblePin(new PinSingleSelect(R.array.find_node_type), R.string.find_node_action_type);
    private final transient Pin pathPin = new PathShowablePin(new PinNodePathString(), R.string.find_node_action_path);
    private final transient Pin fullPathPin = new PathShowablePin(new PinBoolean(true), R.string.find_node_action_full_path);
    private final transient Pin textPin = new TextShowablePin(new PinString(), R.string.pin_string);
    private final transient Pin idPin = new IdShowablePin(new PinString(), R.string.find_node_action_id);
    private final transient Pin areaPin = new NotAllPathShowablePin(new PinArea(), R.string.pin_area);
    private final transient Pin pathTextPin = new PathTextShowablePin(new PinNodePathTextString(), R.string.find_node_action_regex_path);
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node, true);
    private final transient Pin nodesPin = new NotPathShowablePin(new PinList(new PinNode()), true);

    public FindNodeAction() {
        super(ActionType.FIND_NODE);
        reAddPins(typePin, pathPin, fullPathPin, textPin, idPin, areaPin, pathTextPin, nodePin, nodesPin);
    }

    public FindNodeAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(typePin, pathPin, fullPathPin, textPin, idPin, areaPin, pathTextPin, nodePin, nodesPin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinSingleSelect type = typePin.getValue();
        PinList nodes = nodesPin.getValue();

        MainAccessibilityService service = MainApplication.getInstance().getService();

        switch (type.getIndex()) {
            case 0 -> {
                List<NodeInfo> rootNodes = new ArrayList<>();
                for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
                    rootNodes.add(new NodeInfo(window));
                }
                PinString pathString = getPinValue(runnable, pathPin);
                PinNodePathString path = new PinNodePathString(pathString.getValue());
                PinBoolean fullPath = getPinValue(runnable, fullPathPin);
                NodeInfo node = path.findNode(rootNodes, fullPath.getValue());
                if (node == null) return false;
                nodePin.getValue(PinNode.class).setNodeInfo(node);
                MarkTargetFloatView.showTargetArea(node.area);
                return true;
            }
            case 1, 2 -> {
                PinArea area = getPinValue(runnable, areaPin);
                AccessibilityNodeInfo root = service.getRootInActiveWindow();
                if (root == null) return false;
                NodeInfo nodeInfo = new NodeInfo(root);
                List<NodeInfo> children;
                if (type.getIndex() == 1) {
                    PinString text = getPinValue(runnable, textPin);
                    String value = text.getValue();
                    if (value == null || value.isEmpty()) return false;
                    children = nodeInfo.findChildrenByText(value);
                } else {
                    PinString id = getPinValue(runnable, idPin);
                    String value = id.getValue();
                    if (value == null || value.isEmpty()) return false;
                    children = nodeInfo.findChildrenById(value);
                }
                List<NodeInfo> childrenInArea = nodeInfo.findChildrenInArea(area.getValue());
                Set<NodeInfo> nodeInfoSet = new HashSet<>(childrenInArea);
                for (NodeInfo info : children) {
                    if (nodeInfoSet.contains(info)) {
                        nodes.add(new PinNode(info));
                        MarkTargetFloatView.showTargetArea(info.area);
                    }
                }
                if (nodes.isEmpty()) return false;
                nodePin.setValue(nodes.get(0));
                return true;
            }
            case 3 -> {
                List<NodeInfo> rootNodes = new ArrayList<>();
                for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
                    rootNodes.add(new NodeInfo(window));
                }
                PinString pathString = getPinValue(runnable, pathTextPin);
                PinNodePathTextString path = new PinNodePathTextString(pathString.getValue());
                List<NodeInfo> findNodes = path.findNodes(rootNodes);
                if (findNodes == null || findNodes.isEmpty()) return false;
                for (NodeInfo findNode : findNodes) {
                    nodes.add(new PinNode(findNode));
                    MarkTargetFloatView.showTargetArea(findNode.area);
                }
                nodePin.setValue(nodes.get(0));
                return true;
            }
        }
        return false;
    }

    private int getTypeValue() {
        PinSingleSelect type = typePin.getValue();
        return type.getIndex();
    }

    private static class PathShowablePin extends ShowAblePin {
        public PathShowablePin(PinBase value, int titleId) {
            super(value, titleId);
        }

        @Override
        public boolean showAble(Task context) {
            FindNodeAction action = (FindNodeAction) context.getAction(getOwnerId());
            return action.getTypeValue() == 0;
        }
    }

    private static class PathTextShowablePin extends ShowAblePin {
        public PathTextShowablePin(PinBase value, int titleId) {
            super(value, titleId);
        }

        @Override
        public boolean showAble(Task context) {
            FindNodeAction action = (FindNodeAction) context.getAction(getOwnerId());
            return action.getTypeValue() == 3;
        }
    }

    private static class NotAllPathShowablePin extends ShowAblePin {
        public NotAllPathShowablePin(PinBase value, int titleId) {
            super(value, titleId);
        }

        @Override
        public boolean showAble(Task context) {
            FindNodeAction action = (FindNodeAction) context.getAction(getOwnerId());
            return action.getTypeValue() != 0 && action.getTypeValue() != 3;
        }
    }

    private static class NotPathShowablePin extends ShowAblePin {
        public NotPathShowablePin(PinBase value, boolean out) {
            super(value, out);
        }

        @Override
        public boolean showAble(Task context) {
            FindNodeAction action = (FindNodeAction) context.getAction(getOwnerId());
            return action.getTypeValue() != 0;
        }
    }


    private static class TextShowablePin extends ShowAblePin {
        public TextShowablePin(PinBase value, int titleId) {
            super(value, titleId);
        }

        @Override
        public boolean showAble(Task context) {
            FindNodeAction action = (FindNodeAction) context.getAction(getOwnerId());
            return action.getTypeValue() == 1;
        }
    }

    private static class IdShowablePin extends ShowAblePin {
        public IdShowablePin(PinBase value, int titleId) {
            super(value, titleId);
        }

        @Override
        public boolean showAble(Task context) {
            FindNodeAction action = (FindNodeAction) context.getAction(getOwnerId());
            return action.getTypeValue() == 2;
        }
    }
}
