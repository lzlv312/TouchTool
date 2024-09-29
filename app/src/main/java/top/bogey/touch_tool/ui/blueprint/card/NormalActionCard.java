package top.bogey.touch_tool.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionCheckResult;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.CardNormalBinding;
import top.bogey.touch_tool.ui.blueprint.pin.PinBottomView;
import top.bogey.touch_tool.ui.blueprint.pin.PinLeftView;
import top.bogey.touch_tool.ui.blueprint.pin.PinRightView;
import top.bogey.touch_tool.ui.blueprint.pin.PinTopView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class NormalActionCard extends ActionCard{
    private CardNormalBinding binding;

    public NormalActionCard(Context context, Task task, Action action) {
        super(context, task, action);
    }

    @Override
    public void init() {
        binding = CardNormalBinding.inflate(LayoutInflater.from(getContext()), this, true);

        binding.title.setText(action.getTitle());

        ActionInfo info = ActionInfo.getActionInfo(action.getType());
        if (info != null) binding.icon.setImageResource(info.getIcon());

        binding.des.setText(action.getDescription());
        binding.des.setVisibility((action.getDescription() == null || action.getDescription().isEmpty()) ? GONE : VISIBLE);

        binding.editButton.setOnClickListener(v -> AppUtil.showEditDialog(getContext(), R.string.action_add_des, action.getDescription(), result -> {
            action.setDescription(result);
            binding.des.setText(result);
            binding.des.setVisibility((result == null || result.isEmpty()) ? GONE : VISIBLE);
        }));
    }

    @Override
    public boolean check() {
        ActionCheckResult result = new ActionCheckResult();
        action.check(result, task);
        ActionCheckResult.Result importantResult = result.getImportantResult();
        if (importantResult != null) {
            binding.errorText.setVisibility(VISIBLE);
            binding.errorText.setText(importantResult.msg());
            binding.errorText.setBackgroundColor(DisplayUtil.getAttrColor(getContext(), importantResult.type() == ActionCheckResult.ResultType.ERROR ? com.google.android.material.R.attr.colorError : com.google.android.material.R.attr.colorErrorContainer));
            binding.errorText.setTextColor(DisplayUtil.getAttrColor(getContext(), importantResult.type() == ActionCheckResult.ResultType.ERROR ? com.google.android.material.R.attr.colorOnError : com.google.android.material.R.attr.colorOnErrorContainer));
            return false;
        } else {
            binding.errorText.setVisibility(GONE);
            return true;
        }
    }

    @Override
    public void addPinView(Pin pin, int offset) {
        PinView pinView;
        if (pin.isOut()) {
            if (pin.isVertical()) {
                pinView = new PinBottomView(getContext(), this, pin);
                binding.bottomBox.addView(pinView, binding.bottomBox.getChildCount() - offset);
            } else {
                pinView = new PinRightView(getContext(), this, pin);
                binding.outBox.addView(pinView, binding.outBox.getChildCount() - offset);
            }
        } else {
            if (pin.isVertical()) {
                pinView = new PinTopView(getContext(), this, pin);
                binding.topBox.addView(pinView, binding.topBox.getChildCount() - offset);
            } else {
                pinView = new PinLeftView(getContext(), this, pin);
                binding.inBox.addView(pinView, binding.inBox.getChildCount() - offset);
            }
        }
        pinView.expand(action.getExpandType());
        pinViews.put(pin.getId(), pinView);
    }
}
