package top.bogey.touch_tool.bean.task;

import java.util.List;

public interface IVariableManager {
    boolean addVariable(Variable variable);

    void removeVariable(String id);

    Variable getVariable(String id);

    List<Variable> getVariables();

    Variable findVariable(String id);
}
