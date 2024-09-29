package top.bogey.touch_tool.ui.blueprint.history.edit;

import top.bogey.touch_tool.bean.pin.pins.PinObject;

public class PinUpdateHistory extends EditHistory {
    private final String actionId;
    private final String pinId;
    private final PinObject from;
    private final PinObject to;

    public PinUpdateHistory(String actionId, String pinId, PinObject from, PinObject to) {
        super(EditType.UPDATE_PIN);
        this.actionId = actionId;
        this.pinId = pinId;
        this.from = from;
        this.to = to;
    }

    public String getActionId() {
        return actionId;
    }

    public String getPinId() {
        return pinId;
    }

    public PinObject getFrom() {
        return from;
    }

    public PinObject getTo() {
        return to;
    }
}
