package top.bogey.touch_tool.bean.action;

import android.graphics.Point;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import top.bogey.touch_tool.bean.base.Identity;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinListener;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.GsonUtil;

public abstract class Action extends Identity implements PinListener {
    private final ActionType type;
    private final List<Pin> pins = new ArrayList<>();

    private ExpandType expandType = ExpandType.HALF;
    private boolean locked = false;
    private Point pos = new Point();

    protected transient List<Pin> tmpPins = new ArrayList<>();
    private final transient Set<ActionListener> listeners = new HashSet<>();

    protected Action(ActionType type) {
        this.type = type;
    }

    protected Action(JsonObject jsonObject) {
        super(jsonObject);
        type = GsonUtil.getAsObject(jsonObject, "type", ActionType.class, null);
        assert type != null;
        expandType = GsonUtil.getAsObject(jsonObject, "expandType", ExpandType.class, ExpandType.HALF);
        locked = GsonUtil.getAsBoolean(jsonObject, "locked", false);
        pos = GsonUtil.getAsObject(jsonObject, "pos", Point.class, new Point());
        tmpPins = GsonUtil.getAsObject(jsonObject, "pins", TypeToken.getParameterized(ArrayList.class, Pin.class).getType(), new ArrayList<>());
    }

    public void addPin(Pin pin) {
        addPin(pins.size(), pin);
    }

    public void addPin(Pin flag, Pin pin) {
        int index = pins.indexOf(flag);
        if (index == -1) return;
        addPin(index, pin);
    }

