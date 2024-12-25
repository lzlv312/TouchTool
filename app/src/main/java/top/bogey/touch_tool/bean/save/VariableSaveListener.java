package top.bogey.touch_tool.bean.save;

import top.bogey.touch_tool.bean.task.Variable;

public interface VariableSaveListener {
    void onCreate(Variable var);

    void onUpdate(Variable var);

    void onRemove(Variable var);
}
