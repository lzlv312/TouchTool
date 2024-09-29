package top.bogey.touch_tool.bean.action.number;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinString;
import top.bogey.touch_tool.bean.task.TaskRunnable;

public class MathExpressionAction extends CalculateAction implements DynamicPinsAction {
    private final transient Pin expressionPin = new Pin(new PinString(PinSubType.AUTO_PIN), R.string.math_expression_action_express);
    private final transient Pin resultPin = new Pin(new PinDouble(), R.string.math_expression_action_result, true);

    public MathExpressionAction() {
        super(ActionType.MATH_EXPRESSION);
        addPins(expressionPin, resultPin);
    }

    public MathExpressionAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(expressionPin, resultPin);
        reAddPins();
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {

    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (start) pins.add(pin);
            if (pin == resultPin) start = true;
        }
        return pins;
    }
}
