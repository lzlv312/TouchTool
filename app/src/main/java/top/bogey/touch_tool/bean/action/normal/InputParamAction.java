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
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.pin.special_pin.ShowAblePin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.InputParamFloatView;
import top.bogey.touch_tool.utils.EAnchor;

public class InputParamAction extends ExecuteAction {
    private final transient Pin paramPin = new Pin(new PinParam(), R.string.input_param_action_param, true);
    private final transient Pin posTypePin = new NotLinkAblePin(new PinSingleSelect(R.array.float_pos_type, 0), R.string.pin_point, false, false, true);
    private final transient Pin anchorPin = new PosShowablePin(new PinSingleSelect(R.array.anchor, 4), R.string.log_action_show_anchor, false, false, true);
    private final transient Pin showPosPin = new PosShowablePin(new PinPoint(0, 0), R.string.log_action_show_pos, false, false, true);

    public InputParamAction() {
        super(ActionType.INPUT_PARAM);
        addPins(paramPin, posTypePin, anchorPin, showPosPin);
    }

    public InputParamAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(paramPin, true);
        reAddPins(posTypePin, anchorPin, showPosPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinBase value = paramPin.getValue();
        if (value instanceof PinParam) {
            executeNext(runnable, outPin);
            return;
        }

        if (value instanceof PinObject object) {
            if (getTypeValue() == 0) {
                InputParamFloatView.showInputParam(object, result -> runnable.resume());
            } else {
                PinSingleSelect anchor = getPinValue(runnable, anchorPin);
                PinPoint showPos = getPinValue(runnable, showPosPin);
                InputParamFloatView.showInputParam(object, result -> runnable.resume(), EAnchor.values()[anchor.getIndex()], showPos.getValue());
            }
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
        if (origin == paramPin && origin.getLinks().isEmpty()) {
            paramPin.setValue(new PinParam());
        }
        super.onUnLinkedFrom(task, origin, from);
    }

    private int getTypeValue() {
        PinSingleSelect type = posTypePin.getValue();
        return type.getIndex();
    }

    private static class PosShowablePin extends ShowAblePin {
        public PosShowablePin(PinBase value, int titleId, boolean out, boolean dynamic, boolean hide) {
            super(value, titleId, out, dynamic, hide);
        }

        @Override
        public boolean showAble(Task context) {
            InputParamAction action = (InputParamAction) context.getAction(getOwnerId());
            return action.getTypeValue() == 1;
        }
    }
}
