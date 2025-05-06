package top.bogey.touch_tool.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.blueprint.pin.PinLeftView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;

@SuppressLint("ViewConstructor")
public class InputParamActionCard extends ActionCard {
    private FrameLayout frameLayout;

    public InputParamActionCard(Context context, Task task, Action action) {
        super(context, task, action);
        setCardBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        setElevation(0);
    }

    @Override
    public void init() {
        frameLayout = new FrameLayout(getContext());
        addView(frameLayout);
    }

    @Override
    public void refreshCardInfo() {

    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void addPinView(Pin pin, int offset) {
        PinView pinView = new PinLeftView(getContext(), this, pin);
        ViewGroup slotBox = pinView.getSlotBox();
        if (slotBox != null) slotBox.setVisibility(GONE);
        TextView titleView = pinView.getTitleView();
        if (titleView != null) titleView.setVisibility(GONE);

        frameLayout.addView(pinView);
        pinViews.put(pin.getId(), pinView);
    }
}
