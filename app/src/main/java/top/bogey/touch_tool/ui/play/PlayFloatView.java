package top.bogey.touch_tool.ui.play;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatPlayBinding;
import top.bogey.touch_tool.ui.blueprint.picker.FloatBaseCallback;
import top.bogey.touch_tool.ui.setting.SettingSaver;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatDockSide;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindowHelper;

public class PlayFloatView extends FrameLayout implements FloatInterface {

    private final FloatPlayBinding binding;

    public PlayFloatView(@NonNull Context context) {
        super(context);
        binding = FloatPlayBinding.inflate(LayoutInflater.from(context), this, true);

        binding.dragSpace.setOnClickListener(v -> refreshExpand(true));
        binding.closeButton.setOnClickListener(v -> refreshExpand(false));

        binding.buttonBox.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {

            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                int childCount = binding.buttonBox.getChildCount();
                if (childCount == 0) dismiss();
            }
        });
    }

    public PlayFloatView(Context context, Map<ManualStartAction, Task> actions) {
        this(context);
        setActions(actions);
    }

    public void setActions(Map<ManualStartAction, Task> actions) {
        Set<ManualStartAction> already = new HashSet<>();
        int childCount = binding.buttonBox.getChildCount();
        for (int index = childCount - 1; index >= 0; index--) {
            PlayFloatItemView itemView = (PlayFloatItemView) binding.buttonBox.getChildAt(index);
            // 如果itemView不在actions中，则移除
            boolean flag = true;
            for (Map.Entry<ManualStartAction, Task> entry : actions.entrySet()) {
                ManualStartAction action = entry.getKey();
                Task task = entry.getValue();
                if (itemView.check(task, action)) {
                    already.add(action);
                    flag = false;
                    break;
                }
            }
            itemView.tryRemoveFromParent(flag);
        }

        actions.forEach((action, task) -> {
            if (already.contains(action)) return;
            PlayFloatItemView itemView = new PlayFloatItemView(getContext(), task, action);
            binding.buttonBox.addView(itemView);
        });
    }

    private void refreshExpand(boolean expand) {
        SettingSaver.getInstance().setPlayViewState(expand);
        binding.playButtonBox.setVisibility(expand ? VISIBLE : GONE);
        binding.dragSpace.setVisibility(expand ? GONE : VISIBLE);
        binding.dragSpace.setImageResource(inLeft() ? R.drawable.icon_down : R.drawable.icon_up);

        FloatWindowHelper helper = FloatWindow.getHelper(PlayFloatView.class.getName());
        if (helper != null) helper.viewParent.toDockSide();
    }

    private boolean inLeft() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        Point size = DisplayUtil.getScreenSize(getContext());
        return location[0] < (size.x - getWidth()) / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            refreshExpand(SettingSaver.getInstance().isPlayViewState());
        }
    }

    @Override
    public void show() {
        Point pos = SettingSaver.getInstance().getPlayViewPos();
        FloatWindow.with(MainApplication.getInstance().getService())
                .setTag(PlayFloatView.class.getName())
                .setLayout(this)
                .setDockSide(FloatDockSide.HORIZONTAL)
                .setLocation(EAnchor.CENTER_RIGHT, pos.x, pos.y)
                .setAnchor(EAnchor.CENTER)
                .setCallback(new PlayFloatCallback())
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(PlayFloatView.class.getName());
    }

    private static class PlayFloatCallback extends FloatBaseCallback {
        private float lastX;

        @Override
        public void onShow(String tag) {

        }

        @Override
        public void onDragEnd() {
            super.onDragEnd();
            FloatWindowHelper helper = FloatWindow.getHelper(PlayFloatView.class.getName());
            if (helper != null) {
                Point point = helper.getRelativePoint();
                SettingSaver.getInstance().setPlayViewPos(point);
            }
        }

        @Override
        public boolean onTouch(MotionEvent event) {
            float x = event.getRawX();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN -> {
                    lastX = x;
                    boolean state = SettingSaver.getInstance().isPlayViewState();
                    return !state;
                }

                case MotionEvent.ACTION_MOVE -> {
                    boolean state = SettingSaver.getInstance().isPlayViewState();
                    if (!state) {
                        float dx = x - lastX;

                        int width = (int) DisplayUtil.dp2px(MainApplication.getInstance(), 32);
                        View view = FloatWindow.getView(PlayFloatView.class.getName());
                        if (view != null) width = view.getWidth();

                        if (Math.abs(dx) > width && view instanceof PlayFloatView playFloatView) {
                            playFloatView.refreshExpand(true);
                            return true;
                        }
                    }
                }
            }

            return super.onTouch(event);
        }
    }
}
