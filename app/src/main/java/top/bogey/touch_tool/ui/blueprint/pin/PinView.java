package top.bogey.touch_tool.ui.blueprint.pin;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinListener;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.pin_slot.PinSlotView;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidget;
import top.bogey.touch_tool.utils.DisplayUtil;

public abstract class PinView extends FrameLayout implements PinListener {
    protected final ActionCard card;
    protected final Pin pin;
    protected final boolean custom;

    private PinSlotView slotView;

    public PinView(@NonNull Context context, ActionCard card, Pin pin) {
        this(context, card, pin, false);
    }

    public PinView(@NonNull Context context, ActionCard card, Pin pin, boolean custom) {
        super(context);
        this.card = card;
        this.pin = pin;
        this.custom = custom;

        pin.addListener(this);
    }

    protected void init() {
        ViewGroup slotBox = getSlotBox();
        if (slotBox != null) {
            slotBox.removeAllViews();
            PinInfo info = PinInfo.getPinInfo(pin.getValue());
            try {
                Constructor<? extends PinSlotView> constructor = info.getSlot().getConstructor(Context.class, Pin.class);
                slotView = constructor.newInstance(getContext(), pin);
                slotBox.addView(slotView);
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        Button removeButton = getRemoveButton();
        removeButton.setVisibility(pin.isDynamic() ? VISIBLE : GONE);
        removeButton.setOnClickListener(v -> card.removePin(pin));

        refreshPin();
    }

    public abstract Button getRemoveButton();

    public abstract ViewGroup getSlotBox();

    public abstract TextView getTitleView();

    public abstract ViewGroup getWidgetBox();

    public void refreshPin() {
        if (slotView != null) slotView.setLinked(pin.isLinked());

        TextView textView = getTitleView();
        if (textView != null) textView.setText(pin.getTitle());

        ViewGroup widgetBox = getWidgetBox();
        if (widgetBox != null) {
            widgetBox.removeAllViews();

            PinInfo info = PinInfo.getPinInfo(pin.getValue());
            if (info != null) {
                Class<? extends PinWidget<? extends PinBase>> widgetClass = info.getWidget();
                if (widgetClass != null) {
                    try {
                        PinWidget<? extends PinBase> widget = widgetClass.getConstructor(Context.class, ActionCard.class, PinView.class, info.getClazz(), boolean.class).newInstance(getContext(), card, this, pin.getValue(), custom);
                        widgetBox.addView(widget);
                    } catch (Exception e) {
                        Log.d("TAG", "refreshPin: " + info);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void expand(Action.ExpandType expandType) {
        if (pin.isVertical()) return;
        if (pin.showAble(card.getAction())) {
            // 全显示
            if (expandType == Action.ExpandType.FULL) {
                setVisibility(VISIBLE);
            } else {
                // 连接不为空，必须显示
                if (pin.getLinks().isEmpty()) {
                    // 半开的显示非隐藏针脚
                    if (expandType == Action.ExpandType.HALF) {
                        if (pin.isHide()) setVisibility(GONE);
                        else setVisibility(VISIBLE);
                    } else setVisibility(GONE);
                } else {
                    setVisibility(VISIBLE);
                }
            }
        } else {
            setVisibility(GONE);
        }
    }

    public PointF getSlotPosInLayout(float scale) {
        View view = getSlotBox();
        PointF pos = DisplayUtil.getLocationRelativeToView(view, card);
        pos.x *= scale;
        pos.y *= scale;
        if (pin.isVertical()) {
            pos.x += view.getWidth() * scale / 2;
            if (pin.isOut()) {
                pos.y += view.getHeight() * scale;
            }
        } else {
            pos.y += view.getHeight() * scale / 2;
            if (pin.isOut()) {
                pos.x += view.getWidth() * scale;
            }
        }
        pos.offset(card.getX(), card.getY());
        return pos;
    }

    public ActionCard getCard() {
        return card;
    }

    public Pin getPin() {
        return pin;
    }

    @Override
    public void onLinkedTo(Task task, Pin origin, Pin to) {
        post(this::refreshPin);
    }

    @Override
    public void onUnLinkedFrom(Task task, Pin origin, Pin from) {
        post(this::refreshPin);
    }

    @Override
    public void onTypeChanged(Pin origin, Class<? extends PinBase> type) {
        post(this::refreshPin);
    }

    @Override
    public void onValueChanged(Pin origin, PinBase value) {
        post(this::refreshPin);
    }

    @Override
    public void onTitleChanged(Pin origin, String title) {
        post(this::refreshPin);
    }

    @Override
    protected void onDetachedFromWindow() {
        pin.removeListener(this);
        super.onDetachedFromWindow();
    }
}
