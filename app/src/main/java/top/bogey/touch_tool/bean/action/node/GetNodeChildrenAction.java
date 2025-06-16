package top.bogey.touch_tool.bean.action.node;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.service.TaskRunnable;

public class GetNodeChildrenAction extends CalculateAction {
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);
    private final transient Pin childrenPin = new Pin(new PinList(new PinNode()), R.string.pin_node, true);

    public GetNodeChildrenAction() {
        super(ActionType.GET_NODE_CHILDREN);
        addPins(nodePin, childrenPin);
    }

    public GetNodeChildrenAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(nodePin, childrenPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinNode node = getPinValue(runnable, nodePin);
        NodeInfo nodeInfo = node.getNodeInfo();
        for (NodeInfo child : nodeInfo.children) {
            childrenPin.getValue(PinList.class).add(new PinNode(child));
        }
    }
}
