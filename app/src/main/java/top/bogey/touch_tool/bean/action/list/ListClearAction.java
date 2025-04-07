package top.bogey.touch_tool.bean.action.list;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.service.TaskRunnable;

public class ListClearAction extends ListExecuteAction {
    private final transient Pin listPin = new Pin(new PinList(), R.string.pin_list);

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

    @NonNull
    @Override
    public List<Pin> getDynamicValueTypePins() {
        return Collections.singletonList(listPin);
    }

}
