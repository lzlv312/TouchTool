package top.bogey.touch_tool.bean.action.list;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_list.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListForeachAction extends ListExecuteAction {
    private final transient Pin breakPin = new Pin(new PinExecute(), R.string.list_foreach_action_break);
    private final transient Pin listPin = new Pin(new PinList(), R.string.pin_list);
    private final transient Pin elementPin = new Pin(new PinObject(PinSubType.DYNAMIC), R.string.pin_object, true);
    private final transient Pin indexPin = new Pin(new PinInteger(), R.string.list_action_index, true);
    private final transient Pin completePin = new Pin(new PinExecute(), R.string.for_loop_action_complete, true);

    private transient boolean isBreak = false;

    public ListForeachAction() {
        super(ActionType.LIST_FOREACH);
        addPins(breakPin, listPin, elementPin, indexPin, completePin);
    }

    public ListForeachAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(breakPin, listPin);
        reAddPin(elementPin, true);
        reAddPins(indexPin, completePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        if (pin == inPin) {
            PinList list = getPinValue(runnable, listPin);
            for (int i = 0; i < list.size(); i++) {
                if (runnable.isInterrupt()) return;
                if (isBreak) break;
                elementPin.setValue(list.get(i));
                indexPin.getValue(PinInteger.class).setValue(i);
                executeNext(runnable, outPin);
            }
            executeNext(runnable, completePin);
        } else {
            isBreak = true;
        }
    }

    @Override
    public void onExecuteNext(TaskRunnable runnable, Pin pin) {
        if (pin == completePin) {
            super.onExecuteNext(runnable, pin);
        }
    }

    @NonNull
    @Override
    public List<Pin> getDynamicTypePins() {
        return Arrays.asList(listPin, elementPin);
    }
}
