package top.bogey.touch_tool.service;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.ApplicationStartAction;
import top.bogey.touch_tool.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool.bean.action.start.BluetoothStartAction;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool.bean.action.start.NotificationStartAction;
import top.bogey.touch_tool.bean.action.start.ReceivedShareStartAction;
import top.bogey.touch_tool.bean.action.start.ScreenStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.custom.ChoiceExecuteFloatView;
import top.bogey.touch_tool.ui.play.PlayFloatView;
import top.bogey.touch_tool.ui.play.SinglePlayView;
import top.bogey.touch_tool.utils.AppUtil;

public class TaskInfoSummary {
    public static final String OCR_SERVICE_ACTION = "top.bogey.ocr.OcrService";
    private static final String XMLNS_ANDROID = "http://schemas.android.com/apk/res/android";
    private static final int PACKAGE_FLAGS = PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.MATCH_DISABLED_COMPONENTS | PackageManager.MATCH_ALL;

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

    private final List<String> ocrApps = new ArrayList<>();

    private PackageActivity packageActivity;
    private Notification notification;
    private BatteryInfo batteryInfo;
    private BluetoothInfo bluetoothInfo;
    private String lastClipboard;
    private List<NotworkState> networkState;

    public void resetApps() {
        apps.clear();
        PackageManager packageManager = MainApplication.getInstance().getPackageManager();
        List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.MATCH_DISABLED_COMPONENTS | PackageManager.MATCH_ALL);
        for (ApplicationInfo application : applications) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(application.packageName, PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) apps.put(packageInfo.packageName, packageInfo);
            } catch (Exception ignored) {
            }
        }

        ocrApps.clear();
        Intent intent = new Intent(OCR_SERVICE_ACTION);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ocrApps.add(resolveInfo.serviceInfo.packageName);
        }
        ocrApps.sort(String::compareTo);
    }

    public PackageInfo getAppInfo(String packageName) {
        return apps.get(packageName);
    }

    public String getAppName(String packageName) {
        PackageInfo packageInfo = getAppInfo(packageName);
        if (packageInfo == null || packageInfo.applicationInfo == null) return null;
        return packageInfo.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
    }

    public List<PackageInfo> findApps(String keyword, boolean system) {
        List<PackageInfo> packages = new ArrayList<>();

        for (PackageInfo info : apps.values()) {
            if (info.packageName.equals(MainApplication.getInstance().getPackageName())) continue;
            if (info.applicationInfo == null) continue;
            if (system || (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                if (keyword == null || keyword.isEmpty()) {
                    packages.add(info);
                } else {
                    if (info.packageName.toLowerCase().contains(keyword.toLowerCase())) packages.add(info);
                    else {
                        String appName = info.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
                        if (appName.toLowerCase().contains(keyword.toLowerCase())) packages.add(info);
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
            if (resolveInfo.activityInfo.packageName.equals(MainApplication.getInstance().getPackageName())) continue;
            PackageInfo info = apps.get(resolveInfo.activityInfo.packageName);
            if (info == null || info.applicationInfo == null) continue;
            if (system || (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                if (keyword == null || keyword.isEmpty()) {
                    packages.add(info);
                } else {
                    if (info.packageName.toLowerCase().contains(keyword.toLowerCase())) packages.add(info);
                    else {
                        String appName = info.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
                        if (appName.toLowerCase().contains(keyword.toLowerCase())) packages.add(info);
                    }
                }
            }
        }
        return packages;
    }

    private List<ShortcutInfo> findShortcutInfo() {
        List<ShortcutInfo> shortcuts = new ArrayList<>();

        PackageManager packageManager = MainApplication.getInstance().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.MATCH_ALL | PackageManager.GET_META_DATA);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            try (XmlResourceParser parser = activityInfo.loadXmlMetaData(packageManager, "android.app.shortcuts")) {
                if (parser == null) continue;
                int eventType = parser.getEventType();
                Intent shortcutIntent = null;
                ShortcutInfo shortcutInfo = new ShortcutInfo(activityInfo.packageName);

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.END_TAG && "shortcut".equals(parser.getName())) {
                        if (shortcutInfo.isValid()) {
                            shortcuts.add(shortcutInfo);
                        }
                        shortcutInfo = new ShortcutInfo(activityInfo.packageName);
                    } else if (eventType == XmlPullParser.END_TAG && "intent".equals(parser.getName())) {
                        if (shortcutIntent != null) {
                            shortcutInfo.intent = shortcutIntent.toUri(Intent.URI_INTENT_SCHEME);
                            shortcutIntent = null;
                        }
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if ("shortcut".equals(parser.getName()) && parser.getAttributeBooleanValue(XMLNS_ANDROID, "enabled", true)) {
                            shortcutInfo.id = parser.getAttributeValue(XMLNS_ANDROID, "shortcutId");
                            if (shortcutInfo.id == null) shortcutInfo.id = UUID.randomUUID().toString();

                            int titleId = parser.getAttributeResourceValue(XMLNS_ANDROID, "shortcutShortLabel", 0);
                            if (titleId == 0) titleId = parser.getAttributeResourceValue(XMLNS_ANDROID, "shortcutLongLabel", 0);
                            if (titleId != 0) {
                                try {
                                    shortcutInfo.title = packageManager.getResourcesForApplication(activityInfo.packageName).getString(titleId);
                                } catch (PackageManager.NameNotFoundException ignored) {
                                    shortcutInfo.title = shortcutInfo.id;
                                }
                            }
                            shortcutInfo.icon = parser.getAttributeResourceValue(XMLNS_ANDROID, "icon", 0);
                        } else if ("intent".equals(parser.getName()) && shortcutInfo.id != null) {
                            shortcutIntent = new Intent();
                            shortcutIntent.setAction(parser.getAttributeValue(XMLNS_ANDROID, "action"));

                            String pkg = parser.getAttributeValue(XMLNS_ANDROID, "targetPackage");
                            String cls = parser.getAttributeValue(XMLNS_ANDROID, "targetClass");
                            if (cls == null) {
                                cls = parser.getAttributeValue(XMLNS_ANDROID, "targetActivity");
                                if (cls != null) {
                                    cls = cls.replace("$", "_");
                                }
                            }
                            shortcutInfo.activityClass = cls;
                            if (pkg != null && cls != null) {
                                shortcutIntent.setClassName(pkg, cls);
                            }
                            if (shortcutInfo.title == null && cls != null) {
                                shortcutInfo.title = cls;
                            }
                            String data = parser.getAttributeValue(XMLNS_ANDROID, "data");
                            if (data != null) {
                                shortcutIntent.setData(Uri.parse(data));
                            }
                        } else if ("extra".equals(parser.getName()) && shortcutIntent != null) {
                            String name = parser.getAttributeValue(XMLNS_ANDROID, "name");
                            String value = parser.getAttributeValue(XMLNS_ANDROID, "value");
                            if (name != null && value != null) shortcutIntent.putExtra(name, value);
                        } else if ("categories".equals(parser.getName()) && shortcutIntent != null) {
                            String name = parser.getAttributeValue(XMLNS_ANDROID, "name");
                            if (name != null) shortcutIntent.addCategory(name);
                        }
                    }
                    eventType = parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
        return shortcuts;
    }

    public List<ShortcutInfo> findShortcutInfo(String packageName, String activityClass) {
        List<ShortcutInfo> shortcuts = new ArrayList<>();
        if (packageName == null) return shortcuts;
        for (ShortcutInfo shortcutApp : findShortcutInfo()) {
            if (shortcutApp.packageName.equals(packageName) && (activityClass == null || activityClass.equals(shortcutApp.activityClass))) {
                shortcuts.add(shortcutApp);
            }
        }
        return shortcuts;
    }

    public List<PackageInfo> findShortcutApps(String keyword, boolean system) {
        List<PackageInfo> shortcutApps = new ArrayList<>();
        for (ShortcutInfo shortcutApp : findShortcutInfo()) {
            PackageInfo info = getAppInfo(shortcutApp.packageName);
            if (info == null || info.applicationInfo == null) continue;
            if (shortcutApps.contains(info)) continue;
            if (system || (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                if (keyword == null || keyword.isEmpty()) {
                    shortcutApps.add(info);
                } else {
                    if (info.packageName.toLowerCase().contains(keyword.toLowerCase())) shortcutApps.add(info);
                    else {
                        String appName = info.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString();
                        if (appName.toLowerCase().contains(keyword.toLowerCase())) shortcutApps.add(info);
                    }
                }
            }
        }
        return shortcutApps;
    }

    public List<String> getOcrApps() {
        return ocrApps;
    }

    public List<String> getOcrAppNames() {
        List<String> names = new ArrayList<>();
        for (String packageName : ocrApps) {
            PackageInfo packageInfo = apps.get(packageName);
            if (packageInfo == null || packageInfo.applicationInfo == null) continue;
            names.add(packageInfo.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager()).toString());
        }
        return names;
    }

    public void tryStartActions(Class<? extends StartAction> clazz) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;

        for (Task task : Saver.getInstance().getTasks(clazz)) {
            for (Action action : task.getActions(clazz)) {
                StartAction startAction = (StartAction) action;
                if (startAction.isEnable() && startAction.ready()) service.runTask(task, startAction);
            }
        }
    }

    public void tryStartShareTask(PinObject pinObject) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;

        Map<Action, Task> tasks = new HashMap<>();
        for (Task task : Saver.getInstance().getTasks(ReceivedShareStartAction.class)) {
            for (Action action : task.getActions(ReceivedShareStartAction.class)) {
                Pin connectToAblePin = action.getPins().stream().filter(p -> p.isSameClass(pinObject.getClass()) && p.isOut()).findFirst().orElse(null);
                if (connectToAblePin != null) {
                    Task copy = task.copy();
                    Action actionCopy = copy.getAction(action.getId());
                    Pin pinById = actionCopy.getPinById(connectToAblePin.getId());
                    pinById.setValue(pinObject);
                    tasks.put(actionCopy, copy);
                }
            }
        }
        if (tasks.isEmpty()) return;
        if (tasks.size() == 1) {
            Action action = tasks.keySet().iterator().next();
            Task task = tasks.get(action);
            service.runTask(task, (StartAction) action);
            return;
        }
        List<ChoiceExecuteFloatView.Choice> choices = new ArrayList<>();
        tasks.forEach((action, task) -> choices.add(new ChoiceExecuteFloatView.Choice(action.getId(), task.getTitle(), null)));
        ChoiceExecuteFloatView.showChoice(choices, result -> {
            Action action = tasks.keySet().stream().filter(a -> a.getId().equals(result)).findFirst().orElse(null);
            if (action == null) return;
            Task task = tasks.get(action);
            service.runTask(task, (StartAction) action);
        });
    }

    public void tryShowManualPlayView(boolean show) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;

        List<ManualExecuteInfo> normalList = new ArrayList<>();
        List<ManualExecuteInfo> singleShowList = new ArrayList<>();

        if (show) {
            for (Task task : Saver.getInstance().getTasks(ManualStartAction.class)) {
                for (Action action : task.getActions(ManualStartAction.class)) {
                    ManualStartAction startAction = (ManualStartAction) action;
                    if (startAction.isEnable() && startAction.ready()) {
                        if (startAction.isSingleShow()) {
                            singleShowList.add(new ManualExecuteInfo(task, startAction));
                        } else {
                            normalList.add(new ManualExecuteInfo(task, startAction));
                        }
                    }
                }
            }
        }

        // 手动悬浮窗显示限制
        int playType = SettingSaver.getInstance().getManualPlayShowType();
        if (playType == 0 || (playType == 1 && getPhoneState() != PhoneState.ON)) {
            normalList.clear();
            singleShowList.clear();
        }

        PlayFloatView.showActions(normalList);
        SinglePlayView.showActions(singleShowList);
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
        if (packageActivity != null && packageActivity.packageName.equals(packageName) && packageActivity.activityName.equals(activityName)) return false;
        packageActivity = new PackageActivity(packageName, activityName);
        return true;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(NotificationType type, String packageName, Map<String, String> content) {
        notification = new Notification(type, packageName, content);
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
        return AppUtil.getPhoneState(MainApplication.getInstance());
    }

    public void onPhoneStateChanged() {
        tryStartActions(ScreenStartAction.class);
        if (packageActivity == null) return;
        tryShowManualPlayView(!packageActivity.activityName.equals(MainActivity.class.getName()));
    }

    public List<NotworkState> getNetworkState() {
        return networkState;
    }

    public void setNetworkState(List<NotworkState> networkState) {
        this.networkState = networkState;
        tryStartActions(NetworkStartAction.class);
    }

    public String getLastClipboard() {
        return lastClipboard;
    }

    public void setLastClipboard(String lastClipboard) {
        this.lastClipboard = lastClipboard;
    }

    public static class ShortcutInfo {
        public final String packageName;
        public String activityClass;
        public String id;
        public String title;
        public int icon;
        public String intent;

        public ShortcutInfo(String packageName) {
            this.packageName = packageName;
        }

        public boolean isValid() {
            return activityClass != null && id != null && title != null && intent != null;
        }
    }

    public record PackageActivity(String packageName, String activityName) {
    }

    public record Notification(NotificationType type, String packageName, Map<String, String> content) {
    }

    public record BatteryInfo(int percent, BatteryState status) {
    }

    public record BluetoothInfo(String bluetoothAddress, String bluetoothName, boolean active) {
    }

    public record ManualExecuteInfo(Task task, ManualStartAction action) {
    }

    public enum PhoneState {OFF, LOCKED, ON}

    public enum NotworkState {NONE, WIFI, MOBILE, VPN}

    public enum BatteryState {UNKNOWN, CHARGING, DISCHARGING, NOT_CHARGING, FULL}

    public enum NotificationType {NOTIFICATION, TOAST}
}
