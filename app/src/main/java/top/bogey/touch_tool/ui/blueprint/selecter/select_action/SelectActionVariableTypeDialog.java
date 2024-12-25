package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.databinding.DialogSelectActionVariableItemBinding;
import top.bogey.touch_tool.utils.DisplayUtil;

public class SelectActionVariableTypeDialog extends FlexboxLayout {
    private final static Map<PinType, List<PinInfo>> PIN_INFO_MAP = PinInfo.getCustomPinInfoMap();

    private MaterialButton button = null;

    public SelectActionVariableTypeDialog(Context context) {
        super(context);

        int padding = (int) DisplayUtil.dp2px(context, 16);
        setPadding(padding, padding, padding, padding);

        setFlexWrap(FlexWrap.WRAP);
        setFlexDirection(FlexDirection.ROW);
        setJustifyContent(JustifyContent.CENTER);

        PIN_INFO_MAP.forEach((type, infoList) -> {
            for (PinInfo info : infoList) {
                DialogSelectActionVariableItemBinding itemBinding = DialogSelectActionVariableItemBinding.inflate(LayoutInflater.from(context), this, true);
                itemBinding.getRoot().setTag(info);
                itemBinding.getRoot().setText(info.getTitle());
                itemBinding.getRoot().setOnClickListener(v -> {
                    if (button != null) button.setChecked(false);
                    button = (MaterialButton) v;
                    button.setChecked(true);
                });
            }
        });
    }

    public PinInfo getSelected() {
        if (button == null) return null;
        return (PinInfo) button.getTag();
    }
}
