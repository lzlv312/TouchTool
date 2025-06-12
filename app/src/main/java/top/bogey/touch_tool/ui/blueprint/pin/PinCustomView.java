package top.bogey.touch_tool.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.card.CustomActionCard;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionVariableTypeDialog;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public abstract class PinCustomView extends PinView {
    public PinCustomView(@NonNull Context context, ActionCard card, Pin pin) {
        super(context, card, pin, true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init() {
        super.init();
        TextView typeView = getTypeView();
        typeView.setOnClickListener(v -> {
            SelectActionVariableTypeDialog dialog = new SelectActionVariableTypeDialog(getContext());
            new MaterialAlertDialogBuilder(getContext())
                    .setView(dialog)
                    .setPositiveButton(R.string.enter, (view, which) -> {
                        PinInfo pinInfo = dialog.getSelected();
                        typeView.setText(pinInfo.getTitle());
                        pin.setValue(pinInfo.newInstance());
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        EditText editText = getTitleEdit();
        editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(pin.getTitle())) return;
                pin.setTitle(s.toString());
            }
        });

        MaterialButton visibleButton = getVisibleButton();
        visibleButton.setOnClickListener(v -> {
            pin.setHide(!pin.isHide());
            visibleButton.setIconResource(pin.isHide() ? R.drawable.icon_visibility_off : R.drawable.icon_visibility);
        });

        // 抑制一下recycleView的滚动，让针脚连线能够生效
        ViewGroup slotBox = getSlotBox();
        CustomActionCard card = (CustomActionCard) getCard();
        slotBox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) card.suppressLayout();
            return false;
        });
    }

    @Override
    public void refreshPin() {
        EditText editText = getTitleEdit();
        if (!editText.hasFocus()) {
            editText.setText(pin.getTitle());
        }

        TextView typeView = getTypeView();
        PinInfo pinInfo = PinInfo.getPinInfo(pin.getValue());
        typeView.setText(pinInfo.getTitle());

        MaterialButton visibleButton = getVisibleButton();
        visibleButton.setIconResource(pin.isHide() ? R.drawable.icon_visibility_off : R.drawable.icon_visibility);

        super.refreshPin();
    }

    public abstract TextView getTypeView();

    public abstract EditText getTitleEdit();

    public abstract MaterialButton getVisibleButton();
}
