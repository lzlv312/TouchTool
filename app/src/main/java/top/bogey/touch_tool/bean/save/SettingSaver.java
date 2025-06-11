package top.bogey.touch_tool.bean.save;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.bogey.touch_tool.service.KeepAliveService;

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
    private static final String RUN_TIMES = "RUN_TIMES";                                // 运行次数
    private static final String RUNNING_ERROR = "RUNNING_ERROR";                        // 运行错误

    private static final String ENABLE_TIPS = "ENABLE_TIPS";                            // 功能启用提示
    private static final String PLAY_VIEW_STATE = "PLAY_VIEW_STATE";                    // 手动执行悬浮窗状态
    private static final String PLAY_VIEW_POS = "PLAY_VIEW_POS";                        // 手动执行悬浮窗位置
    private static final String CHOICE_VIEW_POS = "CHOICE_VIEW_POS";                    // 选择执行悬浮窗位置
    private static final String SELECT_NODE_TYPE = "SELECT_NODE_TYPE";                  // 选择控件方式

    private static final String FAV_TAGS = "FAV_TAGS";                                  // 收藏的标签

    // 设置
    private static final String ENABLED = "ENABLED";                                    // 功能是否开启
    private static final String HIDE_BACK = "HIDE_BACK";                                // 隐藏后台
    private static final String FORGE_SERVICE = "FORGE_SERVICE";                        // 前台服务
    private static final String AUTO_START = "AUTO_START";                              // 自启动

    private static final String SUPER_USER = "SUPER_USER";                              // 超级用户
    private static final String MANUAL_PLAY = "MANUAL_PLAY";                            // 手动执行
    private static final String OCR = "OCR";                                            // 文字识别
    private static final String ALARM = "ALARM";                                        // 精确定时
    private static final String BLUETOOTH = "BLUETOOTH";                                // 蓝牙监听

    private static final String SHOW_TOUCH = "SHOW_TOUCH";                              // 手势轨迹
    private static final String SHOW_TARGET_AREA = "SHOW_TARGET_AREA";                  // 标记目标区域
    private static final String START_TIPS = "START_TIPS";                              // 任务运行提示

    private static final String SUPPORT_FREE_FORM = "SUPPORT_FREE_FORM";                // 小窗支持
    private static final String THEME = "THEME";                                        // 深色模式
    private static final String COLOR = "COLOR";                                        // 动态颜色
    private static final String PLAY_VIEW_PADDING = "PLAY_VIEW_PADDING";                // 手动执行悬浮窗偏移

    private static final MMKV mmkv = MMKV.defaultMMKV();

    public void init(Activity activity) {
        setHideBack(activity, isHideBack());
        setTheme(getTheme());
        setForgeServiceEnabled(activity, isForgeServiceEnabled());
    }

    public void initColor(Application application) {
        DynamicColors.applyToActivitiesIfAvailable(application, new DynamicColorsOptions.Builder().setPrecondition((act, theme) -> isDynamicColorTheme()).build());
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

    public boolean isEnableTips() {
        return mmkv.decodeBool(ENABLE_TIPS, false);
    }

    public void setEnableTips(boolean enable) {
        mmkv.encode(ENABLE_TIPS, enable);
    }

    public boolean isPlayViewExpand() {
        return mmkv.decodeBool(PLAY_VIEW_STATE, false);
    }

    public void setPlayViewExpand(boolean enable) {
        mmkv.encode(PLAY_VIEW_STATE, enable);
    }

    public Point getPlayViewPos() {
        return mmkv.decodeParcelable(PLAY_VIEW_POS, Point.class, new Point(0, 0));
    }

    public void setPlayViewPos(Point pos) {
        mmkv.encode(PLAY_VIEW_POS, pos);
    }

    public Point getChoiceViewPos() {
        return mmkv.decodeParcelable(CHOICE_VIEW_POS, Point.class, new Point(0, 0));
    }

    public void setChoiceViewPos(Point pos) {
        mmkv.encode(CHOICE_VIEW_POS, pos);
    }

    public int getSelectNodeType() {
        return mmkv.decodeInt(SELECT_NODE_TYPE, 0);
    }

    public void setSelectNodeType(int type) {
        mmkv.encode(SELECT_NODE_TYPE, type);
    }

    public Set<String> getFavTags() {
        return mmkv.decodeStringSet(FAV_TAGS, new HashSet<>());
    }

    public void setFavTags(Set<String> tags) {
        mmkv.encode(FAV_TAGS, tags);
    }

    // 设置

    public boolean isEnabled() {
        return mmkv.decodeBool(ENABLED, false);
    }

    public void setEnabled(boolean enable) {
        mmkv.encode(ENABLED, enable);
    }

    public boolean isHideBack() {
        return mmkv.decodeBool(HIDE_BACK, false);
    }

    public void setHideBack(Activity activity, boolean enable) {
        mmkv.encode(HIDE_BACK, enable);
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

    public boolean isForgeServiceEnabled() {
        return mmkv.decodeBool(FORGE_SERVICE, false);
    }

    public void setForgeServiceEnabled(Context context, boolean enable) {
        mmkv.encode(FORGE_SERVICE, enable);
        Intent intent = new Intent(context, KeepAliveService.class);
        if (enable) context.startService(intent);
        else context.stopService(intent);
    }

    public boolean isAutoStart() {
        return mmkv.decodeBool(AUTO_START, false);
    }

    public void setAutoStart(boolean enable) {
        mmkv.encode(AUTO_START, enable);
    }


    public int getSuperUser() {
        return mmkv.decodeInt(SUPER_USER, 0);
    }

    public void setSuperUser(int type) {
        mmkv.encode(SUPER_USER, type);
    }

    public int getManualPlayType() {
        return mmkv.decodeInt(MANUAL_PLAY, 1);
    }

    public void setManualPlayType(int type) {
        mmkv.encode(MANUAL_PLAY, type);
    }

    public boolean isOcrEnabled() {
        return mmkv.decodeBool(OCR, false);
    }

    public void setOcrEnabled(boolean enable) {
        mmkv.encode(OCR, enable);
    }

    public boolean isAlarmEnabled() {
        return mmkv.decodeBool(ALARM, false);
    }

    public void setAlarmEnabled(boolean enable) {
        mmkv.encode(ALARM, enable);
    }

    public boolean isBluetoothEnabled() {
        return mmkv.decodeBool(BLUETOOTH, false);
    }

    public void setBluetoothEnabled(boolean enable) {
        mmkv.encode(BLUETOOTH, enable);
    }


    public boolean isShowTouch() {
        return mmkv.decodeBool(SHOW_TOUCH, false);
    }

    public void setShowTouch(boolean enable) {
        mmkv.encode(SHOW_TOUCH, enable);
    }

    public boolean isShowTargetArea() {
        return mmkv.decodeBool(SHOW_TARGET_AREA, false);
    }

    public void setShowTargetArea(boolean enable) {
        mmkv.encode(SHOW_TARGET_AREA, enable);
    }

    public boolean isShowStartTips() {
        return mmkv.decodeBool(START_TIPS, true);
    }

    public void setShowStartTips(boolean enable) {
        mmkv.encode(START_TIPS, enable);
    }


    public boolean isSupportFreeForm() {
        return mmkv.decodeBool(SUPPORT_FREE_FORM, false);
    }

    public void setSupportFreeForm(boolean enable) {
        mmkv.encode(SUPPORT_FREE_FORM, enable);
    }

    public int getTheme() {
        return mmkv.decodeInt(THEME, 0);
    }

    public void setTheme(int theme) {
        mmkv.encode(THEME, theme);
        AppCompatDelegate.setDefaultNightMode(theme - 1);
    }

    public boolean isDynamicColorTheme() {
        return mmkv.decodeBool(COLOR, true);
    }

    public void setDynamicColorTheme(Activity activity, boolean enable) {
        mmkv.encode(COLOR, enable);
        activity.recreate();
    }

    public int getPlayViewPadding() {
        return mmkv.decodeInt(PLAY_VIEW_PADDING, 0);
    }

    public void setPlayViewPadding(int padding) {
        mmkv.encode(PLAY_VIEW_PADDING, padding);
    }

}
