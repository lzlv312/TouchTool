package top.bogey.touch_tool.bean.action.system;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class WriteToClipboardAction extends ExecuteAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);

    public WriteToClipboardAction() {
        super(ActionType.WRITE_TO_CLIPBOARD);
        addPins(textPin);
    }

    public WriteToClipboardAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinString text = getPinValue(runnable, textPin);
        if (text.getValue() != null && !text.getValue().isEmpty()) {
            MainApplication instance = MainApplication.getInstance();
            ClipboardManager manager = (ClipboardManager) instance.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(instance.getString(R.string.app_name), text.getValue());
            manager.setPrimaryClip(clipData);
        }
        executeNext(runnable, outPin);
    }
}
