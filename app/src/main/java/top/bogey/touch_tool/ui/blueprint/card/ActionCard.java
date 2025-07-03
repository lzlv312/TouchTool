package top.bogey.touch_tool.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.ActionListener;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;

public abstract class ActionCard extends MaterialCardView implements ActionListener {
    protected final Task task;
    protected final Action action;

    protected final Map<String, PinView> pinViews = new HashMap<>();
    private boolean needDelete = false;

    private MaterialTextView posView;

    public ActionCard(Context context, Task task, Action action) {
        super(context);
        this.task = task;
        this.action = action;

        setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant));
        setStrokeColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryVariant));
        setStrokeWidth(1);
        setElevation(8);
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

    public abstract void refreshCardInfo();

    protected void initCardInfo(ShapeableImageView icon, MaterialTextView title, MaterialTextView des) {
        if (icon != null) {
            ActionInfo info = ActionInfo.getActionInfo(action.getType());
            if (info != null) icon.setImageResource(info.getIcon());
        }

        if (title != null) title.setText(action.getTitle());

        if (des != null) {
            des.setText(action.getDescription());
            des.setVisibility((action.getDescription() == null || action.getDescription().isEmpty()) ? GONE : VISIBLE);
        }
    }

    protected void initEditDesc(MaterialButton button, MaterialTextView des) {
        button.setOnClickListener(v -> AppUtil.showEditDialog(getContext(), R.string.action_add_des, action.getDescription(), result -> {
            action.setDescription(result);
            des.setText(result);
            des.setVisibility((result == null || result.isEmpty()) ? GONE : VISIBLE);
        }));
    }

    protected void initDelete(MaterialButton button) {
        button.setOnClickListener(v -> {
            if (needDelete) {
                ((CardLayoutView) getParent()).removeCard(this);
            } else {
                button.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    button.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });
    }

    protected void initCopy(MaterialButton button) {
        button.setOnClickListener(v -> {
            Action copy = action.newCopy();
            ((CardLayoutView) getParent()).addCard(copy);
        });
    }

    protected void initLock(MaterialButton button) {
        button.setIconResource(action.isLocked() ? R.drawable.icon_lock : R.drawable.icon_lock_open);
        if (action.isLocked()) {
            setCardBackgroundColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceContainerHighest));
        } else {
            setCardBackgroundColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
        }

        button.setOnClickListener(v -> {
            action.setLocked(!action.isLocked());
            button.setIconResource(action.isLocked() ? R.drawable.icon_lock : R.drawable.icon_lock_open);
            if (action.isLocked()) {
                setCardBackgroundColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceContainerHighest));
            } else {
                setCardBackgroundColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant));
            }
        });
    }

    protected void initExpand(MaterialButton button) {
        setExpandType(action.getExpandType());
        button.setIconResource(switch (action.getExpandType()) {
            case NONE -> R.drawable.icon_visibility_off;
            case HALF -> R.drawable.icon_visibility_half;
            case FULL -> R.drawable.icon_visibility;
        });
        button.setOnClickListener(v -> {
            expand();
            button.setIconResource(switch (action.getExpandType()) {
                case NONE -> R.drawable.icon_visibility_off;
                case HALF -> R.drawable.icon_visibility_half;
                case FULL -> R.drawable.icon_visibility;
            });
        });
    }

    protected void initPosView(MaterialTextView posView) {
        this.posView = posView;
    }

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
     * @param offset 添加到列表中的位置
     */
    public void addPin(Pin pin, int offset) {
        action.addPin(action.getPins().size() - offset, pin);
    }

    /**
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

    @SuppressLint("SetTextI18n")
    public void updateCardPos(float x, float y) {
        setX(x);
        setY(y);
        if (posView != null) posView.setText(action.getPos().x + "," + action.getPos().y);
    }

    public void startFocusAnim() {
        AlphaAnimation animation = new AlphaAnimation(1f, 0.5f);
        animation.setDuration(200);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        startAnimation(animation);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStrokeWidth((int) DisplayUtil.dp2px(getContext(), 1));
        } else {
            setStrokeWidth(1);
        }
    }

    public void setDescription(String description) {
        action.setDescription(description);
    }

    public void setExpandType(Action.ExpandType expandType) {
        if (expandType == Action.ExpandType.HALF && !action.canExpand()) {
            expandType = Action.ExpandType.FULL;
        }
        action.setExpandType(expandType);
        pinViews.forEach((id, pinView) -> pinView.expand(action.getExpandType()));
    }

    public void expand() {
        Action.ExpandType expandType = action.getExpandType();
        switch (expandType) {
            case NONE -> {
                if (action.canExpand()) {
                    action.setExpandType(Action.ExpandType.HALF);
                } else {
                    action.setExpandType(Action.ExpandType.FULL);
                }
            }
            case HALF -> action.setExpandType(Action.ExpandType.FULL);
            case FULL -> action.setExpandType(Action.ExpandType.NONE);
        }
        pinViews.forEach((id, pinView) -> pinView.expand(action.getExpandType()));
    }

    public PinView getPinView(String pinId) {
        return pinViews.get(pinId);
    }

    public PinView getLinkAblePinView(float x, float y) {
        float scale = getScaleX();

        for (Map.Entry<String, PinView> entry : pinViews.entrySet()) {
            PinView pinView = entry.getValue();
            if (pinView.getVisibility() != VISIBLE) continue;
            if (!pinView.getPin().linkAble()) continue;

            PointF pos = DisplayUtil.getLocationRelativeToView(pinView, this);
            float px = pos.x * scale;
            float py = pos.y * scale;
            float width = pinView.getWidth() * scale;
            float height = pinView.getHeight() * scale;

            // 左右的针脚取32dp的宽度
            if (!pinView.getPin().isVertical()) {
                float offset = DisplayUtil.dp2px(getContext(), 32 * scale);
                if (pinView.getPin().isOut()) px = px + width - offset;
                width = offset;
            }

            if (new RectF(px, py, px + width, py + height).contains(x, y)) return pinView;
        }

        return null;
    }

    public boolean isEmptyPosition(float x, float y) {
        float scale = getScaleX();

        for (Map.Entry<String, PinView> entry : pinViews.entrySet()) {
            PinView pinView = entry.getValue();
            PointF pointF = DisplayUtil.getLocationRelativeToView(pinView, this);
            float px = pointF.x * scale;
            float py = pointF.y * scale;
            float width = pinView.getWidth() * scale;
            float height = pinView.getHeight() * scale;
            if (new RectF(px, py, px + width, py + height).contains(x, y)) return false;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public void onPinAdded(Pin pin, int index) {
        List<Pin> pins = new ArrayList<>();
        // 找到pin方向方位一致的所有pin
        for (Pin currPin : action.getPins()) {
            // 方向一致 且 方位一致
            if (pin.isOut() == currPin.isOut() && pin.isVertical() == currPin.isVertical()) {
                pins.add(currPin);
            }
        }
        int idx = pins.indexOf(pin);
        addPinView(pin, pins.size() - 1 - idx);
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

    public Map<String, PinView> getPinViews() {
        return pinViews;
    }
}
