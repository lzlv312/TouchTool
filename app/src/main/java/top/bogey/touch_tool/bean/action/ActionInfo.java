package top.bogey.touch_tool.bean.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.reflect.Constructor;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.color.FindColorsAction;
import top.bogey.touch_tool.bean.action.logic.ChoiceExecuteAction;
import top.bogey.touch_tool.bean.action.logic.ForLoopAction;
import top.bogey.touch_tool.bean.action.logic.IfConditionAction;
import top.bogey.touch_tool.bean.action.logic.ParallelExecuteAction;
import top.bogey.touch_tool.bean.action.logic.RandomExecuteAction;
import top.bogey.touch_tool.bean.action.logic.SequenceExecuteAction;
import top.bogey.touch_tool.bean.action.logic.SwitchAction;
import top.bogey.touch_tool.bean.action.logic.WaitConditionAction;
import top.bogey.touch_tool.bean.action.logic.WhileLoopAction;
import top.bogey.touch_tool.bean.action.normal.DelayAction;
import top.bogey.touch_tool.bean.action.normal.LoggerAction;
import top.bogey.touch_tool.bean.action.normal.StickCloseAction;
import top.bogey.touch_tool.bean.action.normal.StickCloseAllAction;
import top.bogey.touch_tool.bean.action.normal.StickScreenAction;
import top.bogey.touch_tool.bean.action.start.ApplicationStartAction;
import top.bogey.touch_tool.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool.bean.action.start.BluetoothStartAction;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool.bean.action.start.NotificationStartAction;
import top.bogey.touch_tool.bean.action.start.OutCallStartAction;
import top.bogey.touch_tool.bean.action.start.ScreenStartAction;
import top.bogey.touch_tool.bean.action.start.TimeStartAction;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.card.DelayActionCard;
import top.bogey.touch_tool.ui.blueprint.card.NormalActionCard;

