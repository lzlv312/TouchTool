package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.logic.FindExecuteAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;

public class FindNodesInAreaAction extends FindExecuteAction {
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private final transient Pin nodesPin = new Pin(new PinList(PinType.NODE), R.string.pin_node, true);

    public FindNodesInAreaAction() {
        super(ActionType.FIND_NODES_IN_AREA);
        addPins(areaPin, nodesPin);
    }

    public FindNodesInAreaAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(areaPin, nodesPin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinArea area = getPinValue(runnable, areaPin);
        PinList nodes = nodesPin.getValue();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        NodeInfo nodeInfo = new NodeInfo(root);
        List<NodeInfo> childrenInArea = nodeInfo.findChildrenInArea(area.getValue());
        for (NodeInfo info : childrenInArea) {
            nodes.add(new PinNode(info));
        }

        return !nodes.isEmpty();
    }
}
