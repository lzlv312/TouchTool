package top.bogey.touch_tool.ui.blueprint.history;

public interface IHistoryOwner {
    void back(HistoryStep step);
    void forward(HistoryStep step);
}
