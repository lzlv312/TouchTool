package top.bogey.touch_tool.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.pin.pins.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinLogString;
import top.bogey.touch_tool.bean.save.TaskSaver;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.ui.custom.ToastFloatView;

public class LoggerAction extends ExecuteAction {
    private final transient Pin log = new Pin(new PinLogString(), R.string.log_action_text);
    private final transient Pin show = new Pin(new PinBoolean(true), R.string.log_action_show, false, false, true);
    private final transient Pin showPos = new Pin(new PinPoint(), R.string.log_action_show_pos, false, false, true);
    private final transient Pin save = new Pin(new PinBoolean(true), R.string.log_action_save, false, false, true);

    public LoggerAction() {
        super(ActionType.LOG);
        addPins(log, show, showPos, save);
    }

    public LoggerAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(log, show, showPos, save);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinObject logObject = getPinValue(runnable, log);
        PinBoolean showLog = getPinValue(runnable, show);
        PinPoint showLogPos = getPinValue(runnable, showPos);
        PinBoolean saveLog = getPinValue(runnable, save);

        if (showLog.getValue()) {
            ToastFloatView.showToast(logObject.toString(), showLogPos.getValue());
        }

        if (saveLog.getValue()) {
            TaskSaver.getInstance().addLog(runnable.getStartTask().getId(), logObject.toString());
        }

        executeNext(runnable, outPin);
    }
}
