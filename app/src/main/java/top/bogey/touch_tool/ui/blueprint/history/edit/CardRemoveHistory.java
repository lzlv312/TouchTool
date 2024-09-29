package top.bogey.touch_tool.ui.blueprint.history.edit;

import top.bogey.touch_tool.bean.action.Action;

public class CardRemoveHistory extends EditHistory{
    private final Action action;

    public CardRemoveHistory(Action action) {
        super(EditType.REMOVE_CARD);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}