public class ActionInfo {
    // 开始动作
    private final static ActionInfo MANUAL_START_INFO = new ActionInfo(ActionType.MANUAL_START, ManualStartAction.class, R.drawable.icon_hand, R.string.manual_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo APPLICATION_START_INFO = new ActionInfo(ActionType.APPLICATION_START, ApplicationStartAction.class, R.drawable.icon_app, R.string.application_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo TIME_START_INFO = new ActionInfo(ActionType.TIME_START, TimeStartAction.class, R.drawable.icon_time, R.string.time_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo NOTIFICATION_START_INFO = new ActionInfo(ActionType.NOTIFICATION_START, NotificationStartAction.class, R.drawable.icon_notification, R.string.notification_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo NETWORK_START_INFO = new ActionInfo(ActionType.NETWORK_START, NetworkStartAction.class, R.drawable.icon_network, R.string.network_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo BATTERY_START_INFO = new ActionInfo(ActionType.BATTERY_START, BatteryStartAction.class, R.drawable.icon_battery, R.string.battery_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo SCREEN_START_INFO = new ActionInfo(ActionType.SCREEN_START, ScreenStartAction.class, R.drawable.icon_screen, R.string.screen_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo BLUETOOTH_START_INFO = new ActionInfo(ActionType.BLUETOOTH_START, BluetoothStartAction.class, R.drawable.icon_bluetooth, R.string.bluetooth_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo OUT_CALL_START_INFO = new ActionInfo(ActionType.OUT_CALL_START, OutCallStartAction.class, R.drawable.icon_auto_start, R.string.out_call_start_action, 0, 0, NormalActionCard.class);

    // 逻辑动作
    private final static ActionInfo IF_LOGIC_INFO = new ActionInfo(ActionType.IF_LOGIC, IfConditionAction.class, R.drawable.icon_condition, R.string.if_action, R.string.if_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo WAIT_IF_LOGIC_INFO = new ActionInfo(ActionType.WAIT_IF_LOGIC, WaitConditionAction.class, R.drawable.icon_condition, R.string.wait_if_action, R.string.wait_if_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo SWITCH_LOGIC_INFO = new ActionInfo(ActionType.SWITCH_LOGIC, SwitchAction.class, R.drawable.icon_condition, R.string.switch_action, R.string.switch_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHOICE_LOGIC_INFO = new ActionInfo(ActionType.CHOICE_LOGIC, ChoiceExecuteAction.class, R.drawable.icon_condition, R.string.choice_action, R.string.choice_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FOR_LOGIC_INFO = new ActionInfo(ActionType.FOR_LOGIC, ForLoopAction.class, R.drawable.icon_for_loop, R.string.for_loop_action, R.string.for_loop_action_desc, R.string.for_loop_action_help, NormalActionCard.class);
    private final static ActionInfo WHILE_LOGIC_INFO = new ActionInfo(ActionType.WHILE_LOGIC, WhileLoopAction.class, R.drawable.icon_condition_while, R.string.while_loop_action, R.string.while_loop_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo SEQUENCE_LOGIC_INFO = new ActionInfo(ActionType.SEQUENCE_LOGIC, SequenceExecuteAction.class, R.drawable.icon_sequence, R.string.sequence_action, R.string.sequence_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo RANDOM_LOGIC_INFO = new ActionInfo(ActionType.RANDOM_LOGIC, RandomExecuteAction.class, R.drawable.icon_random, R.string.random_action, R.string.random_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo PARALLEL_LOGIC_INFO = new ActionInfo(ActionType.PARALLEL_LOGIC, ParallelExecuteAction.class, R.drawable.icon_parallel, R.string.parallel_action, R.string.parallel_action_desc, 0, NormalActionCard.class);

    // 常用动作
    private final static ActionInfo DELAY_INFO = new ActionInfo(ActionType.DELAY, DelayAction.class, R.drawable.icon_delay, R.string.delay_action, R.string.delay_action_desc, R.string.delay_action_help, DelayActionCard.class);
    private final static ActionInfo LOG_INFO = new ActionInfo(ActionType.LOG, LoggerAction.class, R.drawable.icon_log, R.string.log_action, R.string.log_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STICK_INFO = new ActionInfo(ActionType.STICK, StickScreenAction.class, R.drawable.icon_home, R.string.stick_screen_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo CLOSE_STICK_INFO = new ActionInfo(ActionType.CLOSE_STICK, StickCloseAction.class, R.drawable.icon_home, R.string.stick_close_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo CLOSE_ALL_STICK_INFO = new ActionInfo(ActionType.CLOSE_ALL_STICK, StickCloseAllAction.class, R.drawable.icon_home, R.string.stick_close_all_action, 0, 0, NormalActionCard.class);

    private final static ActionInfo FIND_COLORS_INFO = new ActionInfo(ActionType.FIND_COLORS, FindColorsAction.class, R.drawable.icon_color, R.string.find_colors_action, R.string.find_colors_action_desc, 0, NormalActionCard.class);

    public static ActionInfo getActionInfo(ActionType type) {
        return switch (type) {
            case MANUAL_START -> MANUAL_START_INFO;
            case APPLICATION_START -> APPLICATION_START_INFO;
            case TIME_START -> TIME_START_INFO;
            case NOTIFICATION_START -> NOTIFICATION_START_INFO;
            case NETWORK_START -> NETWORK_START_INFO;
            case BATTERY_START -> BATTERY_START_INFO;
            case SCREEN_START -> SCREEN_START_INFO;
            case BLUETOOTH_START -> BLUETOOTH_START_INFO;
            case OUT_CALL_START -> OUT_CALL_START_INFO;

            case IF_LOGIC -> IF_LOGIC_INFO;
            case WAIT_IF_LOGIC -> WAIT_IF_LOGIC_INFO;
            case SWITCH_LOGIC -> SWITCH_LOGIC_INFO;
            case CHOICE_LOGIC -> CHOICE_LOGIC_INFO;
            case FOR_LOGIC -> FOR_LOGIC_INFO;
            case WHILE_LOGIC -> WHILE_LOGIC_INFO;
            case SEQUENCE_LOGIC -> SEQUENCE_LOGIC_INFO;
            case RANDOM_LOGIC -> RANDOM_LOGIC_INFO;
            case PARALLEL_LOGIC -> PARALLEL_LOGIC_INFO;

            case DELAY -> DELAY_INFO;
            case LOG -> LOG_INFO;
            case STICK -> STICK_INFO;
            case CLOSE_STICK -> CLOSE_STICK_INFO;
            case CLOSE_ALL_STICK -> CLOSE_ALL_STICK_INFO;

            case FIND_COLORS -> FIND_COLORS_INFO;
            default -> null;
        };
    }


    //-------------------------------------------------------------------------------------------------

    private final ActionType type;

    private final Class<? extends Action> clazz;

    @StringRes
    private final int title;

    @StringRes
    private final int description;

    @DrawableRes
    private final int icon;

    @StringRes
    private final int help;

    private final Class<? extends ActionCard> cardClass;

    private Action action;

    public ActionInfo(ActionType type, Class<? extends Action> clazz, int icon, int title, int description) {
        this(type, clazz, title, description, icon, 0);
    }

    public ActionInfo(ActionType type, Class<? extends Action> clazz, int icon, int title, int description, int help) {
        this(type, clazz, title, description, icon, help, NormalActionCard.class);
    }

    public ActionInfo(ActionType type, Class<? extends Action> clazz, @DrawableRes int icon, @StringRes int title, @StringRes int description, @StringRes int help, Class<? extends ActionCard> cardClass) {
        this.type = type;
        this.clazz = clazz;
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.help = help;
        this.cardClass = cardClass;

        try {
            Constructor<? extends Action> constructor = clazz.getConstructor();
            action = constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            action = null;
        }
    }

    public ActionType getType() {
        return type;
    }

    public Class<? extends Action> getClazz() {
        return clazz;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        if (title == 0) return "";
        return MainApplication.getInstance().getString(title);
    }

    public String getDescription() {
        if (description == 0) return "";
        return MainApplication.getInstance().getString(description);
    }

    public String getHelp() {
        if (help == 0) return "";
        return MainApplication.getInstance().getString(help);
    }

    public Class<? extends ActionCard> getCardClass() {
        return cardClass;
    }

    public Action getAction() {
        return action;
    }

    @NonNull
    @Override
    public String toString() {
        return "ActionInfo{" +
                "clazz=" + clazz +
                ", type=" + type +
                '}';
    }
}
