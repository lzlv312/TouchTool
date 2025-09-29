package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class SinglePlayView extends PlayFloatItemView implements FloatInterface {
    private final String tag = UUID.randomUUID().toString();

    public static void showActions(List<TaskInfoSummary.ManualExecuteInfo> actions) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            for (View singleShowView : FloatWindow.getViews(SinglePlayView.class)) {
                SinglePlayView itemView = (SinglePlayView) singleShowView;
                ManualStartAction startAction = (ManualStartAction) itemView.getStartAction();
                boolean flag = true;
                for (TaskInfoSummary.ManualExecuteInfo info : actions) {
                    if (info.action() == startAction) {
                        flag = false;
                        actions.remove(info);
                        break;
                    }
                }
                if (flag) {
                    itemView.tryRemoveFromParent();
                }
            }
            for (TaskInfoSummary.ManualExecuteInfo info : actions) {
                new SinglePlayView(keepView.getThemeContext(), info.task(), info.action()).show();
            }
        });
    }

    public SinglePlayView(@NonNull Context context, Task task, StartAction action) {
        super(context, task, action);
        size = SettingSaver.getInstance().getManualPlayViewSingleSize();

        int px = (int) DisplayUtil.dp2px(context, 20 + 8 * size);
        DisplayUtil.setViewWidth(binding.cardLayout, px);
        DisplayUtil.setViewHeight(binding.cardLayout, px);
        DisplayUtil.setViewWidth(binding.title, px);
        DisplayUtil.setViewHeight(binding.title, px);
        binding.circleProgress.setIndicatorSize(px);
    }

    @Override
    public void tryRemoveFromParent() {
        if (playState == PLAY_STATE_STOPED) dismiss();
        else remove = true;
    }

    @Override
    public void onFinish(TaskRunnable runnable) {
        post(() -> {
            stop();
            if (remove) dismiss();
        });
    }

    @Override
    public void show() {
        ManualStartAction action = (ManualStartAction) startAction;
        FloatWindow.with(MainApplication.getInstance().getService())
                .setTag(tag)
                .setLayout(this)
                .setAnchor(action.getAnchor())
                .setLocation(action.getGravity(), action.getShowPos().x, action.getShowPos().y)
                .setDragAble(!action.isLock())
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(tag);
    }
}
