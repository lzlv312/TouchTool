package top.bogey.touch_tool.bean.task;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Future;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.StartAction;

public class TaskRunnable implements Runnable {
    private final Stack<Task> taskStack = new Stack<>();
    private final Stack<Action> actionStack = new Stack<>();

    private final Set<TaskListener> listeners = new HashSet<>();

    private int progress = 0;

    private Future<?> future;
    private boolean interrupt = false;
    private boolean paused;

    public TaskRunnable(Task task, StartAction startAction) {
        taskStack.push(task.copy());
        actionStack.push(startAction.copy());
    }

    @Override
    public void run() {
        try {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onStart(this));
            Action action = getAction();
            while (true) {
                if (action instanceof StartAction start) {
                    if (start.ready(this)) {
                        start.execute(this, null);
                    }
                } else {
                    action.execute(this, null);
                }

                Action nextAction = getAction();
                if (Objects.equals(action, nextAction)) break;
                action = nextAction;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onFinish(this));
        popStack();
        interrupt = true;
    }

    public void pushStack(Task task, Action action) {
        taskStack.push(task);
        actionStack.push(action);
    }

    public void popStack() {
        taskStack.pop();
        actionStack.pop();
        if (taskStack.isEmpty() || actionStack.isEmpty()) stop();
    }

    public Task getTask() {
        return taskStack.peek();
    }

    public Action getAction() {
        return actionStack.peek();
    }

    public Task getStartTask() {
        return taskStack.firstElement();
    }

    public StartAction getStartAction() {
        return (StartAction) actionStack.firstElement();
    }

    public void addListener(TaskListener listener) {
        listeners.add(listener);
    }

    public void addExecuteProgress(Action action) {
        progress++;
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onExecute(this, action, progress));

        StartAction startAction = (StartAction) getStartAction();
        if (startAction == null || startAction.stop(this)) stop();
    }

    public void addCalculateProgress(Action action) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onCalculate(this, action));
    }

    public void stop() {
        if (future != null) future.cancel(true);
        interrupt = true;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        pause(0);
    }

    public synchronized void pause(long ms) {
        if (!paused) {
            try {
                paused = true;
                if (ms == 0) wait();
                else wait(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void resume() {
        if (paused) {
            paused = false;
            notifyAll();
        }
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
