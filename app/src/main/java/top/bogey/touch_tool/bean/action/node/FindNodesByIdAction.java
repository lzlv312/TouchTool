package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;

public class FindNodesByIdAction extends FindExecuteAction {
    private final transient Pin idPin = new Pin(new PinString(), R.string.find_nodes_by_id_action_id);
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, false, false, true);
    private final transient Pin nodesPin = new Pin(new PinList(PinType.NODE), R.string.pin_node, true);

    public FindNodesByIdAction() {
        super(ActionType.FIND_NODES_BY_ID);
        addPins(idPin, areaPin, nodesPin);
    }

    public FindNodesByIdAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(idPin, areaPin, nodesPin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinString id = getPinValue(runnable, idPin);
        PinArea area = getPinValue(runnable, areaPin);
        PinList nodes = nodesPin.getValue();
        String value = id.getValue();
        if (value == null || value.isEmpty()) return false;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        NodeInfo nodeInfo = new NodeInfo(root);
        List<NodeInfo> childrenInArea = nodeInfo.findChildrenInArea(area.getValue());
        Set<NodeInfo> nodeInfoSet = new HashSet<>(childrenInArea);
        List<NodeInfo> childrenById = nodeInfo.findChildrenById(value);
        for (NodeInfo info : childrenById) {
            if (nodeInfoSet.contains(info)) {
                nodes.add(new PinNode(info));
            }
        }

        return !nodes.isEmpty();
    }
}
