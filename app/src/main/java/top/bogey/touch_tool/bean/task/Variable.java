package top.bogey.touch_tool.bean.task;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.UUID;

import top.bogey.touch_tool.bean.base.Identity;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.utils.GsonUtil;

public class Variable extends Identity {
    transient Task owner;
    private PinObject value;

    public Variable(PinObject value) {
        super();
        this.value = value;
    }

    public Variable(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsObject(jsonObject, "value", PinObject.class, new PinObject());
    }

    @Override
    public Variable copy() {
        return GsonUtil.copy(this, Variable.class);
    }

    @Override
    public Variable newCopy() {
        Variable copy = copy();
        copy.setId(UUID.randomUUID().toString());
        return copy;
    }

    public PinObject getValue() {
        return value;
    }

    public void setValue(PinObject value) {
        this.value = value;
    }

    public VariableType getType() {
        if (value instanceof PinList) return VariableType.LIST;
        if (value instanceof PinMap) return VariableType.MAP;
        return VariableType.NORMAL;
    }

    public boolean setType(VariableType type) {
        if (type == getType()) return false;
        PinInfo pinInfo = getPinInfo();
        switch (type) {
            case NORMAL -> setValue((PinObject) pinInfo.newInstance());
            case LIST -> {
                PinList pinList = new PinList();
                pinList.setValueType(pinInfo.getType());
                setValue(pinList);
            }
            case MAP -> {
                PinMap pinMap = new PinMap();
                pinMap.setKeyType(pinInfo.getType());
                setValue(pinMap);
            }
        }
        return true;
    }

    public PinInfo getPinInfo() {
        return getKeyPinInfo();
    }

    public PinInfo getKeyPinInfo() {
        if (value == null) return null;
        VariableType type = getType();
        switch (type) {
            case NORMAL -> {
                return PinInfo.getPinInfo(value.getType(), value.getSubType());
            }
            case LIST -> {
                PinList pinList = (PinList) value;
                return PinInfo.getPinInfo(pinList.getValueType());
            }
            case MAP -> {
                PinMap pinMap = (PinMap) value;
                return PinInfo.getPinInfo(pinMap.getKeyType());
            }
        }
        return null;
    }

    public PinInfo getValuePinInfo() {
        if (value == null) return null;
        VariableType type = getType();
        if (type == VariableType.MAP) {
            PinMap pinMap = (PinMap) value;
            return PinInfo.getPinInfo(pinMap.getValueType());
        }
        return null;
    }

    public void setPinInfo(PinInfo pinInfo) {
        setKeyPinInfo(pinInfo);
    }

    public void setKeyPinInfo(PinInfo pinInfo) {
        switch (getType()) {
            case NORMAL -> setValue((PinObject) pinInfo.newInstance());
            case LIST -> {
                PinList pinList = (PinList) value;
                pinList.setValueType(pinInfo.getType());
                pinList.reset();
            }
            case MAP -> {
                PinMap pinMap = (PinMap) value;
                pinMap.setKeyType(pinInfo.getType());
                pinMap.reset();
            }
        }
    }

    public void setValuePinInfo(PinInfo pinInfo) {
        if (getType() == VariableType.MAP) {
            PinMap pinMap = (PinMap) value;
            pinMap.setValueType(pinInfo.getType());
        }
    }

    public void save() {
        if (owner != null) owner.save();
        else Saver.getInstance().saveVar(this);
    }

    public Task getOwner() {
        return owner;
    }

    public enum VariableType {
        NORMAL, LIST, MAP
    }
}
