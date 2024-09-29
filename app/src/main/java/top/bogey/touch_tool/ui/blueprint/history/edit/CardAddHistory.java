package top.bogey.touch_tool.ui.blueprint.history.edit;

import top.bogey.touch_tool.bean.action.Action;

public class CardAddHistory extends EditHistory{
    private final Action action;

    public CardAddHistory(Action action) {
        super(EditType.ADD_CARD);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}
