package top.bogey.touch_tool.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.card.IDynamicPinCard;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionVariableTypeDialog;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.listener.SpinnerSelectedListener;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public abstract class PinCustomView extends PinView {
    private final Variable variable;

    public PinCustomView(@NonNull Context context, ActionCard card, Pin pin) {
        super(context, card, pin, true);
        if (pin.getValue() instanceof PinObject) {
            variable = new Variable((PinObject) pin.getValue().copy());
        } else {
            variable = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init() {
        super.init();
        TextView keyTypeView = getKeyTypeView();
        if (keyTypeView != null) {
            keyTypeView.setOnClickListener(v -> {
                SelectActionVariableTypeDialog dialog = new SelectActionVariableTypeDialog(getContext());
                new MaterialAlertDialogBuilder(getContext())
                        .setView(dialog)
                        .setPositiveButton(R.string.enter, (view, which) -> {
                            PinInfo pinInfo = dialog.getSelected();
                            keyTypeView.setText(pinInfo.getTitle());
                            variable.setKeyPinInfo(pinInfo);
                            pin.setValue(variable.getValue().copy());
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });
        }

        TextView valueTypeView = getValueTypeView();
        if (valueTypeView != null) {
            valueTypeView.setOnClickListener(v -> {
                SelectActionVariableTypeDialog dialog = new SelectActionVariableTypeDialog(getContext());
                new MaterialAlertDialogBuilder(getContext())
                        .setView(dialog)
                        .setPositiveButton(R.string.enter, (view, which) -> {
                            PinInfo pinInfo = dialog.getSelected();
                            valueTypeView.setText(pinInfo.getTitle());
                            variable.setValuePinInfo(pinInfo);
                            pin.setValue(variable.getValue().copy());
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });
        }

        Spinner typeSpinner = getTypeSpinner();
        if (typeSpinner != null) {
            typeSpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Variable.VariableType type = Variable.VariableType.values()[position];
                    if (variable.getType() == type) return;
                    variable.setType(type);
                    pin.setValue(variable.getValue().copy());
                    refreshPin();
                }
            });
        }

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

//        // 抑制一下recycleView的滚动，让针脚连线能够生效
//        ViewGroup slotBox = getSlotBox();
//        if (getCard() instanceof IDynamicPinCard dynamicPinCard) {
//            slotBox.setOnTouchListener((v, event) -> {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) dynamicPinCard.suppressLayout();
//                return false;
//            });
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 抑制一下recycleView的滚动，让针脚连线能够生效
        if (getCard() instanceof IDynamicPinCard dynamicPinCard) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                PointF pointF = DisplayUtil.getLocationRelativeToView(this, getCard());
                if (getCard().getLinkAblePinView(x + pointF.x, y + pointF.y) == this) {
                    dynamicPinCard.suppressLayout();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void refreshPin() {
        EditText editText = getTitleEdit();
        if (!editText.hasFocus()) {
            editText.setText(pin.getTitle());
        }

        TextView keyTypeView = getKeyTypeView();
        if (keyTypeView != null) {
            PinInfo keyPinInfo = variable.getKeyPinInfo();
            if (keyPinInfo != null) keyTypeView.setText(keyPinInfo.getTitle());
        }

        Spinner typeSpinner = getTypeSpinner();
        if (typeSpinner != null) {
            String[] array = getResources().getStringArray(R.array.pin_simple_type);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.pin_widget_select_item, array);
            typeSpinner.setAdapter(adapter);
            typeSpinner.setSelection(variable.getType().ordinal());
        }

        TextView valueTypeView = getValueTypeView();
        if (valueTypeView != null) {

            valueTypeView.setVisibility(variable.getType() == Variable.VariableType.MAP ? View.VISIBLE : View.GONE);
            PinInfo valuePinInfo = variable.getValuePinInfo();
            if (valuePinInfo != null) valueTypeView.setText(valuePinInfo.getTitle());
        }

        MaterialButton visibleButton = getVisibleButton();
        visibleButton.setIconResource(pin.isHide() ? R.drawable.icon_visibility_off : R.drawable.icon_visibility);

        super.refreshPin();
    }

    public abstract TextView getKeyTypeView();

    public abstract TextView getValueTypeView();

    public abstract Spinner getTypeSpinner();

    public abstract EditText getTitleEdit();

    public abstract MaterialButton getVisibleButton();
}
