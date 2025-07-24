package top.bogey.touch_tool.bean.action.string;

import android.util.Base64;

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.service.TaskRunnable;

public class StringDecodeAction extends CalculateAction {
    private final transient Pin typePin = new NotLinkAblePin(new PinSingleSelect(R.array.string_encode_type), R.string.string_decode_action_type);
    private final transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin resultPin = new Pin(new PinString(), R.string.pin_boolean_result, true);

    public StringDecodeAction() {
        super(ActionType.STRING_DECODE);
        addPins(typePin, textPin, resultPin);
    }

    public StringDecodeAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(typePin, textPin, resultPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinSingleSelect type = typePin.getValue();
        PinString text = getPinValue(runnable, textPin);
        String value = text.getValue();
        if (value == null || value.isEmpty()) return;
        String result = switch (type.getIndex()) {
            case 0 -> {
                try {
                    yield URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    yield value;
                }
            }
            case 1 -> new String(Base64.decode(value, Base64.DEFAULT));
            default -> value;
        };
        resultPin.getValue(PinString.class).setValue(result);
    }
}
