package top.bogey.touch_tool.bean.action.task;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionListener;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;

public class CustomEndAction extends Action implements DynamicPinsAction, SyncAction {
    private final transient Pin justCallPin = new NotLinkAblePin(new PinBoolean(false), R.string.execute_task_action_just_cal);

    private final static SyncActionListener LISTENER = new SyncActionListener();

    public CustomEndAction() {
        super(ActionType.CUSTOM_END);
        setExpandType(ExpandType.FULL);
        addPin(justCallPin);
        setPos(0, 30);
    }

    public CustomEndAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(justCallPin);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        Map<String, PinObject> params = new HashMap<>();
        for (Pin p : getDynamicPins()) {
            if (!p.isOut() && !p.isVertical()) {
                PinObject value = getPinValue(runnable, p);
                params.put(p.getUid(), value);
            }
        }
        onExecuteNext(runnable, null);
        runnable.getTask().executeNext(runnable, pin, params);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {

    }


    @Override
    public List<Pin> getDynamicPins() {
        List<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (start) pins.add(pin);
            if (pin == justCallPin) start = true;
        }
        return pins;
    }

    public boolean isJustCall() {
        PinBoolean justCall = justCallPin.getValue();
        return justCall.getValue();
    }

    @Override
    public void sync(Task context) {
        LISTENER.setContext(context);
        addListener(LISTENER);
    }

    @Override
    public Pin findConnectToAblePin(Pin pin) {
        return getPins().stream().filter(p -> p.linkAble() && p.linkAble(pin.getValue())).findFirst().orElse(null);
    }

    private static class SyncActionListener implements ActionListener {
        private Task context;
        private boolean doing = false;

        @Override
        public void onPinAdded(Pin pin, int index) {
            if (doing) return;
            doing = true;
            Action action = context.getAction(pin.getOwnerId());
            if (action == null) return;
            List<Action> actions = context.getActions(action.getUid());
            for (Action act : actions) {
                if (act == action) continue;
                act.addPin(index, pin.newCopy());
            }
            doing = false;
        }

        @Override
        public void onPinRemoved(Pin pin) {
            if (doing) return;
            doing = true;
            Action action = context.getAction(pin.getOwnerId());
            if (action == null) return;
            List<Action> actions = context.getActions(action.getUid());
            for (Action act : actions) {
                if (act == action) continue;
                Pin pinByUid = act.getPinByUid(pin.getUid());
                if (pinByUid == null) continue;
                act.removePin(context, pinByUid);
            }
            doing = false;
        }

        @Override
        public void onPinChanged(Pin pin) {
            if (doing) return;
            doing = true;
            Action action = context.getAction(pin.getOwnerId());
            if (action == null) return;
            List<Action> actions = context.getActions(action.getUid());
            for (Action act : actions) {
                if (act == action) continue;
                Pin pinByUid = act.getPinByUid(pin.getUid());
                if (pinByUid == null) continue;
                pinByUid.setTitle(pin.getTitle());
                if (!pinByUid.isSameClass(pin)) pinByUid.setValue(pin.getValue().copy());

                // 仅执行针脚同步
                if (pinByUid.getTitleId() == R.string.execute_task_action_just_cal) {
                    pinByUid.setValue(pin.getValue().copy());
                }
            }
            doing = false;
        }

        public void setContext(Task context) {
            this.context = context;
        }
    }
}
