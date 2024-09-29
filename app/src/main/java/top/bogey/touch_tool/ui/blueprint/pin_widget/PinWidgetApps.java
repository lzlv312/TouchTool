package top.bogey.touch_tool.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplications;
import top.bogey.touch_tool.databinding.PinWidgetAppBinding;
import top.bogey.touch_tool.databinding.PinWidgetAppItemBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.ui.blueprint.selecter.select_app.SelectAppDialog;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class PinWidgetApps extends PinWidget<PinApplications>{
    private final PinWidgetAppBinding binding;

    public PinWidgetApps(@NonNull Context context, ActionCard card, PinView pinView, PinApplications pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetAppBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    protected void initBase() {
        binding.selectAppButton.setOnClickListener(v -> new SelectAppDialog(pinBase, result -> refreshApps()).show(((AppCompatActivity) getContext()).getSupportFragmentManager(), null));
        refreshApps();
    }

    @Override
    protected void initCustom() {

    }

    private void refreshApps() {
        binding.iconBox.removeAllViews();

        List<PinObject> values = pinBase.getValues();
        if (values == null || values.isEmpty()) return;

        PackageManager manager = getContext().getPackageManager();
        String commonPackageName = getContext().getString(R.string.common_package);
        PinApplication commonApplication = values.stream().filter(value -> value instanceof PinApplication).map(value -> (PinApplication) value).filter(app -> app.getPackageName().equals(commonPackageName)).findFirst().orElse(null);

        int count = 0;
        if (commonApplication != null) {
            PinWidgetAppItemBinding itemBinding = PinWidgetAppItemBinding.inflate(LayoutInflater.from(getContext()), binding.iconBox, true);
            itemBinding.icon.setImageDrawable(getContext().getApplicationInfo().loadIcon(manager));
            itemBinding.numberBox.setVisibility(GONE);
            itemBinding.getRoot().setOnClickListener(v -> {
                pinBase.getValues().remove(commonApplication);
                refreshApps();
            });
            count++;

            if (values.size() == 1) return;
        }

        for (PinObject value : values) {
            if (value instanceof PinApplication app) {
                if (app.getPackageName().equals(commonPackageName)) continue;

                PinWidgetAppItemBinding itemBinding = PinWidgetAppItemBinding.inflate(LayoutInflater.from(getContext()), binding.iconBox, true);
                itemBinding.exclude.setVisibility(commonApplication == null ? GONE : VISIBLE);
                if (values.size() > 5 && count == 4) {
                    itemBinding.icon.setImageResource(R.drawable.icon_more);
                    itemBinding.icon.setImageTintList(ColorStateList.valueOf(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary)));
                    itemBinding.numberText.setText(String.valueOf(values.size() - count));
                    break;
                } else {
                    PackageInfo info = TaskInfoSummary.getInstance().getAppInfo(app.getPackageName());
                    if (info == null) {
                        itemBinding.icon.setImageResource(R.drawable.icon_menu_help);
                    } else {
                        itemBinding.icon.setImageDrawable(info.applicationInfo.loadIcon(manager));
                    }
                    List<String> classes = app.getActivityClasses();
                    if (classes == null || classes.isEmpty()) {
                        itemBinding.numberBox.setVisibility(GONE);
                    } else {
                        itemBinding.numberText.setText(String.valueOf(classes.size()));
                    }
                    itemBinding.getRoot().setOnClickListener(v -> {
                        pinBase.getValues().remove(value);
                        refreshApps();
                    });
                }

                count++;
            }
        }
    }
}
