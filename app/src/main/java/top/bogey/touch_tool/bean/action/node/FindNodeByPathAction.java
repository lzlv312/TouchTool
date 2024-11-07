package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.logic.FindExecuteAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.AppUtil;

public class FindNodeByPathAction extends FindExecuteAction {
    private final transient Pin pathPin = new Pin(new PinNodePathString(), R.string.find_node_by_path_action_path);
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node, true);

    public FindNodeByPathAction() {
        super(ActionType.FIND_NODE_BY_PATH);
        addPins(pathPin, nodePin);
    }

    public FindNodeByPathAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(pathPin, nodePin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinNodePathString path = getPinValue(runnable, pathPin);
        List<NodeInfo> rootNodes = new ArrayList<>();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
            rootNodes.add(new NodeInfo(window));
        }
        NodeInfo node = path.findNode(rootNodes);
        if (node == null) return false;

        nodePin.getValue(PinNode.class).setNodeInfo(node);
        return true;
    }
}
