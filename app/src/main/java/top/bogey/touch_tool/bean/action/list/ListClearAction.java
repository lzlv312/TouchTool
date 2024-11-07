package top.bogey.touch_tool.bean.action.list;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListClearAction extends ListExecuteAction {
    private final transient Pin listPin = new Pin(new PinList());

    public ListClearAction() {
        super(ActionType.LIST_CLEAR);
        addPin(listPin);
    }

    public ListClearAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(listPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinList list = getPinValue(runnable, listPin);
        list.clear();
        executeNext(runnable, outPin);
    }

    @Override
    public List<Pin> getDynamicValueTypePins() {
        return Collections.singletonList(listPin);
    }

}
