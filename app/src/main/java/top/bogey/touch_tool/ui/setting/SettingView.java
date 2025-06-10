package top.bogey.touch_tool.ui.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.databinding.ViewSettingBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.super_user.ISuperUser;
import top.bogey.touch_tool.service.super_user.SuperUser;
import top.bogey.touch_tool.service.super_user.root.RootSuperUser;
import top.bogey.touch_tool.service.super_user.shizuku.ShizukuSuperUser;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.utils.AppUtil;

public class SettingView extends Fragment {
    private ViewSettingBinding binding;

    private final MenuProvider menuProvider = new MenuProvider() {

        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_setting, menu);

            boolean valid = SuperUser.getInstance().isValid();
            menu.findItem(R.id.reloadService).setVisible(valid);
            menu.findItem(R.id.writeSecureSetting).setVisible(valid);
            menu.findItem(R.id.autoGiveCapturePermission).setVisible(valid);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.reloadService) {
                MainActivity activity = (MainActivity) requireActivity();

                return true;
            } else if (menuItem.getItemId() == R.id.writeSecureSetting) {
                MainActivity activity = (MainActivity) requireActivity();

                return true;
            } else if (menuItem.getItemId() == R.id.autoGiveCapturePermission) {
                MainActivity activity = (MainActivity) requireActivity();

                return true;
            }
            return false;
        }
    };

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();

        binding = ViewSettingBinding.inflate(inflater, container, false);

        binding.toolBar.addMenuProvider(menuProvider, getViewLifecycleOwner());

        // 功能启用
        binding.enableSwitch.setOnSwitchClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            boolean enable = false;
            if (binding.enableSwitch.isChecked()) {
                if (AppUtil.isAccessibilityServiceEnabled(activity)) {
                    if (service != null) {
                        enable = true;
                        service.setEnabled(true);
                    } else {
                        binding.enableSwitch.setChecked(false);
                    }
                } else {
                    AppUtil.showDialog(activity, getString(R.string.app_setting_enable_tips, getString(R.string.app_name)), result -> {
                        if (result) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    });
                    binding.enableSwitch.setChecked(false);
                }
            } else {
                if (service != null) {
                    service.setEnabled(false);
                }
            }

            SettingSaver.getInstance().setEnabled(enable);
        });

        MainAccessibilityService serv = MainApplication.getInstance().getService();
        binding.enableSwitch.setChecked(serv != null && serv.isEnabled());

        // 隐藏后台
        binding.hideBackSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setHideBack(activity, binding.hideBackSwitch.isChecked()));
        binding.hideBackSwitch.setChecked(SettingSaver.getInstance().isHideBack());

        // 忽略电量限制
        binding.ignoreBatterySwitch.setOnSwitchClickListener(v -> {
            if (binding.ignoreBatterySwitch.isChecked()) {
                if (!AppUtil.isIgnoredBattery(activity)) {
                    AppUtil.gotoIgnoreBattery(activity);
                    binding.ignoreBatterySwitch.setChecked(false);
                }
            } else {
                if (AppUtil.isIgnoredBattery(activity)) {
                    AppUtil.gotoIgnoreBattery(activity);
                    binding.ignoreBatterySwitch.setChecked(true);
                }
            }
        });
        binding.ignoreBatterySwitch.setChecked(AppUtil.isIgnoredBattery(activity));

        // 前台服务
        binding.forgeServiceSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setForgeServiceEnabled(activity, binding.forgeServiceSwitch.isChecked()));
        binding.forgeServiceSwitch.setChecked(SettingSaver.getInstance().isForgeServiceEnabled());

        // 开机自启
        binding.autoStartSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setAutoStart(binding.autoStartSwitch.isChecked()));
        binding.autoStartSwitch.setChecked(SettingSaver.getInstance().isAutoStart());

        // 清理缓存
        binding.cleanCacheButton.setOnClickListener(v -> {
            AppUtil.deleteFile(activity.getCacheDir());
            binding.cleanCacheButton.setDescription(getString(R.string.app_setting_clean_cache_desc, AppUtil.getFileSizeString(activity.getCacheDir())));
        });
        binding.cleanCacheButton.setDescription(getString(R.string.app_setting_clean_cache_desc, AppUtil.getFileSizeString(activity.getCacheDir())));


        // 超级用户
        binding.superUserSelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setSuperUser(index);

                // 尝试超级用户
                ISuperUser instance = SuperUser.getInstance();
                if (instance == null || !instance.init()) {
                    SettingSaver.getInstance().setSuperUser(0);
                    binding.superUserSelect.checkIndex(0);

                    if (instance instanceof ShizukuSuperUser) Toast.makeText(activity, R.string.permission_setting_super_user_no_shizuku, Toast.LENGTH_SHORT).show();
                    else if (instance instanceof RootSuperUser) Toast.makeText(activity, R.string.permission_setting_super_user_no_root, Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.superUserSelect.checkIndex(SettingSaver.getInstance().getSuperUser());

        // 手动执行
        binding.manualPlaySelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setManualPlayType(index);
            }
        });
        binding.manualPlaySelect.checkIndex(SettingSaver.getInstance().getManualPlayType());
        binding.manualPlaySelect.setOnClickListener(v -> {
            SettingSaver.getInstance().setPlayViewPos(new Point());
            SettingSaver.getInstance().setPlayViewExpand(true);
        });

        // 精确定时
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        boolean canScheduleExactAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms();
        binding.alarmSwitch.setOnSwitchClickListener(v -> {
            if (binding.alarmSwitch.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (canScheduleExactAlarms) {
                        SettingSaver.getInstance().setAlarmEnabled(true);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                        binding.alarmSwitch.setChecked(false);
                    }
                } else {
                    binding.alarmSwitch.setChecked(true);
                }
            } else {
                SettingSaver.getInstance().setAlarmEnabled(false);
            }
            if (binding.alarmSwitch.isChecked()) {
                MainAccessibilityService service = MainApplication.getInstance().getService();
                if (service != null && service.isEnabled()) {
                    service.resetAllAlarm();
                }
            }
        });
        binding.alarmSwitch.setChecked(canScheduleExactAlarms && SettingSaver.getInstance().isAlarmEnabled());

        // 蓝牙监听
        binding.bluetoothSwitch.setOnSwitchClickListener(v -> {
            if (binding.bluetoothSwitch.isChecked()) {
                activity.launcherBluetooth((code, data) -> {
                    boolean enable = code == Activity.RESULT_OK;
                    SettingSaver.getInstance().setBluetoothEnabled(enable);
                    binding.bluetoothSwitch.setChecked(enable);
                });
            } else {
                SettingSaver.getInstance().setBluetoothEnabled(false);
            }
        });
        binding.bluetoothSwitch.setChecked(SettingSaver.getInstance().isBluetoothEnabled());


        // 手势轨迹
        binding.showTouchSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setShowTouch(binding.showTouchSwitch.isChecked()));
        binding.showTouchSwitch.setChecked(SettingSaver.getInstance().isShowTouch());

        // 标记目标区域
        binding.showTargetAreaSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setShowTargetArea(binding.showTouchSwitch.isChecked()));
        binding.showTargetAreaSwitch.setChecked(SettingSaver.getInstance().isShowTargetArea());

        // 任务提示
        binding.taskTipsSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setShowStartTips(binding.taskTipsSwitch.isChecked()));
        binding.taskTipsSwitch.setChecked(SettingSaver.getInstance().isShowStartTips());


        // 小窗优化
        binding.supportFreeFormSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setSupportFreeForm(binding.supportFreeFormSwitch.isChecked()));
        binding.supportFreeFormSwitch.setChecked(SettingSaver.getInstance().isSupportFreeForm());

        // 夜间模式
        binding.themeSelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setTheme(index);
            }
        });
        binding.themeSelect.checkIndex(SettingSaver.getInstance().getTheme());

        // 动态颜色
        binding.dynamicColorSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setDynamicColorTheme(activity, binding.dynamicColorSwitch.isChecked()));
        binding.dynamicColorSwitch.setChecked(SettingSaver.getInstance().isDynamicColorTheme());

        // 手动执行悬浮窗偏移
        binding.manualPlayPadding.setSliderOnChangeListener((slider, value, fromUser) -> SettingSaver.getInstance().setPlayViewPadding((int) value));
        binding.manualPlayPadding.setValue(SettingSaver.getInstance().getPlayViewPadding());

        PackageManager packageManager = activity.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            binding.versionText.setText(packageInfo.versionName + "(" + packageInfo.versionCode + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }
}
