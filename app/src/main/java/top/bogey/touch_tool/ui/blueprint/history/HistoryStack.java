package top.bogey.touch_tool.ui.blueprint.history;

import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryStack<T>{
    private final Deque<T> stack;
    private final int maxSize;

    public HistoryStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new ArrayDeque<>(maxSize);
    }

    public void push(T t) {
        if (stack.size() >= maxSize) stack.removeFirst();
        stack.addLast(t);
    }

    public T pop() {
        return stack.removeLast();
    }

    public T peek() {
        return stack.peekLast();
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void clear() {
        stack.clear();
    }
}
