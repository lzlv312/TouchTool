package top.bogey.touch_tool.service;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.ApplicationStartAction;
import top.bogey.touch_tool.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool.bean.action.start.BluetoothStartAction;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool.bean.action.start.NotificationStartAction;
import top.bogey.touch_tool.bean.action.start.ScreenStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.save.TaskSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.ui.play.PlayFloatView;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

public class TaskInfoSummary {
    private static TaskInfoSummary instance;

    public static TaskInfoSummary getInstance() {
        synchronized (TaskInfoSummary.class) {
            if (instance == null) {
                instance = new TaskInfoSummary();
            }
        }
        return instance;
    }

    private final Map<String, PackageInfo> apps = new ConcurrentHashMap<>();

    private PackageActivity packageActivity;
    private Notification notification;
    private BatteryInfo batteryInfo;
    private BluetoothInfo bluetoothInfo;
    private PhoneState phoneState;
    private List<NotworkState> networkState;

    public void resetApps() {
        apps.clear();
        PackageManager packageManager = MainApplication.getInstance().getPackageManager();
        List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.MATCH_DISABLED_COMPONENTS | PackageManager.MATCH_ALL);
        for (ApplicationInfo application : applications) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(application.packageName, PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) apps.put(packageInfo.packageName, packageInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public PackageInfo getAppInfo(String packageName) {
        return apps.get(packageName);
    }

    public String getAppName(String packageName) {
        PackageInfo packageInfo = getAppInfo(packageName);
        if (packageInfo == null) return null;
        return packageInfo.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
    }

    public List<PackageInfo> findApps(String keyword, boolean system) {
        List<PackageInfo> packages = new ArrayList<>();

        for (PackageInfo info : apps.values()) {
            if (info.packageName.equals(MainApplication.getInstance().getPackageName()))
                continue;
            if (system || (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                if (keyword == null || keyword.isEmpty()) {
                    packages.add(info);
                } else {
                    if (info.packageName.toLowerCase().contains(keyword.toLowerCase()))
                        packages.add(info);
                    else {
                        String appName = info.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
                        if (appName.toLowerCase().contains(keyword.toLowerCase()))
                            packages.add(info);
                    }
                }
            }
        }
        return packages;
    }

    public List<PackageInfo> findSendApps(String keyword, boolean system) {
        List<PackageInfo> packages = new ArrayList<>();

        PackageManager packageManager = MainApplication.getInstance().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        List<ResolveInfo> resolves = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo resolveInfo : resolves) {
            if (resolveInfo.activityInfo.packageName.equals(MainApplication.getInstance().getPackageName()))
                continue;
            PackageInfo info = apps.get(resolveInfo.activityInfo.packageName);
            if (info == null) continue;
            if (system || (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                if (keyword == null || keyword.isEmpty()) {
                    packages.add(info);
                } else {
                    if (info.packageName.toLowerCase().contains(keyword.toLowerCase()))
                        packages.add(info);
                    else {
                        String appName = info.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
                        if (appName.toLowerCase().contains(keyword.toLowerCase()))
                            packages.add(info);
                    }
                }
            }
        }
        return packages;
    }

    public void tryStartActions(Class<? extends StartAction> clazz) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;

        for (Task task : TaskSaver.getInstance().getTasks(clazz)) {
            for (Action action : task.getActions(clazz)) {
                StartAction startAction = (StartAction) action;
                if (startAction.isEnable() && startAction.ready()) service.runTask(task, startAction);
            }
        }
    }

    public void tryShowManualPlayView(boolean show) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;

        Map<ManualStartAction, Task> actionTasks = new LinkedHashMap<>();

        if (show) {
            for (Task task : TaskSaver.getInstance().getTasks(ManualStartAction.class)) {
                for (Action action : task.getActions(ManualStartAction.class)) {
                    ManualStartAction startAction = (ManualStartAction) action;
                    if (startAction.isEnable() && startAction.isEnable()) {
                        actionTasks.put(startAction, task);
                    }
                }
            }
        }

        View view = FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (view != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                View playFloatView = FloatWindow.getView(PlayFloatView.class.getName());
                if (playFloatView != null) {
                    ((PlayFloatView) playFloatView).setActions(actionTasks);
                } else {
                    if (!actionTasks.isEmpty())
                        new PlayFloatView(view.getContext(), actionTasks).show();
                }
            });
        }
    }

    public boolean isActivityClass(String packageName, String activityName) {
        if (packageName == null || activityName == null) return false;
        if (packageName.isEmpty() || activityName.isEmpty()) return false;
        PackageInfo packageInfo = getAppInfo(packageName);
        if (packageInfo == null || packageInfo.activities == null) return false;
        for (ActivityInfo activityInfo : packageInfo.activities) {
            if (activityInfo.name.equals(activityName)) return true;
        }
        return false;
    }

    public void enterActivity(String packageName, String activityName) {
        if (isActivityClass(packageName, activityName)) {
            if (packageName.equals(MainApplication.getInstance().getPackageName())) {
                if (setPackageActivity(packageName, activityName)) {
                    tryShowManualPlayView(!activityName.equals(MainActivity.class.getName()));
                }
            } else {
                if (setPackageActivity(packageName, activityName)) {
                    tryStartActions(ApplicationStartAction.class);
                    tryShowManualPlayView(true);
                }
            }
        }
    }

    public PackageActivity getPackageActivity() {
        return packageActivity;
    }

    public boolean setPackageActivity(String packageName, String activityName) {
        if (packageName == null || activityName == null) return false;
        if (packageName.isEmpty() || activityName.isEmpty()) return false;
        if (packageActivity != null && packageActivity.packageName.equals(packageName) && packageActivity.activityName.equals(activityName))
            return false;
        packageActivity = new PackageActivity(packageName, activityName);
        return true;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(String packageName, String title, String content) {
        notification = new Notification(packageName, title, content);
        tryStartActions(NotificationStartAction.class);
    }

    public BatteryInfo getBatteryInfo() {
        return batteryInfo;
    }

    public void setBatteryInfo(int percent, BatteryState status) {
        batteryInfo = new BatteryInfo(percent, status);
        tryStartActions(BatteryStartAction.class);
    }

    public BluetoothInfo getBluetoothInfo() {
        return bluetoothInfo;
    }

    public void setBluetoothInfo(String bluetoothAddress, String bluetoothName, boolean active) {
        bluetoothInfo = new BluetoothInfo(bluetoothAddress, bluetoothName, active);
        tryStartActions(BluetoothStartAction.class);
    }

    public PhoneState getPhoneState() {
        return phoneState;
    }

    public void setPhoneState(PhoneState phoneState) {
        this.phoneState = phoneState;
        tryStartActions(ScreenStartAction.class);
    }

    public List<NotworkState> getNetworkState() {
        return networkState;
    }

    public void setNetworkState(List<NotworkState> networkState) {
        this.networkState = networkState;
        tryStartActions(NetworkStartAction.class);
    }

    public record PackageActivity(String packageName, String activityName) {
    }

    public record Notification(String packageName, String title, String content) {
    }

    public record BatteryInfo(int percent, BatteryState status) {
    }

    public record BluetoothInfo(String bluetoothAddress, String bluetoothName, boolean active) {
    }

    public enum PhoneState {OFF, LOCKED, ON}

    public enum NotworkState {NONE, WIFI, MOBILE, VPN}

    public enum BatteryState {UNKNOWN, CHARGING, DISCHARGING, NOT_CHARGING, FULL}

    public enum OcrType {CHINESE, ENGLISH}
}
