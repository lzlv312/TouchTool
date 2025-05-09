package top.bogey.touch_tool.bean.action.node;

import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;

public class EditTextInputAction extends ExecuteAction {
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.edit_text_input_action_edit_text);
    private final transient Pin contentPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin appendPin = new Pin(new PinBoolean(), R.string.edit_text_input_action_append, false, false, true);
    private final transient Pin enterPin = new Pin(new PinBoolean(), R.string.edit_text_input_action_enter, false, false, true);
    private final transient Pin elsePin = new Pin(new PinExecute(), R.string.node_touch_action_else, true);

    public EditTextInputAction() {
        super(ActionType.EDITTEXT_INPUT);
        addPins(nodePin, contentPin, appendPin, enterPin, elsePin);
    }

    public EditTextInputAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(nodePin, contentPin, appendPin, enterPin, elsePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinNode node = getPinValue(runnable, nodePin);
        PinString content = getPinValue(runnable, contentPin);
        PinBoolean append = getPinValue(runnable, appendPin);
        PinBoolean enter = getPinValue(runnable, enterPin);

        NodeInfo nodeInfo = node.getNodeInfo();
        String contentValue = content.getValue();
        boolean result = false;
        if (nodeInfo != null && nodeInfo.usable && nodeInfo.nodeInfo != null && nodeInfo.nodeInfo.isFocusable() && contentValue != null) {
            nodeInfo.nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

            if (append.getValue() && nodeInfo.text != null) {
                contentValue = nodeInfo.text + contentValue;
            }

            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, contentValue);
            result = nodeInfo.nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);

            if (result && enter.getValue() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                nodeInfo.nodeInfo.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER.getId());
            }
        }
        executeNext(runnable, result ? outPin : elsePin);
    }
}
