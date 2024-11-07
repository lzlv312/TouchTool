package top.bogey.touch_tool.bean.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.action.start.InnerStartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.service.TaskListener;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class ParallelExecuteAction extends ExecuteAction implements DynamicPinsAction {
    private final static Pin morePin = new Pin(new PinExecute(), R.string.pin_execute, true);

    private final transient Pin countPin = new Pin(new PinInteger(1), R.string.parallel_action_count);
    private final transient Pin timeoutPin = new Pin(new PinInteger(5000), R.string.parallel_action_timeout);

    private final transient Pin secondPin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin addPin = new Pin(new PinAdd(morePin), R.string.pin_add_execute, true);
    private final transient Pin resultPin = new Pin(new PinBoolean(), R.string.pin_boolean_result, true);
    private final transient Pin completePin = new Pin(new PinExecute(), R.string.random_action_complete, true);


    public ParallelExecuteAction() {
        super(ActionType.PARALLEL_LOGIC);
        addPins(countPin, timeoutPin, secondPin, addPin, resultPin, completePin);
    }

    public ParallelExecuteAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(countPin, timeoutPin, secondPin);
        reAddPins(morePin);
        reAddPins(addPin, resultPin, completePin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinNumber<?> count = getPinValue(runnable, countPin);
        PinNumber<?> timeout = getPinValue(runnable, timeoutPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        CountDownLatch latch = new CountDownLatch(count.intValue() > 0 ? count.intValue() : 1);
        List<TaskRunnable> runnableList = new ArrayList<>();
        for (Pin dynamicPin : getDynamicPins()) {
            TaskRunnable taskRunnable = service.runTask(runnable.getTask(), new InnerStartAction(dynamicPin), new TaskListener() {
                @Override
                public void onStart(TaskRunnable runnable) {

                }

                @Override
                public void onExecute(TaskRunnable run, Action action, int progress) {
                    if (runnable.isInterrupt()) run.stop();
                }

                @Override
                public void onCalculate(TaskRunnable runnable, Action action) {

                }

                @Override
                public void onFinish(TaskRunnable runnable) {
                    latch.countDown();
                }
            });
            runnableList.add(taskRunnable);
        }
        try {
            boolean result = latch.await(timeout.intValue(), TimeUnit.MILLISECONDS);
            runnableList.forEach(TaskRunnable::stop);
            resultPin.getValue(PinBoolean.class).setValue(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
            resultPin.getValue(PinBoolean.class).setValue(false);
        }
        executeNext(runnable, completePin);
    }

    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        pins.add(outPin);
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == secondPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
