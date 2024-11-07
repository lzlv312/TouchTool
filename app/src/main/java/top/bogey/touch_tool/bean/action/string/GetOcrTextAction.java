package top.bogey.touch_tool.bean.action.string;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.service.ocr.OCR;
import top.bogey.touch_tool.service.ocr.OCRResult;
import top.bogey.touch_tool.utils.DisplayUtil;

public class GetOcrTextAction extends CalculateAction {
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private final transient Pin similarPin = new Pin(new PinInteger(), R.string.pin_number_integer);
    private final transient Pin typePin = new Pin(new PinSingleSelect(R.array.ocr_type), R.string.get_ocr_text_action_type, false, false, true);
    private final transient Pin textPin = new Pin(new PinString(), R.string.get_ocr_text_action_text, true);
    private final transient Pin textArrayPin = new Pin(new PinList(PinType.STRING), R.string.pin_string, true);

    public GetOcrTextAction() {
        super(ActionType.GET_OCR_TEXT);
        addPins(areaPin, similarPin, typePin, textPin, textArrayPin);
    }

    public GetOcrTextAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(areaPin, similarPin, typePin, textPin, textArrayPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinArea area = getPinValue(runnable, areaPin);
        Rect areaRect = area.getValue();
        PinNumber<?> similar = getPinValue(runnable, similarPin);
        PinSingleSelect type = getPinValue(runnable, typePin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        service.tryGetScreenShot(bitmap -> {
            PinList textArray = textArrayPin.getValue(PinList.class);
            Bitmap clipBitmap = DisplayUtil.safeClipBitmap(bitmap, areaRect.left, areaRect.top, areaRect.width(), areaRect.height());
            List<OCRResult> ocrResults = OCR.runOcr(TaskInfoSummary.OcrType.values()[type.getIndex()].name(), clipBitmap);
            StringBuilder builder = new StringBuilder();
            for (OCRResult ocrResult : ocrResults) {
                if (ocrResult.getSimilar() <= similar.intValue()) continue;
                builder.append(ocrResult.getText());
                textArray.add(new PinString(ocrResult.getText()));
            }
            textPin.getValue(PinString.class).setValue(builder.toString());
            runnable.resume();
        });
        runnable.await();
    }
}
