package top.bogey.touch_tool.bean.action.image;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionCheckResult;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.action.system.SwitchCaptureAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;

public class GetImageAction extends CalculateAction implements SyncAction {
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private final transient Pin useAccPin = new NotLinkAblePin(new PinBoolean(true), R.string.get_image_action_use_accessibility, false, false, true);
    private final transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image, true);

    public GetImageAction() {
        super(ActionType.GET_IMAGE);
        addPins(areaPin, useAccPin, imagePin);
    }

    public GetImageAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(areaPin, useAccPin, imagePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        sync(runnable.getTask());
        PinArea area = getPinValue(runnable, areaPin);
        PinBoolean useAcc = getPinValue(runnable, useAccPin);
        Rect areaRect = area.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();

        if (useAcc.getValue()) {
            boolean pause = service.getScreenByAccessibility(bitmap -> {
                Bitmap clipBitmap = DisplayUtil.safeClipBitmap(bitmap, areaRect.left, areaRect.top, areaRect.width(), areaRect.height());
                if (clipBitmap != null) imagePin.getValue(PinImage.class).setImage(clipBitmap);
                runnable.resume();
            });
            if (pause) runnable.await();
        } else {
            Bitmap bitmap = service.getScreenShotByCapture();
            Bitmap clipBitmap = DisplayUtil.safeClipBitmap(bitmap, areaRect.left, areaRect.top, areaRect.width(), areaRect.height());
            if (clipBitmap != null) imagePin.getValue(PinImage.class).setImage(clipBitmap);
        }
    }

    @Override
    public void check(ActionCheckResult result, Task task) {
        if (!useAccPin.getValue(PinBoolean.class).getValue()) {
            List<Action> actions = task.getActions(SwitchCaptureAction.class);
            if (actions.isEmpty()) {
                result.addResult(ActionCheckResult.ResultType.WARNING, R.string.check_need_capture_warning);
            }
        }
    }

    @Override
    public void sync(Task context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            useAccPin.getValue(PinBoolean.class).setValue(false);
        }
    }
}
