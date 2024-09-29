package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.task.Task;

public final class VariableInfo {
    private String name;
    private PinObject value;
    private final Task owner;
    private final boolean out;

    public VariableInfo(String name, PinObject value, Task owner, boolean out) {
        this.name = name;
        this.value = value;
        this.owner = owner;
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        owner.removeVar(name);
        this.name = name;
        setValue(value);
    }

    public PinObject getValue() {
        return value;
    }

    public void setValue(PinObject value) {
        this.value = value;
        owner.addVar(name, value);
    }

    public Task getOwner() {
        return owner;
    }

    public boolean isOut() {
        return out;
    }
}
