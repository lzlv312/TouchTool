package top.bogey.touch_tool.bean.action.image;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.concurrent.atomic.AtomicBoolean;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;

public class GetImageAction extends CalculateAction {
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private final transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image, true);

    public GetImageAction() {
        super(ActionType.GET_IMAGE);
        addPins(areaPin, imagePin);
    }

    public GetImageAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(areaPin, imagePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinArea area = getPinValue(runnable, areaPin);
        Rect areaRect = area.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        AtomicBoolean needPaused = new AtomicBoolean(true);
        service.getScreenShot(bitmap -> {
            Bitmap clipBitmap = DisplayUtil.safeClipBitmap(bitmap, areaRect.left, areaRect.top, areaRect.width(), areaRect.height());
            if (clipBitmap != null) imagePin.getValue(PinImage.class).setImage(clipBitmap);
            needPaused.set(false);
            runnable.resume();
        });
        if (needPaused.get()) runnable.await();
    }
}
