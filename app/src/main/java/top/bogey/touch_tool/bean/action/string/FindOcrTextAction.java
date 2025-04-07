package top.bogey.touch_tool.bean.action.string;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.logic.FindExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.special_pin.SingleSelectPin;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.ocr.OCR;
import top.bogey.touch_tool.service.ocr.OcrResult;
import top.bogey.touch_tool.utils.AppUtil;

public class FindOcrTextAction extends FindExecuteAction {
    private final transient Pin sourcePin = new Pin(new PinImage(), R.string.pin_image);
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, false, false, true);
    private final transient Pin typePin = new SingleSelectPin(new PinSingleSelect(R.array.ocr_type), R.string.find_ocr_text_action_type, false, false, true);
    private final transient Pin resultAreaPin = new Pin(new PinArea(), R.string.pin_area, true);
    private final transient Pin resultTextPin = new Pin(new PinString(), R.string.pin_string, true);

    public FindOcrTextAction() {
        super(ActionType.FIND_OCR_TEXT);
        addPins(sourcePin, textPin, areaPin, typePin, resultAreaPin, resultTextPin);
    }

    public FindOcrTextAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(sourcePin, textPin, areaPin, typePin, resultAreaPin, resultTextPin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinImage source = getPinValue(runnable, sourcePin);
        PinObject text = getPinValue(runnable, textPin);
        PinArea area = getPinValue(runnable, areaPin);
        PinSingleSelect type = getPinValue(runnable, typePin);

        String value = text.toString();
        if (value.isEmpty()) return false;
        Pattern pattern = AppUtil.getPattern(value);
        if (pattern == null) return false;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return false;

        Bitmap bitmap = source.getImage();
        List<OcrResult> ocrResults = OCR.runOcr(TaskInfoSummary.OcrType.values()[type.getIndex()].name(), bitmap);
        for (OcrResult ocrResult : ocrResults) {
            if (Rect.intersects(ocrResult.getArea(), area.getValue())) {
                if (pattern.matcher(ocrResult.getText()).find()) {
                    resultAreaPin.getValue(PinArea.class).setValue(ocrResult.getArea());
                    resultTextPin.getValue(PinString.class).setValue(ocrResult.getText());
                    return true;
                }
            }
        }

        return false;
    }
}
