package top.bogey.touch_tool.ui.blueprint.history.edit;

import java.util.Map;

public class PinLinkHistory extends EditHistory {
    private final String actionId;
    private final String pinId;
    private final Map<String, String> from;
    private final Map<String, String> to;

    public PinLinkHistory(String actionId, String pinId, Map<String, String> from, Map<String, String> to) {
        super(EditType.LINK_PIN);
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

    public Map<String, String> getFrom() {
        return from;
    }

    public Map<String, String> getTo() {
        return to;
    }
}
