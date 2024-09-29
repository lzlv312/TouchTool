package top.bogey.touch_tool.ui.blueprint.selecter.select_icon;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.DialogSelectIconBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.callback.BitmapResultCallback;

public class SelectIconDialog extends BottomSheetDialog {
    private final SelectIconPageAdapter adapter;
    private final Map<String, List<Object>> icons = new LinkedHashMap<>();

    public SelectIconDialog(@NonNull Context context, BitmapResultCallback callback) {
        super(context);
        DialogSelectIconBinding binding = DialogSelectIconBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        adapter = new SelectIconPageAdapter(result -> {
            callback.onResult(result);
            dismiss();
        });
        binding.actionsBox.setAdapter(adapter);
        new TabLayoutMediator(binding.tabBox, binding.actionsBox, (tab, position) -> {
            if (position < adapter.tags.size()) {
                tab.setText(adapter.tags.get(position));
            }
        }).attach();

        calculateShowData();
        adapter.setData(icons);
    }

    public void calculateShowData() {
        icons.clear();
        icons.put(getContext().getString(R.string.select_icon_preset), new ArrayList<>(getPresetIcons()));
        icons.put(getContext().getString(R.string.select_icon_app), new ArrayList<>(getInstalledPackages()));
        icons.put(getContext().getString(R.string.select_icon_system_app), new ArrayList<>(getSystemPackages()));
    }

    private List<Integer> getPresetIcons() {
        List<Integer> icons = new ArrayList<>();
        icons.addAll(Arrays.asList(R.drawable.icon_adb, R.drawable.icon_add, R.drawable.icon_array, R.drawable.icon_auto_start));
        icons.addAll(Arrays.asList(R.drawable.icon_battery, R.drawable.icon_bluetooth, R.drawable.icon_capture, R.drawable.icon_check));
        icons.addAll(Arrays.asList(R.drawable.icon_close, R.drawable.icon_color, R.drawable.icon_condition, R.drawable.icon_copy));
        icons.addAll(Arrays.asList(R.drawable.icon_date, R.drawable.icon_delay, R.drawable.icon_delete, R.drawable.icon_menu_save));
        icons.addAll(Arrays.asList(R.drawable.icon_edit, R.drawable.icon_folder, R.drawable.icon_hand, R.drawable.icon_home));
        icons.addAll(Arrays.asList(R.drawable.icon_image, R.drawable.icon_info, R.drawable.icon_input, R.drawable.icon_lock));
        icons.addAll(Arrays.asList(R.drawable.icon_log, R.drawable.icon_menu_export, R.drawable.icon_menu_import, R.drawable.icon_search));
        icons.addAll(Arrays.asList(R.drawable.icon_network, R.drawable.icon_night, R.drawable.icon_notification, R.drawable.icon_number));
        icons.addAll(Arrays.asList(R.drawable.icon_play, R.drawable.icon_path, R.drawable.icon_position, R.drawable.icon_radio_checked));
        icons.addAll(Arrays.asList(R.drawable.icon_radio_unchecked, R.drawable.icon_radio_selected, R.drawable.icon_radio_unselected));
        icons.addAll(Arrays.asList(R.drawable.icon_record, R.drawable.icon_record_start, R.drawable.icon_refresh, R.drawable.icon_service));
        icons.addAll(Arrays.asList(R.drawable.icon_setting, R.drawable.icon_shortcut, R.drawable.icon_stop, R.drawable.icon_tag));
        icons.addAll(Arrays.asList(R.drawable.icon_task, R.drawable.icon_text, R.drawable.icon_time, R.drawable.icon_touch));
        icons.addAll(Arrays.asList(R.drawable.icon_tutorial, R.drawable.icon_unlock, R.drawable.icon_uri));
        icons.addAll(Arrays.asList(R.drawable.icon_widget, R.drawable.icon_zoom_in, R.drawable.icon_zoom_out));
        return icons;
    }

    private List<PackageInfo> getInstalledPackages() {
        return TaskInfoSummary.getInstance().findApps(null, false);
    }

    private List<PackageInfo> getSystemPackages() {
        List<PackageInfo> systemApps = new ArrayList<>();
        List<PackageInfo> apps = TaskInfoSummary.getInstance().findApps(null, true);
        for (PackageInfo app : apps) {
            if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                systemApps.add(app);
            }
        }
        return systemApps;
    }
}
