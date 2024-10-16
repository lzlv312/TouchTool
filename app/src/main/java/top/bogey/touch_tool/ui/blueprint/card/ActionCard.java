package top.bogey.touch_tool.ui.blueprint.card;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionListener;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.DisplayUtil;

public abstract class ActionCard extends MaterialCardView implements ActionListener {
    protected final Task task;
    protected final Action action;

    protected final Map<String, PinView> pinViews = new HashMap<>();

    public ActionCard(Context context, Task task, Action action) {
        super(context);
        this.task = task;
        this.action = action;

        setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant));
        setStrokeColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorPrimary));
        setStrokeWidth(0);
        setPivotX(0);
        setPivotY(0);

        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        init();

        action.getPins().forEach(this::addPinView);
        action.addListener(this);
    }

    public abstract void init();
    public abstract boolean check();

    public void addPin(Pin pin) {
        action.addPin(pin);
    }

    public void addPin(Pin flag, Pin pin) {
        action.addPin(flag, pin);
    }

    public void addPinView(Pin pin) {
        addPinView(pin, 0);
    }

    /**
     *
     * @param pin
     * @param offset 添加到列表中的位置
     */
    public void addPin(Pin pin, int offset) {
        action.addPin(action.getPins().size() - offset, pin);
    }

    /**
     *
     * @param pin
     * @param offset 添加到上下左右各区域得偏移，不是添加到列表中的位置
     */
    public abstract void addPinView(Pin pin, int offset);

    public void removePin(Pin pin) {
        action.removePin(task, pin);
    }

    public void removePinView(Pin pin) {
        PinView view = pinViews.remove(pin.getId());
        if (view != null) ((ViewGroup) view.getParent()).removeView(view);
    }

    public void updateCardPos(float x, float y) {
        setX(x);
        setY(y);
    }

    public void startFocusAnim() {
        ScaleAnimation animation = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        startAnimation(animation);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStrokeWidth((int) DisplayUtil.dp2px(getContext(), 1));
        } else {
            setStrokeWidth(0);
        }
    }

    public void setDescription(String description) {
        action.setDescription(description);
    }

    public void setExpandType(Action.ExpandType expandType) {
        action.setExpandType(expandType);
        pinViews.forEach((id, pinView) -> pinView.expand(expandType));
    }

    public void expand() {
        int ordinal = action.getExpandType().ordinal();
        Action.ExpandType[] values = Action.ExpandType.values();
        // 下一个枚举
        if (ordinal < values.length - 1) {
            action.setExpandType(values[ordinal + 1]);
        } else {
            action.setExpandType(values[0]);
        }
        pinViews.forEach((id, pinView) -> pinView.expand(action.getExpandType()));
    }

    public PinView getPinView(String pinId) {
        return pinViews.get(pinId);
    }

    public PinView getLinkAblePinView(float x, float y, float scale) {
        for (Map.Entry<String, PinView> entry : pinViews.entrySet()) {
            PinView pinView = entry.getValue();
            if (pinView.getVisibility() != VISIBLE) continue;
            if (!pinView.getPin().linkAble()) continue;

            PointF pos = DisplayUtil.getLocationRelativeToView(pinView, this);
            float px = pos.x * scale;
            float py = pos.y * scale;
            float width = pinView.getWidth() * scale;
            float height = pinView.getHeight() * scale;

            if (!pinView.getPin().isVertical()) {
                float offset = DisplayUtil.dp2px(getContext(), 32 * scale);
                if (pinView.getPin().isOut()) px = px + width - offset;
                width = offset;
            }

            if (new RectF(px, py, px + width, py + height).contains(x, y)) return pinView;
        }

        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public void onPinAdded(Pin pin) {
        List<Pin> pins = new ArrayList<>();
        // 找到pin方向方位一致的所有pin
        for (Pin currPin : action.getPins()) {
            // 方向一致 且 方位一致
            if (pin.isOut() == currPin.isOut() && pin.isVertical() == currPin.isVertical()) {
                pins.add(currPin);
            }
        }
        int index = pins.indexOf(pin);
        addPinView(pin, pins.size() - 1 - index);
    }

    @Override
    public void onPinRemoved(Pin pin) {
        removePinView(pin);
    }

    @Override
    public void onPinChanged(Pin pin) {
        pinViews.forEach((id, pinView) -> pinView.expand(action.getExpandType()));
        check();
    }

    public Task getTask() {
        return task;
    }

    public Action getAction() {
        return action;
    }
}
