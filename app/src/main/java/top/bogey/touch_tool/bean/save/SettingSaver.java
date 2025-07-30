package top.bogey.touch_tool.bean.save;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.tencent.mmkv.MMKV;

import java.util.List;

import top.bogey.touch_tool.service.KeepAliveService;
import top.bogey.touch_tool.service.notification.NotificationService;

public class SettingSaver {
    private static SettingSaver instance;

    public static SettingSaver getInstance() {
        synchronized (SettingSaver.class) {
            if (instance == null) {
                instance = new SettingSaver();
            }
        }
        return instance;
    }

    // 记录
    private static final String RUN_TIMES = "RUN_TIMES";                                                // 运行次数
    private static final String RUNNING_ERROR = "RUNNING_ERROR";                                        // 运行错误

    private static final String SHOW_SERVICE_ENABLE_TIPS = "SERVICE_ENABLE_TIPS";                       // 功能启用提示
    private static final String MANUAL_PLAY_VIEW_STATE = "MANUAL_PLAY_VIEW_STATE";                      // 手动执行悬浮窗状态
    private static final String MANUAL_PLAY_VIEW_POS = "MANUAL_PLAY_VIEW_POS";                          // 手动执行悬浮窗位置
    private static final String MANUAL_CHOICE_VIEW_POS = "MANUAL_CHOICE_VIEW_POS";                      // 选择执行悬浮窗位置
    private static final String PICK_NODE_TYPE = "PICK_NODE_TYPE";                                      // 选择控件方式

    private static final String LAST_GROUP = "LAST_GROUP";                                              // 上次打开的分组
    private static final String LAST_SUB_GROUP = "LAST_SUB_GROUP";                                      // 上次打开的次级分组


    // 设置
    private static final String SERVICE_ENABLED = "SERVICE_ENABLED";                                    // 功能是否开启
    private static final String HIDE_APP_BACKGROUND = "HIDE_APP_BACKGROUND";                            // 隐藏后台
    private static final String KEEP_ALIVE_FOREGROUND_SERVICE = "KEEP_ALIVE_FOREGROUND_SERVICE";        // 前台保活服务
    private static final String BOOT_COMPLETED_AUTO_START = "BOOT_COMPLETED_AUTO_START";                // 开机自启动

    private static final String SUPER_USER_TYPE = "SUPER_USER_TYPE";                                    // 超级用户
    private static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";                                // 通知来源
    private static final String EXACT_ALARM = "EXACT_ALARM";                                            // 精确定时
    private static final String BLUETOOTH = "BLUETOOTH";                                                // 蓝牙监听

    private static final String SHOW_GESTURE_TRACK = "SHOW_GESTURE_TRACK";                              // 显示手势轨迹
    private static final String SHOW_NODE_AREA = "SHOW_NODE_AREA";                                      // 标记目标控件区域
    private static final String SHOW_TASK_START_TIPS = "SHOW_TASK_START_TIPS";                          // 任务开始运行提示

    private static final String DEFAULT_CARD_TYPE = "DEFAULT_CARD_TYPE";                                // 默认卡片展开类型
    private static final String SUPPORT_FREE_FORM = "SUPPORT_FREE_FORM";                                // 小窗支持
    private static final String NIGHT_MODE_TYPE = "NIGHT_MODE_TYPE";                                    // 深色模式
    private static final String DYNAMIC_COLOR = "DYNAMIC_COLOR";                                        // 动态颜色

    private static final String MANUAL_PLAY_SHOW_TYPE = "MANUAL_PLAY_SHOW_TYPE";                        // 手动执行什么时候显示
    private static final String MANUAL_PLAY_PAUSE_TYPE = "MANUAL_PLAY_PAUSE_TYPE";                      // 手动执行暂停模式
    private static final String MANUAL_PLAY_HIDE_TYPE = "MANUAL_PLAY_HIDE_TYPE";                        // 手动执行什么时候隐藏
    private static final String MANUAL_PLAY_VIEW_PADDING = "MANUAL_PLAY_VIEW_PADDING";                  // 手动执行悬浮窗偏移
    private static final String MANUAL_PLAY_VIEW_EXPAND_SIZE = "MANUAL_PLAY_VIEW_EXPAND_SIZE";          // 手动执行悬浮窗按钮容纳文本数量
    private static final String MANUAL_PLAY_VIEW_CLOSE_SIZE = "MANUAL_PLAY_VIEW_CLOSE_SIZE";            // 手动执行悬浮窗按钮收起时宽度
    private static final String MANUAL_PLAY_VIEW_SINGLE_SIZE = "MANUAL_PLAY_VIEW_SINGLE_SIZE";          // 手动执行悬浮窗独立按钮宽度

    private static final MMKV mmkv = MMKV.defaultMMKV();

