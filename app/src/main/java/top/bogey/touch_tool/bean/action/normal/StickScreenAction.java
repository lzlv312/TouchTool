package top.bogey.touch_tool.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinLogString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.special_pin.SingleSelectPin;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.StickScreenFloatView;
import top.bogey.touch_tool.utils.EAnchor;

public class StickScreenAction extends ExecuteAction {
    private final transient Pin valuePin = new Pin(new PinLogString(), R.string.pin_object);
    private final transient Pin showPosPin = new Pin(new PinPoint(), R.string.stick_screen_action_pos, false, false, true);
    private final transient Pin anchorPin = new SingleSelectPin(new PinSingleSelect(R.array.anchor, 4), R.string.stick_screen_action_anchor, false, false, true);
    private final transient Pin idPin = new Pin(new PinString(), R.string.stick_screen_action_id, true);

    public StickScreenAction() {
        super(ActionType.STICK);
        addPins(valuePin, showPosPin, anchorPin, idPin);
    }

    public StickScreenAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(valuePin, showPosPin, anchorPin, idPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinObject value = getPinValue(runnable, valuePin);
        PinPoint showPos = getPinValue(runnable, showPosPin);
        PinSingleSelect anchor = getPinValue(runnable, anchorPin);
        PinString id = idPin.getValue();

        String tag = StickScreenFloatView.showStick(value, EAnchor.values()[anchor.getIndex()], showPos.getValue());
        id.setValue(tag);

        executeNext(runnable, outPin);
    }
}
