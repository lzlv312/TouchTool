package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;

public class ReadFromClipboardAction extends ExecuteAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string, true);

    public ReadFromClipboardAction() {
        super(ActionType.READ_FROM_CLIPBOARD);
        addPin(textPin);
    }

    public ReadFromClipboardAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(textPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        String string = KeepAliveFloatView.getClipboardText();
        textPin.getValue(PinString.class).setValue(string);
        executeNext(runnable, outPin);
    }
}
