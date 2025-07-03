package top.bogey.touch_tool.utils.float_window_manager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;

import top.bogey.touch_tool.utils.DisplayUtil;

public class FloatAnimator {
    public Animator enter(View view, FloatDockSide side) {
        return makeAnimator(view, side, true);
    }

    public Animator exit(View view, FloatDockSide side) {
        return makeAnimator(view, side, false);
    }

    private Animator makeAnimator(View view, FloatDockSide side, boolean enter) {
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();

        Rect validLocationArea;
        Point size = DisplayUtil.getScreenSize(view.getContext());
        int statusBar = DisplayUtil.getStatusBarHeight(view, params);
        if (DisplayUtil.isPortrait(view.getContext())) validLocationArea = new Rect(0, 0, size.x, size.y - statusBar);
        else validLocationArea = new Rect(0, 0, size.x - statusBar, size.y);

        int width = view.getWidth();
        int height = view.getHeight();

        int leftDistance = params.x - validLocationArea.left;
        int rightDistance = validLocationArea.right - params.x - width;
        int topDistance = params.y - validLocationArea.top;
        int bottomDistance = validLocationArea.bottom - params.y - height;

        boolean isPortrait = false;
        int startValue = 0, endValue = 0;
        switch (side) {
            case LEFT -> {
                startValue = validLocationArea.left - width;
                endValue = params.x;
            }
            case RIGHT -> {
                startValue = validLocationArea.right;
                endValue = params.x;
            }
            case TOP -> {
                isPortrait = true;
                startValue = validLocationArea.top - height;
                endValue = params.y;
            }
            case BOTTOM -> {
                isPortrait = true;
                startValue = validLocationArea.bottom;
                endValue = params.y;
            }
            case HORIZONTAL, DEFAULT -> {
                startValue = leftDistance < rightDistance ? validLocationArea.left - width : validLocationArea.right;
                endValue = params.x;
            }
            case VERTICAL -> {
                isPortrait = true;
                startValue = topDistance < bottomDistance ? validLocationArea.top - height : validLocationArea.bottom;
                endValue = params.y;
            }
            case SIDE -> {
                int minX = Math.min(leftDistance, rightDistance);
                int minY = Math.min(topDistance, bottomDistance);
                if (minX < minY) {
                    startValue = leftDistance < rightDistance ? validLocationArea.left - width : validLocationArea.right;
                    endValue = params.x;
                } else {
                    isPortrait = true;
                    startValue = topDistance < bottomDistance ? validLocationArea.top - height : validLocationArea.bottom;
                    endValue = params.y;
                }
            }
        }

        if (!enter) {
            int temp = startValue;
            startValue = endValue;
            endValue = temp;
        }

        WindowManager manager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        boolean finalIsPortrait = isPortrait;
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            if (finalIsPortrait) params.y = value;
            else params.x = value;
            manager.updateViewLayout(view, params);
        });
        return animator;
    }
}
