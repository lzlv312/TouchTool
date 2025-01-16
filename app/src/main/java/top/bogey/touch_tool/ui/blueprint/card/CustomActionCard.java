package top.bogey.touch_tool.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;

@SuppressLint("ViewConstructor")
public class CustomActionCard extends ActionCard{
    public CustomActionCard(Context context, Task task, Action action) {
        super(context, task, action);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public void addPinView(Pin pin, int offset) {

    }
}
