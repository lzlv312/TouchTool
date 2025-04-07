package top.bogey.touch_tool.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.CardCustomBinding;
import top.bogey.touch_tool.ui.blueprint.pin.PinBottomView;
import top.bogey.touch_tool.ui.blueprint.pin.PinLeftView;
import top.bogey.touch_tool.ui.blueprint.pin.PinRightView;
import top.bogey.touch_tool.ui.blueprint.pin.PinTopView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class CustomActionCard extends ActionCard {
    private CardCustomBinding binding;
    private CustomActionCardRecycleViewAdapter adapter;

    public CustomActionCard(Context context, Task task, Action action) {
        super(context, task, action);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init() {
        adapter = new CustomActionCardRecycleViewAdapter(this);
        binding = CardCustomBinding.inflate(LayoutInflater.from(getContext()), this, true);

        CustomActionCardRecycleViewAdapter.DragViewHolderHelper helper = new CustomActionCardRecycleViewAdapter.DragViewHolderHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(helper);

        boolean isOut = action instanceof CustomStartAction;
        if (isOut) {
            binding.outPinBox.setAdapter(adapter);
            touchHelper.attachToRecyclerView(binding.outPinBox);
            ((View)binding.inPinBox.getParent()).setVisibility(GONE);
        } else {
            binding.inPinBox.setAdapter(adapter);
            touchHelper.attachToRecyclerView(binding.inPinBox);
            ((View)binding.outPinBox.getParent()).setVisibility(GONE);
        }

        initCardInfo(null, binding.title, binding.des);
        initEditDesc(binding.editButton, binding.des);
        initDelete(binding.removeButton);
        initLock(binding.lockButton);
        initPosView(binding.position);

        binding.addButton.setOnClickListener(v -> action.addPin(new Pin(new PinString(), 0, isOut, true)));
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void addPinView(Pin pin, int offset) {
        PinView pinView;
        if (pin.isOut()) {
            if (pin.isVertical()) {
                pinView = new PinBottomView(getContext(), this, pin);
                binding.bottomBox.addView(pinView, binding.bottomBox.getChildCount() - offset);
            } else {
                if (pin.isDynamic()) pinView = adapter.addPin(pin);
                else {
                    pinView = new PinRightView(getContext(), this, pin);
                    binding.outBox.addView(pinView, binding.outBox.getChildCount() - offset);
                }
            }
        } else {
            if (pin.isVertical()) {
                pinView = new PinTopView(getContext(), this, pin);
                binding.topBox.addView(pinView, binding.topBox.getChildCount() - offset);
            } else {
                if (pin.isDynamic()) pinView = adapter.addPin(pin);
                else {
                    pinView = new PinLeftView(getContext(), this, pin);
                    binding.inBox.addView(pinView, binding.inBox.getChildCount() - offset);
                }
            }
        }
        pinView.expand(action.getExpandType());
        pinViews.put(pin.getId(), pinView);
    }

    @Override
    public void removePinView(Pin pin) {
        if (pin.isVertical() || !pin.isDynamic()) super.removePinView(pin);
        else adapter.removePin(pin);
    }

    @Override
    public boolean isEmptyPosition(float x, float y) {
        float scale = getScaleX();

        List<MaterialButton> buttons = List.of(binding.lockButton, binding.addButton, binding.removeButton, binding.editButton);
        for (MaterialButton button : buttons) {
            PointF pointF = DisplayUtil.getLocationRelativeToView(button, this);
            float px = pointF.x * scale;
            float py = pointF.y * scale;
            float width = button.getWidth() * scale;
            float height = button.getHeight() * scale;
            if (new RectF(px, py, px + width, py + height).contains(x, y)) return false;
        }
        return super.isEmptyPosition(x, y);
    }

    public void suppressLayout() {
        binding.inPinBox.suppressLayout(true);
        binding.outPinBox.suppressLayout(true);
        postDelayed(() -> {
            binding.inPinBox.suppressLayout(false);
            binding.outPinBox.suppressLayout(false);
        }, 100);
    }
}
