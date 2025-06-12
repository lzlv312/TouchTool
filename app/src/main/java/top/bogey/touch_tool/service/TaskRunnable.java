package top.bogey.touch_tool.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Future;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.save.LogInfo;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;

public class TaskRunnable implements Runnable {
    private final Stack<Task> taskStack = new Stack<>();
    private final Stack<Action> actionStack = new Stack<>();

    private final Set<TaskListener> listeners = new HashSet<>();

    private final Task task;
    private final StartAction startAction;
    private boolean debug;

    private int progress = 0;

    private Future<?> future;
    private boolean interrupt = false;
    private boolean paused;
    private long pauseTime = -1;

    private final Stack<LogInfo> logStack = new Stack<>();

    public TaskRunnable(Task task, StartAction startAction) {
        this.task = task;
        this.startAction = startAction;
        this.debug = task.hasFlag(Task.FLAG_DEBUG);
    }

    @Override
    public void run() {
        try {
            task.execute(this, startAction, result -> {
                if (result) listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onStart(this));
            });
        } catch (Exception e) {
            e.printStackTrace();

            String errorInfo = e.toString();
            try {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                errorInfo = stringWriter.toString();
            } catch (Exception ignored) {
            }
            Saver.getInstance().addLog(task.getId(), new LogInfo(errorInfo));
        }

        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onFinish(this));
        interrupt = true;
    }

    private synchronized void checkStatus() {
        if (pauseTime >= 0) {
            try {
                paused = true;
                wait(pauseTime);
                pauseTime = -1;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void pushStack(Task task, Action action) {
        taskStack.push(task);
        actionStack.push(action);
        debug = task.hasFlag(Task.FLAG_DEBUG);
    }

    public void popStack() {
        taskStack.pop();
        actionStack.pop();
        debug = taskStack.peek().hasFlag(Task.FLAG_DEBUG);
        if (taskStack.isEmpty() || actionStack.isEmpty()) stop();
    }

    public Task getTask() {
        return taskStack.peek();
    }

    public Action getAction() {
        return actionStack.peek();
    }

    public Task getStartTask() {
        return task;
    }

    public StartAction getStartAction() {
        return startAction;
    }

    public void addListener(TaskListener listener) {
        listeners.add(listener);
    }

    public void addExecuteProgress(Action action) {
        progress++;
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onExecute(this, action, progress));

        StartAction startAction = getStartAction();
        if (startAction == null || startAction.stop(this)) stop();
        else checkStatus();
    }

    public void addCalculateProgress(Action action) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onCalculate(this, action));
        checkStatus();
    }

    public void addDebugLog(Action action, int stackOption) {
        if (debug) addDebugLog(new LogInfo(progress + 1, action, stackOption != 0),  stackOption);
    }

    private void addDebugLog(LogInfo logInfo, int stackOption) {
        switch (stackOption) {
            case -1 -> {
                LogInfo lastLogInfo = logStack.pop();
                addDebugLog(lastLogInfo, 0);
            }
            case 0 -> {
                if (logStack.isEmpty()) Saver.getInstance().addLog(task.getId(), logInfo);
                else logStack.peek().addChild(logInfo);
            }
            case 1 -> logStack.push(logInfo);
        }
    }

    public void stop() {
        if (paused) resume();
        if (future != null) future.cancel(true);
        interrupt = true;
    }

    public void sleep(long time) {
        if (time <= 0) return;
        long remainTime = time;
        long sleepTime = Math.min(remainTime, 100);
        while (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
                remainTime = remainTime - 100;
                sleepTime = Math.min(remainTime, 100);
                checkStatus();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void await() {
        if (paused) return;
        await(0);
    }

    public void await(long ms) {
        if (paused) return;
        pauseTime = ms;
        checkStatus();
    }

    public void pause() {
        pause(0);
    }

    public void pause(long ms) {
        pauseTime = ms;
    }

    public synchronized void resume() {
        if (paused) {
            paused = false;
            this.notifyAll();
        } else {
            pauseTime = -1;
        }
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
