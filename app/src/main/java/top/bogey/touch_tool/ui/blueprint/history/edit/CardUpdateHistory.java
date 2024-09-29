package top.bogey.touch_tool.ui.blueprint.history.edit;

public class CardUpdateHistory extends EditHistory {
    private final String actionId;
    private final String from;
    private final String to;

    public CardUpdateHistory(String actionId, String from, String to) {
        super(EditType.UPDATE_CARD);
        this.actionId = actionId;
        this.from = from;
        this.to = to;
    }

    public String getActionId() {
        return actionId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
