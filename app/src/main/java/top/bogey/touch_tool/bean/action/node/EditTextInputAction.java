package top.bogey.touch_tool.bean.action.node;

import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
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
import top.bogey.touch_tool.utils.AppUtil;

public class EditTextInputAction extends ExecuteAction {
    private final transient Pin nodePin = new Pin(new PinNode(), R.string.edit_text_input_action_edit_text, false, false, true);
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
        NodeInfo nodeInfo = null;
        if (nodePin.isLinked()) {
            PinNode node = getPinValue(runnable, nodePin);
            nodeInfo = node.getNodeInfo();
        } else {
            for (AccessibilityNodeInfo window : AppUtil.getWindows(MainApplication.getInstance().getService())) {
                AccessibilityNodeInfo node = findNodeByClass(window, EditText.class);
                if (node != null) {
                    nodeInfo = new NodeInfo(node);
                    break;
                }
            }
        }
        PinString content = getPinValue(runnable, contentPin);
        PinBoolean append = getPinValue(runnable, appendPin);
        PinBoolean enter = getPinValue(runnable, enterPin);

        String contentValue = content.getValue();
        boolean result = false;
        if (nodeInfo != null && nodeInfo.usable && nodeInfo.node != null && nodeInfo.node.isFocusable() && contentValue != null) {
            nodeInfo.node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

            if (append.getValue() && nodeInfo.text != null) {
                contentValue = nodeInfo.text + contentValue;
            }

            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, contentValue);
            result = nodeInfo.node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);

            if (result && enter.getValue() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                nodeInfo.node.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER.getId());
            }
        }
        executeNext(runnable, result ? outPin : elsePin);
    }

    private AccessibilityNodeInfo findNodeByClass(AccessibilityNodeInfo nodeInfo, Class<?> clazz) {
        if (nodeInfo == null) return null;
        CharSequence className = nodeInfo.getClassName();
        if (className != null) {
            Class<?> aClass = null;
            try {
                aClass = Class.forName(className.toString());

            } catch (ClassNotFoundException ignored) {
            }

            if (aClass != null && clazz.isAssignableFrom(aClass)) {
                return nodeInfo;
            }
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                AccessibilityNodeInfo result = findNodeByClass(child, clazz);
                if (result != null) return result;
            }
        }
        return null;
    }
}
