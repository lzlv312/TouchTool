package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.databinding.DialogSelectActionVariableBinding;
import top.bogey.touch_tool.databinding.DialogSelectActionVariableItemBinding;

public class SelectActionVariableTypeDialog extends FrameLayout {
    private final static Map<PinType, List<PinInfo>> PIN_INFO_MAP = PinInfo.getCustomPinInfoMap();
    private final DialogSelectActionVariableBinding binding;

    private MaterialButton button = null;

    public SelectActionVariableTypeDialog(Context context) {
        super(context);

        binding = DialogSelectActionVariableBinding.inflate(LayoutInflater.from(context), this, true);

        PIN_INFO_MAP.forEach((type, infoList) -> {
            for (int i = 0; i < infoList.size(); i++) {
                PinInfo info = infoList.get(i);
                DialogSelectActionVariableItemBinding itemBinding = DialogSelectActionVariableItemBinding.inflate(LayoutInflater.from(context), binding.contentBox, true);
                itemBinding.getRoot().setTag(info);
                itemBinding.getRoot().setText(info.getTitle());
                itemBinding.getRoot().setOnClickListener(v -> {
                    if (button != null) button.setChecked(false);
                    button = (MaterialButton) v;
                    button.setChecked(true);
                });
                if (button == null) {
                    button = itemBinding.getRoot();
                    button.setChecked(true);
                }
            }
        });
    }

    public PinInfo getSelected() {
        if (button == null) return null;
        return (PinInfo) button.getTag();
    }
}
