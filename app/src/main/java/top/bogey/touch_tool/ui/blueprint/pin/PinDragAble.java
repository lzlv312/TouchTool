package top.bogey.touch_tool.ui.blueprint.pin;

import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.button.MaterialButton;

// 可拖动排序的针脚，各方位针脚都带一个拖动柄，按住拖动柄由卡片开始拖动
public interface PinDragAble {

    // 按住拖动柄即开始拖动，返回 false 保留按钮自身的按压反馈
    default void initDragView(MaterialButton dragButton, PinView pinView, OnStartDragListener listener) {
        if (dragButton == null) return;
        dragButton.setVisibility(listener == null ? View.GONE : View.VISIBLE);
        if (listener == null) return;
        dragButton.setOnTouchListener((v, event) -> {
            // 只在按下时触发，后续的移动、抬起事件交给拖动逻辑处理
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) listener.onStartDrag(pinView);
            return false;
        });
    }

    interface OnStartDragListener {
        void onStartDrag(PinView pinView);
    }
}