    public void init(Activity activity) {
        setHideAppBackground(activity, isHideAppBackground());
        setNightModeType(getNightModeType());
        setKeepAliveForegroundServiceEnabled(activity, isKeepAliveForegroundServiceEnabled());
    }

    public void initColor(Application application) {
        DynamicColors.applyToActivitiesIfAvailable(application, new DynamicColorsOptions.Builder().setPrecondition((act, theme) -> isDynamicColorTheme()).build());
        Log.d("TAG", "initColor: ");
    }

    // 记录

    public int getRunTimes() {
        return mmkv.decodeInt(RUN_TIMES, 0);
    }

    public void addRunTimes() {
        mmkv.encode(RUN_TIMES, getRunTimes() + 1);
    }

    public String getRunningError() {
        return mmkv.decodeString(RUNNING_ERROR, "");
    }

    public void setRunningError(String error) {
        mmkv.encode(RUNNING_ERROR, error);
    }

    public boolean isShowServiceEnableTips() {
        return mmkv.decodeBool(SHOW_SERVICE_ENABLE_TIPS, false);
    }

    public void setShowServiceEnableTips(boolean enable) {
        mmkv.encode(SHOW_SERVICE_ENABLE_TIPS, enable);
    }

    public boolean getManualPlayViewState() {
        return mmkv.decodeBool(MANUAL_PLAY_VIEW_STATE, false);
    }

    public void setManualPlayViewState(boolean enable) {
        mmkv.encode(MANUAL_PLAY_VIEW_STATE, enable);
    }

    public Point getManualPlayViewPos() {
        return mmkv.decodeParcelable(MANUAL_PLAY_VIEW_POS, Point.class, new Point(0, 0));
    }

    public void setManualPlayViewPos(Point pos) {
        mmkv.encode(MANUAL_PLAY_VIEW_POS, pos);
    }

    public Point getManualChoiceViewPos() {
        return mmkv.decodeParcelable(MANUAL_CHOICE_VIEW_POS, Point.class, new Point(0, 0));
    }

    public void setManualChoiceViewPos(Point pos) {
        mmkv.encode(MANUAL_CHOICE_VIEW_POS, pos);
    }

    public int getPickNodeType() {
        return mmkv.decodeInt(PICK_NODE_TYPE, 0);
    }

    public void setPickNodeType(int type) {
        mmkv.encode(PICK_NODE_TYPE, type);
    }

    public String getLastGroup() {
        return mmkv.decodeString(LAST_GROUP, "");
    }

    public void setLastGroup(String group) {
        mmkv.encode(LAST_GROUP, group);
    }


    public String getLastSubGroup() {
        return mmkv.decodeString(LAST_SUB_GROUP, "");
    }

    public void setLastSubGroup(String group) {
        mmkv.encode(LAST_SUB_GROUP, group);
    }

    // 设置

    public boolean isServiceEnabled() {
        return mmkv.decodeBool(SERVICE_ENABLED, false);
    }

    public void setServiceEnabled(boolean enable) {
        mmkv.encode(SERVICE_ENABLED, enable);
    }

    public boolean isHideAppBackground() {
        return mmkv.decodeBool(HIDE_APP_BACKGROUND, false);
    }

