package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.MarkTargetFloatView;
import top.bogey.touch_tool.utils.AppUtil;

public class IsNodeExistByPathAction extends CalculateAction {
    private final transient Pin pathPin = new Pin(new PinNodePathString(), R.string.find_node_by_path_action_path);
    private final transient Pin resultPin = new Pin(new PinBoolean(), R.string.pin_boolean_result, true);
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node, true);

    public IsNodeExistByPathAction() {
        super(ActionType.IS_NODE_EXIST_BY_PATH);
        addPins(pathPin, resultPin, nodePin);
    }

    public IsNodeExistByPathAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(pathPin, resultPin, nodePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinNodePathString path = getPinValue(runnable, pathPin);
        List<NodeInfo> rootNodes = new ArrayList<>();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
            rootNodes.add(new NodeInfo(window));
        }
        NodeInfo node = path.findNode(rootNodes);
        if (node == null) return;

        resultPin.getValue(PinBoolean.class).setValue(true);
        nodePin.getValue(PinNode.class).setNodeInfo(node);
        MarkTargetFloatView.showTargetArea(node.area);
    }
}
