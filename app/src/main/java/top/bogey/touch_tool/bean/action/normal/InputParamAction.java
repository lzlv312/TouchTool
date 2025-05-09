package top.bogey.touch_tool.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinParam;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.special_pin.SingleSelectPin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.InputParamFloatView;
import top.bogey.touch_tool.utils.EAnchor;

public class InputParamAction extends ExecuteAction {
    private final transient Pin paramPin = new Pin(new PinParam(), R.string.input_param_action_param, true);
    private final transient Pin anchorPin = new SingleSelectPin(new PinSingleSelect(R.array.anchor, 4), R.string.log_action_show_anchor, false, false, true);
    private final transient Pin showPosPin = new Pin(new PinPoint(-1, -1), R.string.log_action_show_pos, false, false, true);

    public InputParamAction() {
        super(ActionType.INPUT_PARAM);
        addPins(paramPin, anchorPin, showPosPin);
    }

    public InputParamAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(paramPin, true);
        reAddPins(anchorPin, showPosPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinSingleSelect anchor = getPinValue(runnable, anchorPin);
        PinPoint showPos = getPinValue(runnable, showPosPin);

        PinBase value = paramPin.getValue();
        if (value instanceof PinParam) {
            executeNext(runnable, outPin);
            return;
        }
        if (value instanceof PinObject object) {
            InputParamFloatView.showInputParam(object, result -> runnable.resume(), EAnchor.values()[anchor.getIndex()], showPos.getValue());
            runnable.await();
            executeNext(runnable, outPin);
        }
    }

    @Override
    public void onLinkedTo(Task task, Pin origin, Pin to) {
        // 第一条链接才能变更值
        if (origin == paramPin && origin.getLinks().size() == 1) {
            paramPin.setValue(to.getValue().newCopy());
        }
        super.onLinkedTo(task, origin, to);
    }

    @Override
    public void onUnLinkedFrom(Task task, Pin origin, Pin from) {
        // 最后一条链接断开才能重置值
        if (origin == paramPin && origin.getLinks().size() == 0) {
            paramPin.setValue(new PinParam());
        }
        super.onUnLinkedFrom(task, origin, from);
    }
}
