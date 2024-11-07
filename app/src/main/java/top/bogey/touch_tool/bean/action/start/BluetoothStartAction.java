package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class BluetoothStartAction extends StartAction {
    private final transient Pin devicePin = new Pin(new PinString(), R.string.bluetooth_start_action_device, true);
    private final transient Pin statePin = new Pin(new PinBoolean(), R.string.bluetooth_start_action_state, true);

    public BluetoothStartAction() {
        super(ActionType.BLUETOOTH_START);
        addPins(devicePin, statePin);
    }

    public BluetoothStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(devicePin, statePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        TaskInfoSummary.BluetoothInfo bluetoothInfo = TaskInfoSummary.getInstance().getBluetoothInfo();
        devicePin.getValue(PinString.class).setValue(bluetoothInfo.bluetoothName());
        statePin.getValue(PinBoolean.class).setValue(bluetoothInfo.active());
        super.execute(runnable, pin);
    }
}
