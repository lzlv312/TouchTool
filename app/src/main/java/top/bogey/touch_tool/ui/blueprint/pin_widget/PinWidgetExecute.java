package top.bogey.touch_tool.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinIconExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinStringExecute;
import top.bogey.touch_tool.databinding.PinWidgetExecuteBinding;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.ui.blueprint.selecter.select_icon.SelectIconDialog;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetExecute extends PinWidget<PinExecute> {
    private final PinWidgetExecuteBinding binding;

    public PinWidgetExecute(@NonNull Context context, ActionCard card, PinView pinView, PinExecute pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetExecuteBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Keep
    public PinWidgetExecute(@NonNull Context context, ActionCard card, PinView pinView, PinStringExecute pinBase, boolean custom) {
        this(context, card, pinView, (PinExecute) pinBase, custom);
    }

    @Keep
    public PinWidgetExecute(@NonNull Context context, ActionCard card, PinView pinView, PinIconExecute pinBase, boolean custom) {
        this(context, card, pinView, (PinExecute) pinBase, custom);
    }

    @Override
    protected void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);

        switch (pinBase.getSubType()) {
            case WITH_STRING -> {
                pinView.getTitleView().setVisibility(GONE);
                binding.editBox.setVisibility(VISIBLE);
                binding.pickButton.setVisibility(GONE);
                PinStringExecute stringExecute = (PinStringExecute) pinBase;
                binding.editText.setText(stringExecute.getValue());
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        stringExecute.setValue(s.toString());
                        pinView.getPin().notifyValueUpdated();
                    }
                });
            }
            case WITH_ICON -> {
                pinView.getTitleView().setVisibility(GONE);
                binding.editBox.setVisibility(VISIBLE);
                binding.pickButton.setVisibility(VISIBLE);
                PinIconExecute iconExecute = (PinIconExecute) pinBase;
                binding.pickButton.setImageBitmap(iconExecute.getImage());
                binding.pickButton.setOnClickListener(v -> new SelectIconDialog(getContext(), result -> {
                    binding.pickButton.setImageBitmap(result);
                    pinView.getPin().notifyValueUpdated();
                    iconExecute.setImage(result);
                }).show());
                binding.editText.setText(iconExecute.getValue());
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        iconExecute.setValue(s.toString());
                        pinView.getPin().notifyValueUpdated();
                    }
                });
            }
        }
    }

    @Override
    protected void initCustom() {

    }
}
