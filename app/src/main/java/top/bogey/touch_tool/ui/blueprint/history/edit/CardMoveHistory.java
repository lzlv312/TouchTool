package top.bogey.touch_tool.ui.blueprint.history.edit;

import android.graphics.Point;

public class CardMoveHistory extends EditHistory {
    private final String actionId;
    private final Point start;
    private final Point end;

    public CardMoveHistory(String actionId, Point start, Point end) {
        super(EditType.MOVE_CARD);
        this.actionId = actionId;
        this.start = start;
        this.end = end;
    }

    public String getActionId() {
        return actionId;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
