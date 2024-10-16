package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class TextToSpeechAction extends ExecuteAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);

    public TextToSpeechAction() {
        super(ActionType.TEXT_TO_SPEECH);
        addPins(textPin);
    }

    public TextToSpeechAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinString text = getPinValue(runnable, textPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        service.speak(text.getValue());
        executeNext(runnable, outPin);
    }
}
