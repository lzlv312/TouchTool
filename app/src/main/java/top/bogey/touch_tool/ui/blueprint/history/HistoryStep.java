package top.bogey.touch_tool.ui.blueprint.history;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.ui.blueprint.history.edit.EditHistory;

public class HistoryStep {
    private final List<EditHistory> history = new ArrayList<>();

    public HistoryStep(EditHistory editHistory) {
        history.add(editHistory);
    }

    public HistoryStep(List<EditHistory> history) {
        this.history.addAll(history);
    }

    public List<EditHistory> getHistory() {
        return history;
    }
}
