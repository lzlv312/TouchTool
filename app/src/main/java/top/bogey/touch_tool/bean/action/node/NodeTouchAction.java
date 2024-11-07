package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.service.TaskRunnable;

public class NodeTouchAction extends ExecuteAction {
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);
    private final transient Pin ltPin = new Pin(new PinBoolean(), R.string.node_touch_action_long);
    private final transient Pin elsePin = new Pin(new PinBoolean(), R.string.node_touch_action_else, true);

    public NodeTouchAction() {
        super(ActionType.NODE_TOUCH);
        addPins(nodePin, ltPin, elsePin);
    }

    public NodeTouchAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(nodePin, ltPin, elsePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinNode node = getPinValue(runnable, nodePin);
        PinBoolean longTouch = getPinValue(runnable, ltPin);
        NodeInfo nodeInfo = node.getNodeInfo();
        if (nodeInfo != null) {
            AccessibilityNodeInfo accessibilityNodeInfo = nodeInfo.findUsableParentNode();
            if (accessibilityNodeInfo != null) {
                if (longTouch.getValue()) {
                    if (accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)) {
                        executeNext(runnable, outPin);
                        return;
                    }
                } else {
                    if (accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        executeNext(runnable, outPin);
                        return;
                    }
                }
            }
        }
        executeNext(runnable, elsePin);
    }
}
