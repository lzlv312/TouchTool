package top.bogey.touch_tool.ui.custom;

import android.graphics.Point;

import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.ui.blueprint.picker.FloatBaseCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindowHelper;

public class ActionFloatViewCallback extends FloatBaseCallback {
    private final String tag;
    private final boolean remember;

    public ActionFloatViewCallback(String tag, boolean remember) {
        this.tag = tag;
        this.remember = remember;
    }

    @Override
    public void onShow(String tag) {

    }

    @Override
    public void onDragEnd() {
        super.onDragEnd();
        FloatWindowHelper helper = FloatWindow.getHelper(tag);
        if (helper != null && remember) {
            Point point = helper.getRelativePoint();
            SettingSaver.getInstance().setChoiceViewPos(point);
        }
    }

    @Override
    public void onDismiss() {

    }
}
