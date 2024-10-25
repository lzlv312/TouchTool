package top.bogey.touch_tool.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.AppUtil;

public class GetWindowsAction extends CalculateAction {
    private final transient Pin windowsPin = new Pin(new PinList(PinType.NODE), R.string.pin_node, true);

    public GetWindowsAction() {
        super(ActionType.GET_WINDOWS);
        addPin(windowsPin);
    }

    public GetWindowsAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(windowsPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
            NodeInfo nodeInfo = new NodeInfo(window);
            windowsPin.getValue(PinList.class).add(new PinNode(nodeInfo));
        }
    }
}