    public void setHideAppBackground(Activity activity, boolean enable) {
        mmkv.encode(HIDE_APP_BACKGROUND, enable);
        int taskId = activity.getTaskId();
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.AppTask> taskList = manager.getAppTasks();
            if (taskList != null) {
                for (ActivityManager.AppTask task : taskList) {
                    if (task.getTaskInfo().id == taskId) task.setExcludeFromRecents(enable);
                }
            }
        }
    }

    public boolean isKeepAliveForegroundServiceEnabled() {
        return mmkv.decodeBool(KEEP_ALIVE_FOREGROUND_SERVICE, false);
    }

    public void setKeepAliveForegroundServiceEnabled(Context context, boolean enable) {
        mmkv.encode(KEEP_ALIVE_FOREGROUND_SERVICE, enable);
        Intent intent = new Intent(context, KeepAliveService.class);
        if (enable) context.startService(intent);
        else context.stopService(intent);
    }

    public boolean isBootCompletedAutoStart() {
        return mmkv.decodeBool(BOOT_COMPLETED_AUTO_START, false);
    }

    public void setBootCompletedAutoStart(boolean enable) {
        mmkv.encode(BOOT_COMPLETED_AUTO_START, enable);
    }


    public int getSuperUserType() {
        return mmkv.decodeInt(SUPER_USER_TYPE, 0);
    }

    public void setSuperUserType(int type) {
        mmkv.encode(SUPER_USER_TYPE, type);
    }

    public int getNotificationType() {
        int type = mmkv.decodeInt(NOTIFICATION_TYPE, 0);
        if (type == 1) type = NotificationService.isEnabled() ? 1 : 0;
        return type;
    }

    public void setNotificationType(int type) {
        mmkv.encode(NOTIFICATION_TYPE, type);
    }

    public boolean isExactAlarmEnabled() {
        return mmkv.decodeBool(EXACT_ALARM, false);
    }

    public void setExactAlarmEnabled(boolean enable) {
        mmkv.encode(EXACT_ALARM, enable);
    }

    public boolean isBluetoothEnabled() {
        return mmkv.decodeBool(BLUETOOTH, false);
    }

    public void setBluetoothEnabled(boolean enable) {
        mmkv.encode(BLUETOOTH, enable);
    }


    public boolean isShowGestureTrack() {
        return mmkv.decodeBool(SHOW_GESTURE_TRACK, false);
    }

    public void setShowGestureTrack(boolean enable) {
        mmkv.encode(SHOW_GESTURE_TRACK, enable);
    }

    public boolean isShowNodeArea() {
        return mmkv.decodeBool(SHOW_NODE_AREA, false);
    }

    public void setShowNodeArea(boolean enable) {
        mmkv.encode(SHOW_NODE_AREA, enable);
    }

    public boolean isShowTaskStartTips() {
        return mmkv.decodeBool(SHOW_TASK_START_TIPS, true);
    }

    public void setShowTaskStartTips(boolean enable) {
        mmkv.encode(SHOW_TASK_START_TIPS, enable);
    }


    public int getDefaultCardExpandType() {
        return mmkv.decodeInt(DEFAULT_CARD_TYPE, 1);
    }

    public void setDefaultCardExpandType(int type) {
        mmkv.encode(DEFAULT_CARD_TYPE, type);
    }

    public boolean isSupportFreeForm() {
        return mmkv.decodeBool(SUPPORT_FREE_FORM, false);
    }

    public void setSupportFreeForm(boolean enable) {
        mmkv.encode(SUPPORT_FREE_FORM, enable);
    }

    public int getNightModeType() {
        return mmkv.decodeInt(NIGHT_MODE_TYPE, 0);
    }

    public void setNightModeType(int type) {
        mmkv.encode(NIGHT_MODE_TYPE, type);
        AppCompatDelegate.setDefaultNightMode(type - 1);
    }

    public boolean isDynamicColorTheme() {
        return mmkv.decodeBool(DYNAMIC_COLOR, true);
    }

    public void setDynamicColorTheme(Activity activity, boolean enable) {
        mmkv.encode(DYNAMIC_COLOR, enable);
        activity.recreate();
    }

    public int getManualPlayShowType() {
        return mmkv.decodeInt(MANUAL_PLAY_SHOW_TYPE, 1);
    }

    public void setManualPlayShowType(int type) {
        mmkv.encode(MANUAL_PLAY_SHOW_TYPE, type);
    }

    public int getManualPlayPauseType() {
        return mmkv.decodeInt(MANUAL_PLAY_PAUSE_TYPE, 0);
    }

    public void setManualPlayPauseType(int type) {
        mmkv.encode(MANUAL_PLAY_PAUSE_TYPE, type);
    }

    public int getManualPlayHideType() {
        return mmkv.decodeInt(MANUAL_PLAY_HIDE_TYPE, 0);
    }

    public void setManualPlayHideType(int type) {
        mmkv.encode(MANUAL_PLAY_HIDE_TYPE, type);
    }

    public int getManualPlayViewPadding() {
        return mmkv.decodeInt(MANUAL_PLAY_VIEW_PADDING, 0);
    }

    public void setManualPlayViewPadding(int padding) {
        mmkv.encode(MANUAL_PLAY_VIEW_PADDING, padding);
    }

    public int getManualPlayViewExpandSize() {
        return mmkv.decodeInt(MANUAL_PLAY_VIEW_EXPAND_SIZE, 1);
    }

    public void setManualPlayViewExpandSize(int size) {
        mmkv.encode(MANUAL_PLAY_VIEW_EXPAND_SIZE, size);
    }

    public int getManualPlayViewCloseSize() {
        return mmkv.decodeInt(MANUAL_PLAY_VIEW_CLOSE_SIZE, 1);
    }

    public void setManualPlayViewCloseSize(int size) {
        mmkv.encode(MANUAL_PLAY_VIEW_CLOSE_SIZE, size);
    }

    public int getManualPlayViewSingleSize() {
        return mmkv.decodeInt(MANUAL_PLAY_VIEW_SINGLE_SIZE, 1);
    }

    public void setManualPlayViewSingleSize(int size) {
        mmkv.encode(MANUAL_PLAY_VIEW_SINGLE_SIZE, size);
    }
}
