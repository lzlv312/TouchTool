package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.os.Handler;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.card.MaterialCardView;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskListener;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

public class KeepAliveFloatView extends FrameLayout implements FloatInterface, TaskListener {
    private final Handler handler;

    public KeepAliveFloatView(@NonNull Context context) {
        super(context);
        handler = new Handler();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        service.addListener(this);

        MaterialCardView cardView = new MaterialCardView(context);
        float px = DisplayUtil.dp2px(context, 4);
        DisplayUtil.setViewWidth(cardView, (int) px);
        DisplayUtil.setViewHeight(cardView, (int) px);
        cardView.setRadius(px / 2);
        cardView.setStrokeWidth(0);
        cardView.setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant));
        addView(cardView);

//        setAlpha(0);
    }

    private void showMe() {
        boolean startTips = SettingSaver.getInstance().isShowStartTips();
        if (startTips) {
            post(() -> {
                animate().alpha(0.5f);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(this::hideMe, 1500);
            });
        }
    }

    private void hideMe() {
        animate().alpha(0);
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        showMe();
    }

    @Override
    public void onExecute(TaskRunnable runnable, Action action, int progress) {

    }

    @Override
    public void onCalculate(TaskRunnable runnable, Action action) {

    }

    @Override
    public void onFinish(TaskRunnable runnable) {

    }

    @Override
    public void show() {
        MainAccessibilityService service = MainApplication.getInstance().getService();

        FloatWindow.with(service)
                .setLayout(this)
                .setTag(KeepAliveFloatView.class.getName())
                .setSpecial(true)
                .setDragAble(false)
                .setAnimator(null)
                .setLocation(EAnchor.TOP_CENTER, 0, (int) DisplayUtil.dp2px(getContext(), 2))
                .show();
        showMe();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(KeepAliveFloatView.class.getName());
    }
}
