package top.bogey.touch_tool.ui.blueprint.selecter.select_icon;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.DialogSelectIconBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.utils.DisplayUtil;
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

        binding.addButton.setOnClickListener(v -> {
            MainActivity activity = MainApplication.getInstance().getActivity();
            activity.launcherPickMedia((code, intent) -> {
                if (code == Activity.RESULT_OK) {
                    Uri uri = intent.getData();
                    if (uri != null) {
                        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri)) {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            callback.onResult(bitmap);
                            dismiss();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }, ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE);
        });

        calculateShowData();
        adapter.setData(icons);

        BottomSheetBehavior<FrameLayout> behavior = getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        MainActivity activity = MainApplication.getInstance().getActivity();
        View decorView = activity.getWindow().getDecorView();
        int width = decorView.getWidth();
        int height = decorView.getHeight();

        boolean portrait = DisplayUtil.isPortrait(context);
        if (portrait) {
            DisplayUtil.setViewHeight(binding.getRoot(), (int) (height * 0.7f));
            DisplayUtil.setViewWidth(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            DisplayUtil.setViewHeight(binding.getRoot(), (int) (height * 0.8f));
            behavior.setMaxWidth(width);
        }
    }

    public void calculateShowData() {
        icons.clear();
        icons.put(getContext().getString(R.string.select_icon_preset), new ArrayList<>(getPresetIcons()));
        icons.put(getContext().getString(R.string.select_icon_app), new ArrayList<>(getInstalledPackages()));
        icons.put(getContext().getString(R.string.select_icon_system_app), new ArrayList<>(getSystemPackages()));
    }

    private List<Integer> getPresetIcons() {
        List<Integer> icons = new ArrayList<>();
        icons.addAll(Arrays.asList(R.drawable.icon_add, R.drawable.icon_remove, R.drawable.icon_close, R.drawable.icon_check));
        icons.addAll(Arrays.asList(R.drawable.icon_arrow_up, R.drawable.icon_arrow_down, R.drawable.icon_arrow_left, R.drawable.icon_arrow_right));
        icons.addAll(Arrays.asList(R.drawable.icon_graph_1, R.drawable.icon_graph_2, R.drawable.icon_shuffle, R.drawable.icon_tactic));
        icons.addAll(Arrays.asList(R.drawable.icon_input, R.drawable.icon_output, R.drawable.icon_zoom_in, R.drawable.icon_zoom_out));
        icons.addAll(Arrays.asList(R.drawable.icon_text, R.drawable.icon_image, R.drawable.icon_palette, R.drawable.icon_widget));
        icons.addAll(Arrays.asList(R.drawable.icon_upload, R.drawable.icon_download, R.drawable.icon_copy, R.drawable.icon_edit));
        icons.addAll(Arrays.asList(R.drawable.icon_delete, R.drawable.icon_save, R.drawable.icon_draw, R.drawable.icon_refresh));
        icons.addAll(Arrays.asList(R.drawable.icon_notification, R.drawable.icon_calendar, R.drawable.icon_timer, R.drawable.icon_schedule));
        icons.addAll(Arrays.asList(R.drawable.icon_network, R.drawable.icon_bluetooth, R.drawable.icon_adb, R.drawable.icon_battery));
        icons.addAll(Arrays.asList(R.drawable.icon_map, R.drawable.icon_location, R.drawable.icon_hand, R.drawable.icon_touch));
        icons.addAll(Arrays.asList(R.drawable.icon_home, R.drawable.icon_assignment, R.drawable.icon_star, R.drawable.icon_setting));
        icons.addAll(Arrays.asList(R.drawable.icon_video_cam, R.drawable.icon_video_call, R.drawable.icon_visibility, R.drawable.icon_visibility_off));
        icons.addAll(Arrays.asList(R.drawable.icon_radio_checked, R.drawable.icon_radio_unchecked, R.drawable.icon_radio_selected, R.drawable.icon_radio_unselected));
        icons.addAll(Arrays.asList(R.drawable.icon_play, R.drawable.icon_pause, R.drawable.icon_stop, R.drawable.icon_screen_record));
        icons.addAll(Arrays.asList(R.drawable.icon_lock, R.drawable.icon_lock_open, R.drawable.icon_search, R.drawable.icon_auto_play));
        icons.addAll(Arrays.asList(R.drawable.icon_help, R.drawable.icon_info, R.drawable.icon_tag, R.drawable.icon_number));
        icons.addAll(Arrays.asList(R.drawable.icon_array, R.drawable.icon_folder, R.drawable.icon_keyboard, R.drawable.icon_link));
        icons.addAll(Arrays.asList(R.drawable.icon_redo, R.drawable.icon_undo, R.drawable.icon_dark_mode, R.drawable.icon_swap));
        icons.addAll(Arrays.asList(R.drawable.icon_back, R.drawable.icon_more, R.drawable.icon_phone, R.drawable.icon_apps));
        icons.addAll(Arrays.asList(R.drawable.icon_repeat, R.drawable.icon_repeat_one, R.drawable.icon_qr_code, R.drawable.icon_qr_code_scanner));
        icons.addAll(Arrays.asList(R.drawable.icon_area, R.drawable.icon_crop, R.drawable.icon_gesture, R.drawable.icon_hourglass_arrow_down));
        icons.addAll(Arrays.asList(R.drawable.icon_divide, R.drawable.icon_percent, R.drawable.icon_decimal_decrease, R.drawable.icon_equal));
        icons.addAll(Arrays.asList(R.drawable.icon_function, R.drawable.icon_join_inner, R.drawable.icon_split, R.drawable.icon_regular));
        icons.addAll(Arrays.asList(R.drawable.icon_live_help, R.drawable.icon_note_stack, R.drawable.icon_ocr, R.drawable.icon_text_to_speech));
        icons.addAll(Arrays.asList(R.drawable.icon_notification_active, R.drawable.icon_notification_off, R.drawable.icon_share, R.drawable.icon_toast));
        icons.addAll(Arrays.asList(R.drawable.icon_person_check, R.drawable.icon_straighten));
        return icons;
    }

    private List<PackageInfo> getInstalledPackages() {
        return TaskInfoSummary.getInstance().findApps(null, false);
    }

    private List<PackageInfo> getSystemPackages() {
        List<PackageInfo> systemApps = new ArrayList<>();
        List<PackageInfo> apps = TaskInfoSummary.getInstance().findApps(null, true);
        for (PackageInfo app : apps) {
            if (app.applicationInfo == null) continue;
            if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                systemApps.add(app);
            }
        }
        return systemApps;
    }
}
