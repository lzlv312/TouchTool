package top.bogey.touch_tool.ui.blueprint.history.edit;

public abstract class EditHistory {
    private final EditType type;

    public EditHistory(EditType type) {
        this.type = type;
    }

    public EditType getType() {
        return type;
    }
}
