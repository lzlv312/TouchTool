package top.bogey.touch_tool.ui.blueprint.history.edit;

import top.bogey.touch_tool.bean.pin.Pin;

public class PinRemoveHistory extends EditHistory{
    private final String actionId;
    private final Pin pin;

    public PinRemoveHistory(String actionId, Pin pin) {
        super(EditType.REMOVE_PIN);
        this.actionId = actionId;
        this.pin = pin;
    }

    public String getActionId() {
        return actionId;
    }

    public Pin getPin() {
        return pin;
    }
}
