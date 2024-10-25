package top.bogey.touch_tool.bean.action.string;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.logic.FindExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.service.ocr.OCR;
import top.bogey.touch_tool.service.ocr.OCRResult;
import top.bogey.touch_tool.utils.AppUtil;

public class FindOcrTextAction extends FindExecuteAction {
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, false, false, true);
    private final transient Pin typePin = new Pin(new PinSingleSelect(R.array.ocr_type), R.string.get_ocr_text_action_type, false, false, true);
    private final transient Pin resultAreaPin = new Pin(new PinArea(), R.string.pin_area, true);
    private final transient Pin resultTextPin = new Pin(new PinString(), R.string.pin_string, true);

    public FindOcrTextAction() {
        super(ActionType.FIND_OCR_TEXT);
        addPins(textPin, areaPin, typePin, resultAreaPin, resultTextPin);
    }

    public FindOcrTextAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(textPin, areaPin, typePin, resultAreaPin, resultTextPin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinString text = getPinValue(runnable, textPin);
        PinArea area = getPinValue(runnable, areaPin);
        PinSingleSelect type = getPinValue(runnable, typePin);

        String value = text.getValue();
        if (value == null || value.isEmpty()) return false;
        Pattern pattern = AppUtil.getPattern(value);
        if (pattern == null) return false;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return false;

        AtomicBoolean result = new AtomicBoolean(false);
        service.tryGetScreenShot(bitmap -> {
            List<OCRResult> ocrResults = OCR.runOcr(TaskInfoSummary.OcrType.values()[type.getIndex()].name(), bitmap);
            for (OCRResult ocrResult : ocrResults) {
                if (Rect.intersects(ocrResult.getArea(), area.getValue())) {
                    if (pattern.matcher(ocrResult.getText()).find()) {
                        resultAreaPin.getValue(PinArea.class).setValue(ocrResult.getArea());
                        resultTextPin.getValue(PinString.class).setValue(ocrResult.getText());
                        result.set(true);
                        break;
                    }
                }
            }
            runnable.resume();
        });
        runnable.pause();

        return result.get();
    }
}
