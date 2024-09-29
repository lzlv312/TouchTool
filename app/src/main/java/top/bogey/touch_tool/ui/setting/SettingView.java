package top.bogey.touch_tool.ui.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

        activity.addMenuProvider(menuProvider, getViewLifecycleOwner());

        binding = ViewSettingBinding.inflate(inflater, container, false);

        // 功能启用
        binding.enableSwitch.setOnSwitchClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (binding.enableSwitch.isChecked()) {
                if (AppUtil.isAccessibilityServiceEnabled(activity)) {
                    if (service != null) {
                        service.setEnabled(true);
                        SettingSaver.getInstance().setEnabled(true);
                    } else {
                        binding.enableSwitch.setChecked(false);
                        SettingSaver.getInstance().setEnabled(false);
                    }
                } else {
                    AppUtil.showDialog(activity, getString(R.string.app_setting_enable_tips, getString(R.string.app_name)), result -> {
                        if (result) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    });
                    binding.enableSwitch.setChecked(false);
                    SettingSaver.getInstance().setEnabled(false);
                }
            } else {
                if (service != null) {
                    service.setEnabled(false);
                    SettingSaver.getInstance().setEnabled(false);
                }
            }
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
        binding.forgeServiceSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setForgeService(activity, binding.forgeServiceSwitch.isChecked()));
        binding.forgeServiceSwitch.setChecked(SettingSaver.getInstance().isForgeService());

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
                SettingSaver.getInstance().setManualPlay(index);
            }
        });
        binding.manualPlaySelect.checkIndex(SettingSaver.getInstance().getManualPlay());

        // 屏幕截图
        binding.captureSelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setCapture(index);
                binding.ocrSwitch.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
            }
        });
        binding.captureSelect.checkIndex(SettingSaver.getInstance().getCapture());
        binding.ocrSwitch.setVisibility(SettingSaver.getInstance().getCapture() == 0 ? View.GONE : View.VISIBLE);

        // 文字识别
        binding.ocrSwitch.setOnSwitchClickListener(v -> {
            if (binding.ocrSwitch.isChecked()) {

            } else {
                SettingSaver.getInstance().setOcr(false);
            }
        });
        binding.ocrSwitch.setChecked(SettingSaver.getInstance().isOcr());

        // 精确定时
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        boolean canScheduleExactAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms();
        binding.alarmSwitch.setOnSwitchClickListener(v -> {
            if (binding.alarmSwitch.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (canScheduleExactAlarms) {
                        SettingSaver.getInstance().setAlarm(true);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                        binding.alarmSwitch.setChecked(false);
                    }
                } else {
                    binding.alarmSwitch.setChecked(true);
                }
            } else {
                SettingSaver.getInstance().setAlarm(false);
            }
            if (binding.alarmSwitch.isChecked()) {
                MainAccessibilityService service = MainApplication.getInstance().getService();
                if (service != null && service.isEnabled()) {
                    service.resetAllAlarm();
                }
            }
        });
        binding.alarmSwitch.setChecked(canScheduleExactAlarms && SettingSaver.getInstance().isAlarm());

        // 蓝牙监听
        binding.bluetoothSwitch.setOnSwitchClickListener(v -> {
            if (binding.bluetoothSwitch.isChecked()) {
                activity.launcherBluetooth((code, data) -> {
                    boolean enable = code == Activity.RESULT_OK;
                    SettingSaver.getInstance().setBluetooth(enable);
                    binding.bluetoothSwitch.setChecked(enable);
                });
            } else {
                SettingSaver.getInstance().setBluetooth(false);
            }
        });
        binding.bluetoothSwitch.setChecked(SettingSaver.getInstance().isBluetooth());


        // 手势轨迹
        binding.showTouchSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setShowTouch(binding.showTouchSwitch.isChecked()));
        binding.showTouchSwitch.setChecked(SettingSaver.getInstance().isShowTouch());

        // 任务提示
        binding.taskTipsSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setStartTips(binding.taskTipsSwitch.isChecked()));
        binding.taskTipsSwitch.setChecked(SettingSaver.getInstance().isStartTips());


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
        binding.dynamicColorSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setColor(activity, binding.dynamicColorSwitch.isChecked()));
        binding.dynamicColorSwitch.setChecked(SettingSaver.getInstance().isColor());

        // 优先查看蓝图
        binding.lookFirstSwitch.setOnSwitchClickListener(v -> SettingSaver.getInstance().setLookFirst(binding.lookFirstSwitch.isChecked()));
        binding.lookFirstSwitch.setChecked(SettingSaver.getInstance().isLookFirst());

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