    public void addPin(int index, Pin pin) {
        if (pin == null) return;
        if (getPinById(pin.getId()) != null) return;
        pins.add(index, pin);
        pin.setOwnerId(getId());
        pin.addListener(this);
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinAdded(pin));
    }

    public void addPins(Pin... pins) {
        for (Pin pin : pins) addPin(pin);
    }

    // 从临时列表中获取一个类似的针脚加入到正式列表中
    public void reAddPin(Pin def) {
        if (!tmpPins.isEmpty()) {
            Pin tmpPin = tmpPins.get(0);
            if (def.isSameClass(tmpPin)) {
                tmpPins.remove(0);
                def.setPin(tmpPin);
            }
        }
        addPin(def);
    }

    // 从临时列表中获取一个类似的针脚加入到正式列表中，如果没有类似的，就根据classes找其他类似的，这是一个纠错机制
    public void reAddPin(Pin def, Class<? extends PinBase>... classes) {
        if (!tmpPins.isEmpty()) {
            Pin tmpPin = tmpPins.get(0);
            for (Class<? extends PinBase> aClass : classes) {
                if (def.isSameClass(aClass)) {
                    tmpPins.remove(0);
                    def.setPin(tmpPin);
                    break;
                }
            }
        }

        addPin(def);
    }

    // 从临时列表中根据type获取一个类似的针脚加入到正式列表中，如果没有类似的，就根据type创建一个
    public void reAddPin(Pin def, PinType type) {
        PinInfo info = PinInfo.getPinInfo(type);
        if (info != null) {
            if (!tmpPins.isEmpty()) {
                Pin tmpPin = tmpPins.get(0);
                if (def.isSameClass(info.getClazz())) {
                    tmpPins.remove(0);
                    def.setPin(tmpPin);
                }
            }
        }

        addPin(def);
    }

    public void reAddPins(Pin... pins) {
        for (Pin pin : pins) reAddPin(pin);
    }

    // 从临时列表获取一系列类似针脚加入到正式列表中，直到出现添加针脚为止
    public void reAddPins(Pin def) {
        if (tmpPins.isEmpty()) {
            return;
        }

        Pin tmpPin = tmpPins.get(0);
        while (!tmpPin.isSameClass(PinAdd.class)) {
            if (def.isSameClass(tmpPin)) {
                Pin copy = def.newCopy();
                if (copy.getTitleId() == 0) {
                    copy.setTitle(tmpPin.getTitle());
                }
                reAddPin(copy);
            } else {
                tmpPins.remove(0);
            }
            if (tmpPins.isEmpty()) break;
            tmpPin = tmpPins.get(0);
        }
    }

    // 从临时列表根据type获取一系列类似针脚加入到正式列表中，直到出现添加针脚为止
    public void reAddPins(Pin def, PinType type) {
        if (tmpPins.isEmpty()) {
            return;
        }
        PinInfo info = PinInfo.getPinInfo(type);
        if (info == null) return;

        Pin tmpPin = tmpPins.get(0);
        while (!tmpPin.isSameClass(PinAdd.class)) {
            if (tmpPin.isSameClass(info.getClazz())) {
                Pin copy = def.newCopy();
                if (copy.getTitleId() == 0) copy.setTitle(tmpPin.getTitle());
                reAddPin(copy, type);
            } else {
                tmpPins.remove(0);
            }
            if (tmpPins.isEmpty()) break;
            tmpPin = tmpPins.get(0);
        }
    }

    public void removePin(Pin pin) {
        if (pin == null) return;
        if (pins.remove(pin)) {
            pin.removeListener(this);
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinRemoved(pin));
        }
    }

    public void removePin(Task context, Pin pin) {
        pin.clearLinks(context);
        removePin(pin);
    }

    public Pin getPinById(String id) {
        return pins.stream().filter(pin -> pin.getId().equals(id)).findFirst().orElse(null);
    }

    public Pin getPinByUid(String uid) {
        return pins.stream().filter(pin -> pin.getUid().equals(uid)).findFirst().orElse(null);
    }

    public Pin findConnectToAblePin(Pin pin) {
        return pins.stream().filter(p -> p.linkAble() && p.linkAble(pin)).findFirst().orElse(null);
    }

    public boolean withCapture() {
        return false;
    }

    public void addListener(ActionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ActionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Action copy() {
        return GsonUtil.copy(this, Action.class);
    }

    @Override
    public Action newCopy() {
        Action copy = copy();
        copy.setId(UUID.randomUUID().toString());
        copy.setLocked(false);
        copy.setExpandType(ExpandType.HALF);
        copy.getPins().forEach(pin -> {
            pin.setId(UUID.randomUUID().toString());
            pin.setOwnerId(copy.getId());
            pin.getLinks().clear();
        });

        copy.pos.offset(1, 1);
        return copy;
    }

    @Override
    public String getTitle() {
        ActionInfo info = ActionInfo.getActionInfo(getType());
        if (info == null) return "";
        return info.getTitle();
    }

    public abstract void execute(TaskRunnable runnable, Pin pin);

    public void executeNext(TaskRunnable runnable, Pin pin) {
        runnable.addExecuteProgress(this);

        if (runnable.isInterrupt()) return;
        if (!pin.isOut()) return;

        Pin linkedPin = pin.getLinkedPin(runnable.getTask());
        if (linkedPin == null) return;
        Action action = runnable.getTask().getAction(linkedPin.getOwnerId());
        if (action == null) return;
        action.execute(runnable, linkedPin);
    }

    public abstract void calculate(TaskRunnable runnable, Pin pin);

    public abstract void resetReturnValue();

    public <T extends PinObject> T getPinValue(TaskRunnable runnable, Pin pin) {
        if (pin.isOut()) {
            resetReturnValue();
            calculate(runnable, pin);
            runnable.addCalculateProgress(this);
            return pin.getValue();
        }

        if (pin.isLinked()) {
            Pin linkedPin = pin.getLinkedPin(runnable.getTask());
            if (linkedPin != null) {
                Action action = runnable.getTask().getAction(linkedPin.getOwnerId());
                if (action != null) {
                    T pinValue = action.getPinValue(runnable, linkedPin);
                    pin.setValue(pinValue);
                    return pinValue;
                }
            }
        }
        return pin.getValue();
    }

    public void check(ActionCheckResult result, Task task) {

    }

    @Override
    public void onLinkedTo(Task task, Pin origin, Pin to) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(origin));
    }

    @Override
    public void onUnLinkedFrom(Task task, Pin origin, Pin from) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(origin));
    }

    @Override
    public void onTypeChanged(Pin origin, Class<? extends PinBase> type) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(origin));
    }

    @Override
    public void onValueChanged(Pin origin, PinBase value) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(origin));
    }

    @Override
    public void onTitleChanged(Pin origin, String title) {
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(origin));
    }

    public ActionType getType() {
        return type;
    }

    public List<Pin> getPins() {
        return pins;
    }

    public ExpandType getExpandType() {
        return expandType;
    }

    public void setExpandType(ExpandType expandType) {
        this.expandType = expandType;
    }

    public boolean canExpand() {
        for (Pin pin : getPins()) {
            if (pin.isHide()) return true;
        }
        return false;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public void setPos(int x, int y) {
        pos.set(x, y);
    }

    public enum ExpandType {
        NONE, HALF, FULL
    }

    public static class ActionDeserializer implements JsonDeserializer<Action> {
        @Override
        public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ActionType type = GsonUtil.getAsObject(jsonObject, "type", ActionType.class, null);
            assert type != null;
            ActionInfo info = ActionInfo.getActionInfo(type);
            if (info == null) return null;
            try {
                Constructor<? extends Action> constructor = info.getClazz().getConstructor(JsonObject.class);
                return constructor.newInstance(jsonObject);
            } catch (Exception e) {
                Log.d("TAG", "deserialize action: " + info);
                Log.d("TAG", "deserialize action json: " + json);
                e.printStackTrace();
                return null;
            }
        }
    }
}
