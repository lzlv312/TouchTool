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
import top.bogey.touch_tool.ui.custom.MarkTargetFloatView;

public class FindNodesByTextAction extends FindExecuteAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, false, false, true);
    private final transient Pin nodesPin = new Pin(new PinList(new PinNode()), R.string.pin_node, true);
    private final transient Pin firstNodePin = new Pin(new PinNode(), R.string.pin_node_first, true);

    public FindNodesByTextAction() {
        super(ActionType.FIND_NODES_BY_TEXT);
        addPins(textPin, areaPin, nodesPin, firstNodePin);
    }

    public FindNodesByTextAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, areaPin, nodesPin, firstNodePin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinString text = getPinValue(runnable, textPin);
        PinArea area = getPinValue(runnable, areaPin);
        PinList nodes = nodesPin.getValue();
        String value = text.getValue();
        if (value == null || value.isEmpty()) return false;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        NodeInfo nodeInfo = new NodeInfo(root);
        List<NodeInfo> childrenInArea = nodeInfo.findChildrenInArea(area.getValue());
        Set<NodeInfo> nodeInfoSet = new HashSet<>(childrenInArea);
        List<NodeInfo> childrenByText = nodeInfo.findChildrenByText(value);
        for (NodeInfo info : childrenByText) {
            if (nodeInfoSet.contains(info)) {
                nodes.add(new PinNode(info));
                MarkTargetFloatView.showTargetArea(info.area);
            }
        }
        if (nodes.isEmpty()) return false;
        firstNodePin.setValue(nodes.get(0));
        return true;
    }
}
