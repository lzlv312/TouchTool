package top.bogey.touch_tool.bean.action.number;

import android.widget.Toast;

import com.google.gson.JsonObject;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinNumber;
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
        PinString expression = getPinValue(runnable, expressionPin);
        String expressionStr = expression.getValue();
        if (expressionStr == null || expressionStr.isEmpty()) return;

        ExpressionBuilder builder = new ExpressionBuilder(expressionStr);
        List<Pin> dynamicPins = getDynamicPins();
        dynamicPins.forEach(dynamicPin -> builder.variable(dynamicPin.getTitle()));
        try {
            Expression exp = builder.build();
            dynamicPins.forEach(dynamicPin -> {
                PinNumber<?> value = getPinValue(runnable, dynamicPin);
                exp.setVariable(dynamicPin.getTitle(), value.doubleValue());
            });
            double result = exp.evaluate();
            resultPin.getValue(PinDouble.class).setValue(result);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainApplication.getInstance(), R.string.math_expression_action_tips, Toast.LENGTH_SHORT).show();
        }
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
