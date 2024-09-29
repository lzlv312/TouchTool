package top.bogey.touch_tool.ui.blueprint.history;

public class HistoryManager {
    private final static int MAX_SIZE = 20;
    private final HistoryStack<HistoryStep> backStack = new HistoryStack<>(MAX_SIZE);
    private final HistoryStack<HistoryStep> forwardStack = new HistoryStack<>(MAX_SIZE);

    public void push(HistoryStep step) {
        backStack.push(step);
        forwardStack.clear();
    }

    public void back(IHistoryOwner historyOwner) {
        if (backStack.isEmpty()) return;
        HistoryStep step = backStack.pop();
        historyOwner.back(step);
        forwardStack.push(step);
    }

    public void forward(IHistoryOwner historyOwner) {
        if (forwardStack.isEmpty()) return;
        HistoryStep step = forwardStack.pop();
        historyOwner.forward(step);
        backStack.push(step);
    }

    public boolean canBack() {
        return !backStack.isEmpty();
    }

    public boolean canForward() {
        return !forwardStack.isEmpty();
    }
}
