package top.bogey.touch_tool.ui.play;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatPlayBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.ui.blueprint.picker.FloatBaseCallback;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatAnimator;
import top.bogey.touch_tool.utils.float_window_manager.FloatDockSide;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindowHelper;

public class PlayFloatView extends FrameLayout implements FloatInterface {
    public static final int UNIT_PIXEL = 5;

    private static String HIDE_PACKAGE = null;
    private static long HIDE_TIME = 0;

    private final FloatPlayBinding binding;
    private final int padding = SettingSaver.getInstance().getManualPlayViewPadding() * UNIT_PIXEL;

    public static void showActions(Map<ManualStartAction, Task> actions) {
        TaskInfoSummary.PackageActivity packageActivity = TaskInfoSummary.getInstance().getPackageActivity();
        if (packageActivity != null && packageActivity.packageName().equals(HIDE_PACKAGE)) return;
        if (System.currentTimeMillis() < HIDE_TIME) return;
        HIDE_PACKAGE = null;
        HIDE_TIME = 0;

        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            View playFloatView = FloatWindow.getView(PlayFloatView.class.getName());
            if (playFloatView == null) {
                if (!actions.isEmpty()) new PlayFloatView(keepView.getThemeContext(), actions).show();
            } else {
                ((PlayFloatView) playFloatView).setActions(actions);
            }
        });
    }

    public PlayFloatView(@NonNull Context context) {
        super(context);
        binding = FloatPlayBinding.inflate(LayoutInflater.from(context), this, true);
        DisplayUtil.setViewMargin(binding.playButtonBox, padding, 0, padding, 0);
        int size = SettingSaver.getInstance().getManualPlayViewCloseSize();
        DisplayUtil.setViewWidth(binding.dragSpaceButton, (int) DisplayUtil.dp2px(context, 8 + 8 * size));

        binding.dragSpaceButton.setOnClickListener(v -> {
            refreshExpand(true);
            refreshCorner(false);
            toDockSide();
        });

        binding.dragSpaceButton.setOnLongClickListener(v -> {
            hide(SettingSaver.getInstance().getManualPlayHideType());
            return true;
        });

        binding.closeButton.setOnClickListener(v -> {
            refreshExpand(false);
            refreshCorner(false);
            toDockSide();
        });

        binding.closeButton.setOnLongClickListener(v -> {
            hide(SettingSaver.getInstance().getManualPlayHideType());
            return true;
        });

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
        Set<PlayFloatItemView> needRemove = new HashSet<>();

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
                    itemView.setNeedRemove(false);
                    flag = false;
                    break;
                }
            }
            if (flag) needRemove.add(itemView);
        }

        actions.forEach((action, task) -> {
            if (already.contains(action)) return;
            PlayFloatItemView itemView = new PlayFloatItemView(getContext(), task, action);
            binding.buttonBox.addView(itemView);
        });

        needRemove.forEach(PlayFloatItemView::tryRemoveFromParent);
    }

    private void refreshExpand(boolean expand) {
        SettingSaver.getInstance().setManualPlayViewState(expand);
        binding.playButtonBox.setVisibility(expand ? VISIBLE : GONE);
        binding.dragSpace.setVisibility(expand ? GONE : VISIBLE);
        binding.dragSpaceButton.setIconResource(inLeft() ? R.drawable.icon_arrow_down : R.drawable.icon_arrow_up);
        binding.getRoot().animate().alpha(expand ? 1 : 0.3f);
    }

    private void refreshCorner(boolean dragging) {
        boolean expand = SettingSaver.getInstance().getManualPlayViewState();
        float cornerSize = DisplayUtil.dp2px(getContext(), expand ? 16 : 8);
        if (dragging) {
            ShapeAppearanceModel appearanceModel = ShapeAppearanceModel.builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                    .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                    .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                    .build();

            if (padding == 0) binding.playButtonBox.setShapeAppearanceModel(appearanceModel);
            binding.dragSpace.setShapeAppearanceModel(appearanceModel);
        } else {
            if (inLeft()) {
                ShapeAppearanceModel appearanceModel = ShapeAppearanceModel.builder()
                        .setTopLeftCorner(CornerFamily.CUT, 0)
                        .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                        .setBottomLeftCorner(CornerFamily.CUT, 0)
                        .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                        .build();
                if (padding == 0) binding.playButtonBox.setShapeAppearanceModel(appearanceModel);
                binding.dragSpace.setShapeAppearanceModel(appearanceModel);
            } else {
                ShapeAppearanceModel appearanceModel = ShapeAppearanceModel.builder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                        .setTopRightCorner(CornerFamily.CUT, 0)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                        .setBottomRightCorner(CornerFamily.CUT, 0)
                        .build();
                if (padding == 0) binding.playButtonBox.setShapeAppearanceModel(appearanceModel);
                binding.dragSpace.setShapeAppearanceModel(appearanceModel);
            }
        }
    }

    private void toDockSide() {
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
            refreshExpand(SettingSaver.getInstance().getManualPlayViewState());
            toDockSide();
        }
    }

    @Override
    public void show() {
        Point pos = SettingSaver.getInstance().getManualPlayViewPos();
        FloatWindow.with(MainApplication.getInstance().getService())
                .setTag(PlayFloatView.class.getName())
                .setLayout(this)
                .setDockSide(FloatDockSide.HORIZONTAL)
                .setLocation(EAnchor.CENTER_RIGHT, pos.x, pos.y)
                .setCallback(new PlayFloatCallback())
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(PlayFloatView.class.getName());
    }

    public void hide(int hideType) {
        switch (hideType) {
            case 1 -> {
                TaskInfoSummary.PackageActivity packageActivity = TaskInfoSummary.getInstance().getPackageActivity();
                if (packageActivity != null) HIDE_PACKAGE = packageActivity.packageName();
                dismiss();
            }
            case 2 -> {
                HIDE_TIME = System.currentTimeMillis() + 3 * 50 * 1000;
                dismiss();
            }

            case 3 -> new PlayFloatHideChoiceView(getContext(), this::hide).show();

            default -> dismiss();
        }
    }

    private static class PlayFloatCallback extends FloatBaseCallback {

        @Override
        public void onShow(String tag) {

        }

        @Override
        public void onDismiss() {

        }

        @Override
        public void onDrag() {
            super.onDrag();
            View view = FloatWindow.getView(PlayFloatView.class.getName());
            if (view instanceof PlayFloatView playFloatView) {
                playFloatView.refreshCorner(true);
            }
        }

        @Override
        public void onDragEnd() {
            super.onDragEnd();
            FloatWindowHelper helper = FloatWindow.getHelper(PlayFloatView.class.getName());
            if (helper != null) {
                Point point = helper.getRelativePoint();
                SettingSaver.getInstance().setManualPlayViewPos(point);
                PlayFloatView view = (PlayFloatView) FloatWindow.getView(PlayFloatView.class.getName());
                if (view != null) {
                    view.refreshExpand(SettingSaver.getInstance().getManualPlayViewState());
                    view.refreshCorner(false);
                }
            }
        }
    }
}
