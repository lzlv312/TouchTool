package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.Task;

public final class VariableInfo {
    private final Task owner;
    private String name;
    private PinObject value;

    public VariableInfo(Task owner, String name, PinObject value) {
        this.owner = owner;
        this.name = name;
        this.value = value;
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
}
