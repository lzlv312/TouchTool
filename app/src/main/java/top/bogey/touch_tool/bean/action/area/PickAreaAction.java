package top.bogey.touch_tool.bean.action.area;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.blueprint.picker.AreaPicker;

public class PickAreaAction extends ExecuteAction {
    private final transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, true);

    public PickAreaAction() {
        super(ActionType.PICK_AREA);
        addPin(areaPin);
    }

    public PickAreaAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(areaPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        AreaPicker.showPicker(result -> {
            if (result != null) {
                areaPin.getValue(PinArea.class).setValue(result);
            }
            runnable.resume();
        });
        runnable.await();
        executeNext(runnable, outPin);
    }
}
