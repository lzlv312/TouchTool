package top.bogey.touch_tool.bean.action.image;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.AppUtil;

public class SaveImageAction extends ExecuteAction {
    private final transient Pin sourcePin = new Pin(new PinImage(), R.string.pin_image);

    public SaveImageAction() {
        super(ActionType.SAVE_IMAGE);
        addPin(sourcePin);
    }

    public SaveImageAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(sourcePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinImage image = getPinValue(runnable, sourcePin);
        AppUtil.saveImage(MainApplication.getInstance(), image.getImage());
        executeNext(runnable, outPin);
    }
}
