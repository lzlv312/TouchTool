package top.bogey.touch_tool.bean.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.reflect.Constructor;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.area.AreaFromIntegerAction;
import top.bogey.touch_tool.bean.action.area.AreaToIntegerAction;
import top.bogey.touch_tool.bean.action.area.CheckAreaContainPosAction;
import top.bogey.touch_tool.bean.action.area.CheckAreaRelationAction;
import top.bogey.touch_tool.bean.action.area.GetAreaCenterAction;
import top.bogey.touch_tool.bean.action.area.GetAreaIntersectionAction;
import top.bogey.touch_tool.bean.action.area.PickAreaAction;
import top.bogey.touch_tool.bean.action.bool.BooleanAndAction;
import top.bogey.touch_tool.bean.action.bool.BooleanNotAction;
import top.bogey.touch_tool.bean.action.bool.BooleanOrAction;
import top.bogey.touch_tool.bean.action.color.FindColorsAction;
import top.bogey.touch_tool.bean.action.color.GetColorAction;
import top.bogey.touch_tool.bean.action.image.CropImageAction;
import top.bogey.touch_tool.bean.action.image.FindImagesAction;
import top.bogey.touch_tool.bean.action.image.GetImageAction;
import top.bogey.touch_tool.bean.action.image.ImageEqualAction;
import top.bogey.touch_tool.bean.action.image.SaveImageAction;
import top.bogey.touch_tool.bean.action.list.ListAddAction;
import top.bogey.touch_tool.bean.action.list.ListAppendAction;
import top.bogey.touch_tool.bean.action.list.ListClearAction;
import top.bogey.touch_tool.bean.action.list.ListContainAction;
import top.bogey.touch_tool.bean.action.list.ListForeachAction;
import top.bogey.touch_tool.bean.action.list.ListGetAction;
import top.bogey.touch_tool.bean.action.list.ListIndexOfAction;
import top.bogey.touch_tool.bean.action.list.ListIsEmptyAction;
import top.bogey.touch_tool.bean.action.list.ListRemoveAction;
import top.bogey.touch_tool.bean.action.list.ListSetAction;
import top.bogey.touch_tool.bean.action.list.ListSizeAction;
import top.bogey.touch_tool.bean.action.list.MakeListAction;
import top.bogey.touch_tool.bean.action.logic.ChoiceExecuteAction;
import top.bogey.touch_tool.bean.action.logic.ForLoopAction;
import top.bogey.touch_tool.bean.action.logic.IfConditionAction;
import top.bogey.touch_tool.bean.action.logic.ParallelExecuteAction;
import top.bogey.touch_tool.bean.action.logic.RandomExecuteAction;
import top.bogey.touch_tool.bean.action.logic.SequenceExecuteAction;
import top.bogey.touch_tool.bean.action.logic.SwitchAction;
import top.bogey.touch_tool.bean.action.logic.WaitConditionAction;
import top.bogey.touch_tool.bean.action.logic.WhileLoopAction;
import top.bogey.touch_tool.bean.action.node.CheckNodeValidAction;
import top.bogey.touch_tool.bean.action.node.EditTextInputAction;
import top.bogey.touch_tool.bean.action.node.FindNodeByPathAction;
import top.bogey.touch_tool.bean.action.node.FindNodesByIdAction;
import top.bogey.touch_tool.bean.action.node.FindNodesByTextAction;
import top.bogey.touch_tool.bean.action.node.FindNodesInAreaAction;
import top.bogey.touch_tool.bean.action.node.GetNodeChildrenAction;
import top.bogey.touch_tool.bean.action.node.GetNodeInfoAction;
import top.bogey.touch_tool.bean.action.node.GetNodeParentAction;
import top.bogey.touch_tool.bean.action.node.GetWindowsAction;
import top.bogey.touch_tool.bean.action.node.NodeTouchAction;
import top.bogey.touch_tool.bean.action.normal.DelayAction;
import top.bogey.touch_tool.bean.action.normal.LoggerAction;
import top.bogey.touch_tool.bean.action.normal.StickCloseAction;
import top.bogey.touch_tool.bean.action.normal.StickCloseAllAction;
import top.bogey.touch_tool.bean.action.normal.StickScreenAction;
import top.bogey.touch_tool.bean.action.number.CheckNumberInValueArea;
import top.bogey.touch_tool.bean.action.number.MathExpressionAction;
import top.bogey.touch_tool.bean.action.number.NumberAbsAction;
import top.bogey.touch_tool.bean.action.number.NumberAddAction;
import top.bogey.touch_tool.bean.action.number.NumberDivAction;
import top.bogey.touch_tool.bean.action.number.NumberEqualAction;
import top.bogey.touch_tool.bean.action.number.NumberGreaterAction;
import top.bogey.touch_tool.bean.action.number.NumberLessAction;
import top.bogey.touch_tool.bean.action.number.NumberModAction;
import top.bogey.touch_tool.bean.action.number.NumberMulAction;
import top.bogey.touch_tool.bean.action.number.NumberRandomAction;
import top.bogey.touch_tool.bean.action.number.NumberSubAction;
import top.bogey.touch_tool.bean.action.number.NumberToIntegerAction;
import top.bogey.touch_tool.bean.action.number.NumberToValueArea;
import top.bogey.touch_tool.bean.action.point.PointFromIntegerAction;
import top.bogey.touch_tool.bean.action.point.PointOffsetAction;
import top.bogey.touch_tool.bean.action.point.PointToIntegerAction;
import top.bogey.touch_tool.bean.action.point.PointToTouchAction;
import top.bogey.touch_tool.bean.action.point.TouchAction;
import top.bogey.touch_tool.bean.action.start.ApplicationStartAction;
import top.bogey.touch_tool.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool.bean.action.start.BluetoothStartAction;
import top.bogey.touch_tool.bean.action.start.ManualStartAction;
import top.bogey.touch_tool.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool.bean.action.start.NotificationStartAction;
import top.bogey.touch_tool.bean.action.start.OutCallStartAction;
import top.bogey.touch_tool.bean.action.start.ScreenStartAction;
import top.bogey.touch_tool.bean.action.start.TimeStartAction;
import top.bogey.touch_tool.bean.action.string.FindOcrTextAction;
import top.bogey.touch_tool.bean.action.string.GetOcrTextAction;
import top.bogey.touch_tool.bean.action.string.StringAppendAction;
import top.bogey.touch_tool.bean.action.string.StringEqualAction;
import top.bogey.touch_tool.bean.action.string.StringFromObjectAction;
import top.bogey.touch_tool.bean.action.string.StringMatchAction;
import top.bogey.touch_tool.bean.action.string.StringReplaceAction;
import top.bogey.touch_tool.bean.action.string.StringSplitAction;
import top.bogey.touch_tool.bean.action.string.StringToNumberAction;
import top.bogey.touch_tool.bean.action.system.CaptureSwitchAction;
import top.bogey.touch_tool.bean.action.system.CheckCaptureReadyAction;
import top.bogey.touch_tool.bean.action.system.CheckInAppAction;
import top.bogey.touch_tool.bean.action.system.GetBatteryStatusAction;
import top.bogey.touch_tool.bean.action.system.GetCurrentAppAction;
import top.bogey.touch_tool.bean.action.system.GetDateAction;
import top.bogey.touch_tool.bean.action.system.GetNetworkStatusAction;
import top.bogey.touch_tool.bean.action.system.GetScreenStatusAction;
import top.bogey.touch_tool.bean.action.system.GetTimeAction;
import top.bogey.touch_tool.bean.action.system.OpenAppAction;
import top.bogey.touch_tool.bean.action.system.OpenUriSchemeAction;
import top.bogey.touch_tool.bean.action.system.PlayRingtoneAction;
import top.bogey.touch_tool.bean.action.system.ShareToAction;
import top.bogey.touch_tool.bean.action.system.StopRingtoneAction;
import top.bogey.touch_tool.bean.action.system.SwitchScreenAction;
import top.bogey.touch_tool.bean.action.system.TextToSpeechAction;
import top.bogey.touch_tool.bean.action.system.WriteToClipboardAction;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.card.DelayActionCard;
import top.bogey.touch_tool.ui.blueprint.card.NormalActionCard;

public class ActionInfo {
    // 开始动作
    private final static ActionInfo MANUAL_START_INFO = new ActionInfo(ActionType.MANUAL_START, ManualStartAction.class, R.drawable.icon_hand, R.string.manual_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo APPLICATION_START_INFO = new ActionInfo(ActionType.APPLICATION_START, ApplicationStartAction.class, R.drawable.icon_task, R.string.application_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo TIME_START_INFO = new ActionInfo(ActionType.TIME_START, TimeStartAction.class, R.drawable.icon_time, R.string.time_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo NOTIFICATION_START_INFO = new ActionInfo(ActionType.NOTIFICATION_START, NotificationStartAction.class, R.drawable.icon_notification, R.string.notification_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo NETWORK_START_INFO = new ActionInfo(ActionType.NETWORK_START, NetworkStartAction.class, R.drawable.icon_network, R.string.network_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo BATTERY_START_INFO = new ActionInfo(ActionType.BATTERY_START, BatteryStartAction.class, R.drawable.icon_battery, R.string.battery_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo SCREEN_START_INFO = new ActionInfo(ActionType.SCREEN_START, ScreenStartAction.class, R.drawable.icon_screen, R.string.screen_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo BLUETOOTH_START_INFO = new ActionInfo(ActionType.BLUETOOTH_START, BluetoothStartAction.class, R.drawable.icon_bluetooth, R.string.bluetooth_start_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo OUT_CALL_START_INFO = new ActionInfo(ActionType.OUT_CALL_START, OutCallStartAction.class, R.drawable.icon_auto_start, R.string.out_call_start_action, 0, 0, NormalActionCard.class);


    // 逻辑动作
    private final static ActionInfo IF_LOGIC_INFO = new ActionInfo(ActionType.IF_LOGIC, IfConditionAction.class, R.drawable.icon_condition, R.string.if_action, R.string.if_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo WAIT_IF_LOGIC_INFO = new ActionInfo(ActionType.WAIT_IF_LOGIC, WaitConditionAction.class, R.drawable.icon_wait_condition, R.string.wait_if_action, R.string.wait_if_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo SWITCH_LOGIC_INFO = new ActionInfo(ActionType.SWITCH_LOGIC, SwitchAction.class, R.drawable.icon_condition, R.string.switch_action, R.string.switch_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHOICE_LOGIC_INFO = new ActionInfo(ActionType.CHOICE_LOGIC, ChoiceExecuteAction.class, R.drawable.icon_condition, R.string.choice_action, R.string.choice_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FOR_LOGIC_INFO = new ActionInfo(ActionType.FOR_LOGIC, ForLoopAction.class, R.drawable.icon_for_loop, R.string.for_loop_action, R.string.for_loop_action_desc, R.string.for_loop_action_help, NormalActionCard.class);
    private final static ActionInfo WHILE_LOGIC_INFO = new ActionInfo(ActionType.WHILE_LOGIC, WhileLoopAction.class, R.drawable.icon_condition_while, R.string.while_loop_action, R.string.while_loop_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo SEQUENCE_LOGIC_INFO = new ActionInfo(ActionType.SEQUENCE_LOGIC, SequenceExecuteAction.class, R.drawable.icon_sequence, R.string.sequence_action, R.string.sequence_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo RANDOM_LOGIC_INFO = new ActionInfo(ActionType.RANDOM_LOGIC, RandomExecuteAction.class, R.drawable.icon_random, R.string.random_action, R.string.random_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo PARALLEL_LOGIC_INFO = new ActionInfo(ActionType.PARALLEL_LOGIC, ParallelExecuteAction.class, R.drawable.icon_parallel, R.string.parallel_action, R.string.parallel_action_desc, 0, NormalActionCard.class);


    // 通用动作
    private final static ActionInfo DELAY_INFO = new ActionInfo(ActionType.DELAY, DelayAction.class, R.drawable.icon_delay, R.string.delay_action, R.string.delay_action_desc, R.string.delay_action_help, DelayActionCard.class);
    private final static ActionInfo LOG_INFO = new ActionInfo(ActionType.LOG, LoggerAction.class, R.drawable.icon_log, R.string.log_action, R.string.log_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STICK_INFO = new ActionInfo(ActionType.STICK, StickScreenAction.class, R.drawable.icon_home, R.string.stick_screen_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo CLOSE_STICK_INFO = new ActionInfo(ActionType.CLOSE_STICK, StickCloseAction.class, R.drawable.icon_home, R.string.stick_close_action, 0, 0, NormalActionCard.class);
    private final static ActionInfo CLOSE_ALL_STICK_INFO = new ActionInfo(ActionType.CLOSE_ALL_STICK, StickCloseAllAction.class, R.drawable.icon_home, R.string.stick_close_all_action, 0, 0, NormalActionCard.class);


    // 系统动作
    private final static ActionInfo OPEN_APP_INFO = new ActionInfo(ActionType.OPEN_APP, OpenAppAction.class, R.drawable.icon_task, R.string.open_app_action, R.string.open_app_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo OPEN_URI_SCHEME_INFO = new ActionInfo(ActionType.OPEN_URI_SCHEME, OpenUriSchemeAction.class, R.drawable.icon_uri, R.string.open_uri_scheme_action, R.string.open_uri_scheme_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo SHARE_TO_INFO = new ActionInfo(ActionType.SHARE_TO, ShareToAction.class, R.drawable.icon_menu_history_forward, R.string.share_to_action, R.string.share_to_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo PLAY_RINGTONE_INFO = new ActionInfo(ActionType.PLAY_RINGTONE, PlayRingtoneAction.class, R.drawable.icon_notification, R.string.play_ringtone_action, R.string.play_ringtone_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STOP_RINGTONE_INFO = new ActionInfo(ActionType.STOP_RINGTONE, StopRingtoneAction.class, R.drawable.icon_notification, R.string.stop_ringtone_action, R.string.stop_ringtone_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo TEXT_TO_SPEECH_INFO = new ActionInfo(ActionType.TEXT_TO_SPEECH, TextToSpeechAction.class, R.drawable.icon_notification, R.string.text_to_speak_action, R.string.text_to_speak_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo WRITE_TO_CLIPBOARD_INFO = new ActionInfo(ActionType.WRITE_TO_CLIPBOARD, WriteToClipboardAction.class, R.drawable.icon_copy, R.string.write_to_clipboard_action, R.string.write_to_clipboard_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo SWITCH_SCREEN_INFO = new ActionInfo(ActionType.SWITCH_SCREEN, SwitchScreenAction.class, R.drawable.icon_screen, R.string.switch_screen_action, R.string.switch_screen_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_SCREEN_STATUS_INFO = new ActionInfo(ActionType.GET_SCREEN_STATUS, GetScreenStatusAction.class, R.drawable.icon_screen, R.string.get_screen_status_action, R.string.get_screen_status_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo SWITCH_CAPTURE_INFO = new ActionInfo(ActionType.SWITCH_CAPTURE, CaptureSwitchAction.class, R.drawable.icon_capture, R.string.capture_switch_action, R.string.capture_switch_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHECK_CAPTURE_READY_INFO = new ActionInfo(ActionType.CHECK_CAPTURE_READY, CheckCaptureReadyAction.class, R.drawable.icon_capture, R.string.check_capture_ready_action, R.string.check_capture_ready_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo GET_CURRENT_APPLICATION_INFO = new ActionInfo(ActionType.GET_CURRENT_APPLICATION, GetCurrentAppAction.class, R.drawable.icon_task, R.string.get_current_app_action, R.string.get_current_app_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHECK_IN_APPLICATION_INFO = new ActionInfo(ActionType.CHECK_IN_APPLICATION, CheckInAppAction.class, R.drawable.icon_task, R.string.check_in_app_action, R.string.check_in_app_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo GET_BATTERY_STATUS_INFO = new ActionInfo(ActionType.GET_BATTERY_STATUS, GetBatteryStatusAction.class, R.drawable.icon_battery, R.string.get_battery_status_action, R.string.get_battery_status_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo GET_NETWORK_STATUS_INFO = new ActionInfo(ActionType.GET_NETWORK_STATUS, GetNetworkStatusAction.class, R.drawable.icon_network, R.string.get_network_status_action, R.string.get_network_status_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo GET_CURRENT_DATE_INFO = new ActionInfo(ActionType.GET_CURRENT_DATE, GetDateAction.class, R.drawable.icon_date, R.string.get_date_action, R.string.get_date_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_CURRENT_TIME_INFO = new ActionInfo(ActionType.GET_CURRENT_TIME, GetTimeAction.class, R.drawable.icon_time, R.string.get_time_action, R.string.get_time_action_desc, 0, NormalActionCard.class);


    // 数值运算
    private final static ActionInfo NUMBER_ADD_INFO = new ActionInfo(ActionType.NUMBER_ADD, NumberAddAction.class, R.drawable.icon_number, R.string.number_add_action, R.string.number_add_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_SUB_INFO = new ActionInfo(ActionType.NUMBER_SUB, NumberSubAction.class, R.drawable.icon_number, R.string.number_subtract_action, R.string.number_subtract_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_MUL_INFO = new ActionInfo(ActionType.NUMBER_MUL, NumberMulAction.class, R.drawable.icon_number, R.string.number_multiply_action, R.string.number_multiply_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_DIV_INFO = new ActionInfo(ActionType.NUMBER_DIV, NumberDivAction.class, R.drawable.icon_number, R.string.number_divide_action, R.string.number_divide_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_MOD_INFO = new ActionInfo(ActionType.NUMBER_MOD, NumberModAction.class, R.drawable.icon_number, R.string.number_mod_action, R.string.number_mod_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_ABS_INFO = new ActionInfo(ActionType.NUMBER_ABS, NumberAbsAction.class, R.drawable.icon_number, R.string.number_abs_action, R.string.number_abs_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_EQUAL_INFO = new ActionInfo(ActionType.NUMBER_EQUAL, NumberEqualAction.class, R.drawable.icon_number, R.string.number_equal_action, R.string.number_equal_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_GREATER_INFO = new ActionInfo(ActionType.NUMBER_GREATER, NumberGreaterAction.class, R.drawable.icon_number, R.string.number_greater_action, R.string.number_greater_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_LESS_INFO = new ActionInfo(ActionType.NUMBER_LESS, NumberLessAction.class, R.drawable.icon_number, R.string.number_less_action, R.string.number_less_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_RANDOM_INFO = new ActionInfo(ActionType.NUMBER_RANDOM, NumberRandomAction.class, R.drawable.icon_number, R.string.number_random_action, R.string.number_random_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_TO_INT_INFO = new ActionInfo(ActionType.NUMBER_TO_INT, NumberToIntegerAction.class, R.drawable.icon_number, R.string.number_to_integer_action, R.string.number_mod_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo CHECK_NUMBER_IN_VALUE_AREA_INFO = new ActionInfo(ActionType.CHECK_NUMBER_IN_VALUE_AREA, CheckNumberInValueArea.class, R.drawable.icon_number, R.string.check_number_in_value_area_action, R.string.check_number_in_value_area_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo NUMBER_TO_VALUE_AREA_INFO = new ActionInfo(ActionType.NUMBER_TO_VALUE_AREA, NumberToValueArea.class, R.drawable.icon_number, R.string.number_to_value_area_action, R.string.number_to_value_area_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo MATH_EXPRESSION_INFO = new ActionInfo(ActionType.MATH_EXPRESSION, MathExpressionAction.class, R.drawable.icon_number, R.string.math_expression_action, R.string.math_expression_action_desc, 0, NormalActionCard.class);


    // 文本处理
    private final static ActionInfo STRING_FROM_OBJECT_INFO = new ActionInfo(ActionType.STRING_FROM_OBJECT, StringFromObjectAction.class, R.drawable.icon_text, R.string.string_from_object_action, R.string.string_from_object_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STRING_TO_NUMBER_INFO = new ActionInfo(ActionType.STRING_TO_NUMBER, StringToNumberAction.class, R.drawable.icon_text, R.string.string_to_number_action, R.string.string_to_number_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STRING_APPEND_INFO = new ActionInfo(ActionType.STRING_APPEND, StringAppendAction.class, R.drawable.icon_text, R.string.string_append_action, R.string.string_append_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STRING_REGEX_INFO = new ActionInfo(ActionType.STRING_REGEX, StringMatchAction.class, R.drawable.icon_text, R.string.string_match_action, R.string.string_match_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STRING_SPLIT_INFO = new ActionInfo(ActionType.STRING_SPLIT, StringSplitAction.class, R.drawable.icon_text, R.string.string_split_action, R.string.string_split_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STRING_EQUAL_INFO = new ActionInfo(ActionType.STRING_EQUAL, StringEqualAction.class, R.drawable.icon_text, R.string.string_equal_action, R.string.string_equal_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo STRING_REPLACE_INFO = new ActionInfo(ActionType.STRING_REPLACE, StringReplaceAction.class, R.drawable.icon_text, R.string.string_replace_action, R.string.string_replace_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_OCR_TEXT_INFO = new ActionInfo(ActionType.GET_OCR_TEXT, GetOcrTextAction.class, R.drawable.icon_text, R.string.get_ocr_text_action, R.string.get_ocr_text_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FIND_OCR_TEXT_INFO = new ActionInfo(ActionType.FIND_OCR_TEXT, FindOcrTextAction.class, R.drawable.icon_text, R.string.find_ocr_text_action, R.string.find_ocr_text_action_desc, 0, NormalActionCard.class);


    // 条件判断
    private final static ActionInfo BOOLEAN_OR_INFO = new ActionInfo(ActionType.BOOLEAN_OR, BooleanOrAction.class, R.drawable.icon_condition, R.string.boolean_or_action, R.string.boolean_or_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo BOOLEAN_AND_INFO = new ActionInfo(ActionType.BOOLEAN_AND, BooleanAndAction.class, R.drawable.icon_condition, R.string.boolean_and_action, R.string.boolean_and_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo BOOLEAN_NOT_INFO = new ActionInfo(ActionType.BOOLEAN_NOT, BooleanNotAction.class, R.drawable.icon_condition, R.string.boolean_not_action, R.string.boolean_not_action_desc, 0, NormalActionCard.class);


    // 控件操作
    private final static ActionInfo FIND_NODE_BY_PATH_INFO = new ActionInfo(ActionType.FIND_NODE_BY_PATH, FindNodeByPathAction.class, R.drawable.icon_widget, R.string.find_node_by_path_action, R.string.find_node_by_path_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FIND_NODES_BY_TEXT_INFO = new ActionInfo(ActionType.FIND_NODES_BY_TEXT, FindNodesByTextAction.class, R.drawable.icon_widget, R.string.find_nodes_by_text_action, R.string.find_nodes_by_text_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FIND_NODES_BY_ID_INFO = new ActionInfo(ActionType.FIND_NODES_BY_ID, FindNodesByIdAction.class, R.drawable.icon_widget, R.string.find_nodes_by_id_action, R.string.find_nodes_by_id_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FIND_NODES_IN_AREA_INFO = new ActionInfo(ActionType.FIND_NODES_IN_AREA, FindNodesInAreaAction.class, R.drawable.icon_widget, R.string.find_nodes_in_area_action, R.string.find_nodes_in_area_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_NODE_INFO_INFO = new ActionInfo(ActionType.GET_NODE_INFO, GetNodeInfoAction.class, R.drawable.icon_widget, R.string.get_node_info_action, R.string.get_node_info_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_NODE_CHILDREN_INFO = new ActionInfo(ActionType.GET_NODE_CHILDREN, GetNodeChildrenAction.class, R.drawable.icon_widget, R.string.get_node_children_action, R.string.get_node_children_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_NODE_PARENT_INFO = new ActionInfo(ActionType.GET_NODE_PARENT, GetNodeParentAction.class, R.drawable.icon_widget, R.string.get_node_parent_action, R.string.get_node_parent_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_WINDOWS_INFO = new ActionInfo(ActionType.GET_WINDOWS, GetWindowsAction.class, R.drawable.icon_widget, R.string.get_windows_action, R.string.get_windows_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHECK_NODE_VALID_INFO = new ActionInfo(ActionType.CHECK_NODE_VALID, CheckNodeValidAction.class, R.drawable.icon_widget, R.string.check_node_valid_action, R.string.check_node_valid_action_desc, 0, NormalActionCard.class);

    private final static ActionInfo NODE_TOUCH_INFO = new ActionInfo(ActionType.NODE_TOUCH, NodeTouchAction.class, R.drawable.icon_widget, R.string.node_touch_action, R.string.node_touch_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo EDITTEXT_INPUT_INFO = new ActionInfo(ActionType.EDITTEXT_INPUT, EditTextInputAction.class, R.drawable.icon_widget, R.string.edit_text_input_action, R.string.edit_text_input_action_desc, 0, NormalActionCard.class);


    // 图片操作
    private final static ActionInfo GET_IMAGE_INFO = new ActionInfo(ActionType.GET_IMAGE, GetImageAction.class, R.drawable.icon_image, R.string.get_image_action, R.string.get_image_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CROP_IMAGE_INFO = new ActionInfo(ActionType.CROP_IMAGE, CropImageAction.class, R.drawable.icon_image, R.string.crop_image_action, R.string.crop_image_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo SAVE_IMAGE_INFO = new ActionInfo(ActionType.SAVE_IMAGE, SaveImageAction.class, R.drawable.icon_image, R.string.save_image_action, R.string.save_image_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FIND_IMAGES_INFO = new ActionInfo(ActionType.FIND_IMAGES, FindImagesAction.class, R.drawable.icon_image, R.string.find_images_action, R.string.find_images_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo IMAGE_EQUAL_INFO = new ActionInfo(ActionType.IMAGE_EQUAL, ImageEqualAction.class, R.drawable.icon_image, R.string.image_equal_action, R.string.image_equal_action_desc, 0, NormalActionCard.class);


    // 颜色操作
    private final static ActionInfo GET_COLOR_INFO = new ActionInfo(ActionType.GET_COLOR, GetColorAction.class, R.drawable.icon_color, R.string.get_color_action, R.string.get_color_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo FIND_COLORS_INFO = new ActionInfo(ActionType.FIND_COLORS, FindColorsAction.class, R.drawable.icon_color, R.string.find_colors_action, R.string.find_colors_action_desc, 0, NormalActionCard.class);


    // 区域操作
    private final static ActionInfo AREA_TO_INT_INFO = new ActionInfo(ActionType.AREA_TO_INT, AreaToIntegerAction.class, R.drawable.icon_stop, R.string.area_to_integer_action, R.string.area_to_integer_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo AREA_FROM_INT_INFO = new ActionInfo(ActionType.AREA_FROM_INT, AreaFromIntegerAction.class, R.drawable.icon_stop, R.string.area_from_integer_action, R.string.area_from_integer_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHECK_AREA_CONTAIN_POS_INFO = new ActionInfo(ActionType.CHECK_AREA_CONTAIN_POS, CheckAreaContainPosAction.class, R.drawable.icon_stop, R.string.check_area_contain_pos_action, R.string.check_area_contain_pos_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo CHECK_AREA_RELATION_INFO = new ActionInfo(ActionType.CHECK_AREA_RELATION, CheckAreaRelationAction.class, R.drawable.icon_stop, R.string.check_area_relation_action, R.string.check_area_relation_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_AREA_INTERSECTION_INFO = new ActionInfo(ActionType.GET_AREA_INTERSECTION, GetAreaIntersectionAction.class, R.drawable.icon_stop, R.string.get_area_intersection_action, R.string.get_area_intersection_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo GET_AREA_CENTER_INFO = new ActionInfo(ActionType.GET_AREA_CENTER, GetAreaCenterAction.class, R.drawable.icon_stop, R.string.get_area_center_action, R.string.get_area_center_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo PICK_AREA_INFO = new ActionInfo(ActionType.PICK_AREA, PickAreaAction.class, R.drawable.icon_stop, R.string.pick_area_action, R.string.pick_area_action_desc, 0, NormalActionCard.class);


    // 位置操作
    private final static ActionInfo POINT_TO_INT_INFO = new ActionInfo(ActionType.POINT_TO_INT, PointToIntegerAction.class, R.drawable.icon_position, R.string.point_to_integer_action, R.string.point_to_integer_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo POINT_FROM_INT_INFO = new ActionInfo(ActionType.POINT_FROM_INT, PointFromIntegerAction.class, R.drawable.icon_position, R.string.point_from_integer_action, R.string.point_from_integer_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo POINT_OFFSET_INFO = new ActionInfo(ActionType.POINT_OFFSET, PointOffsetAction.class, R.drawable.icon_position, R.string.point_offset_action, R.string.point_offset_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo POINT_TO_TOUCH_INFO = new ActionInfo(ActionType.POINT_TO_TOUCH, PointToTouchAction.class, R.drawable.icon_touch, R.string.point_to_touch_action, R.string.point_to_touch_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo TOUCH_INFO = new ActionInfo(ActionType.TOUCH, TouchAction.class, R.drawable.icon_touch, R.string.touch_action, R.string.touch_action_desc, 0, NormalActionCard.class);


    // List操作
    private final static ActionInfo LIST_MAKE_INFO = new ActionInfo(ActionType.LIST_MAKE, MakeListAction.class, R.drawable.icon_array, R.string.list_make_action, R.string.list_make_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_SIZE_INFO = new ActionInfo(ActionType.LIST_SIZE, ListSizeAction.class, R.drawable.icon_array, R.string.list_size_action, R.string.list_size_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_IS_EMPTY_INFO = new ActionInfo(ActionType.LIST_IS_EMPTY, ListIsEmptyAction.class, R.drawable.icon_array, R.string.list_is_empty_action, R.string.list_is_empty_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_CONTAIN_INFO = new ActionInfo(ActionType.LIST_CONTAIN, ListContainAction.class, R.drawable.icon_array, R.string.list_contain_action, R.string.list_contain_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_ADD_INFO = new ActionInfo(ActionType.LIST_ADD, ListAddAction.class, R.drawable.icon_array, R.string.list_add_action, R.string.list_add_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_REMOVE_INFO = new ActionInfo(ActionType.LIST_REMOVE, ListRemoveAction.class, R.drawable.icon_array, R.string.list_remove_action, R.string.list_remove_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_APPEND_INFO = new ActionInfo(ActionType.LIST_APPEND, ListAppendAction.class, R.drawable.icon_array, R.string.list_append_action, R.string.list_append_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_CLEAR_INFO = new ActionInfo(ActionType.LIST_CLEAR, ListClearAction.class, R.drawable.icon_array, R.string.list_clear_action, R.string.list_clear_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_GET_INFO = new ActionInfo(ActionType.LIST_GET, ListGetAction.class, R.drawable.icon_array, R.string.list_get_action, R.string.list_get_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_SET_INFO = new ActionInfo(ActionType.LIST_SET, ListSetAction.class, R.drawable.icon_array, R.string.list_set_action, R.string.list_set_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_INDEX_OF_INFO = new ActionInfo(ActionType.LIST_INDEX_OF, ListIndexOfAction.class, R.drawable.icon_array, R.string.list_index_of_action, R.string.list_index_of_action_desc, 0, NormalActionCard.class);
    private final static ActionInfo LIST_FOREACH_INFO = new ActionInfo(ActionType.LIST_FOREACH, ListForeachAction.class, R.drawable.icon_array, R.string.list_foreach_action, R.string.list_foreach_action_desc, 0, NormalActionCard.class);

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


            case OPEN_APP -> OPEN_APP_INFO;
            case OPEN_URI_SCHEME -> OPEN_URI_SCHEME_INFO;
            case SHARE_TO -> SHARE_TO_INFO;

            case PLAY_RINGTONE -> PLAY_RINGTONE_INFO;
            case STOP_RINGTONE -> STOP_RINGTONE_INFO;
            case TEXT_TO_SPEECH -> TEXT_TO_SPEECH_INFO;

            case WRITE_TO_CLIPBOARD -> WRITE_TO_CLIPBOARD_INFO;

            case SWITCH_SCREEN -> SWITCH_SCREEN_INFO;
            case GET_SCREEN_STATUS -> GET_SCREEN_STATUS_INFO;

            case SWITCH_CAPTURE -> SWITCH_CAPTURE_INFO;
            case CHECK_CAPTURE_READY -> CHECK_CAPTURE_READY_INFO;

            case GET_CURRENT_APPLICATION -> GET_CURRENT_APPLICATION_INFO;
            case CHECK_IN_APPLICATION -> CHECK_IN_APPLICATION_INFO;

            case GET_BATTERY_STATUS -> GET_BATTERY_STATUS_INFO;

            case GET_NETWORK_STATUS -> GET_NETWORK_STATUS_INFO;

            case GET_CURRENT_DATE -> GET_CURRENT_DATE_INFO;
            case GET_CURRENT_TIME -> GET_CURRENT_TIME_INFO;


            case NUMBER_ADD -> NUMBER_ADD_INFO;
            case NUMBER_SUB -> NUMBER_SUB_INFO;
            case NUMBER_MUL -> NUMBER_MUL_INFO;
            case NUMBER_DIV -> NUMBER_DIV_INFO;
            case NUMBER_MOD -> NUMBER_MOD_INFO;
            case NUMBER_ABS -> NUMBER_ABS_INFO;
            case NUMBER_EQUAL -> NUMBER_EQUAL_INFO;
            case NUMBER_LESS -> NUMBER_LESS_INFO;
            case NUMBER_GREATER -> NUMBER_GREATER_INFO;
            case NUMBER_RANDOM -> NUMBER_RANDOM_INFO;
            case NUMBER_TO_INT -> NUMBER_TO_INT_INFO;

            case CHECK_NUMBER_IN_VALUE_AREA -> CHECK_NUMBER_IN_VALUE_AREA_INFO;
            case NUMBER_TO_VALUE_AREA -> NUMBER_TO_VALUE_AREA_INFO;

            case MATH_EXPRESSION -> MATH_EXPRESSION_INFO;


            case STRING_FROM_OBJECT -> STRING_FROM_OBJECT_INFO;
            case STRING_TO_NUMBER -> STRING_TO_NUMBER_INFO;
            case STRING_APPEND -> STRING_APPEND_INFO;
            case STRING_REGEX -> STRING_REGEX_INFO;
            case STRING_SPLIT -> STRING_SPLIT_INFO;
            case STRING_EQUAL -> STRING_EQUAL_INFO;
            case STRING_REPLACE -> STRING_REPLACE_INFO;
            case GET_OCR_TEXT -> GET_OCR_TEXT_INFO;
            case FIND_OCR_TEXT -> FIND_OCR_TEXT_INFO;


            case BOOLEAN_OR -> BOOLEAN_OR_INFO;
            case BOOLEAN_AND -> BOOLEAN_AND_INFO;
            case BOOLEAN_NOT -> BOOLEAN_NOT_INFO;


            case FIND_NODE_BY_PATH -> FIND_NODE_BY_PATH_INFO;
            case FIND_NODES_BY_TEXT -> FIND_NODES_BY_TEXT_INFO;
            case FIND_NODES_BY_ID -> FIND_NODES_BY_ID_INFO;
            case FIND_NODES_IN_AREA -> FIND_NODES_IN_AREA_INFO;
            case GET_NODE_INFO -> GET_NODE_INFO_INFO;
            case GET_NODE_CHILDREN -> GET_NODE_CHILDREN_INFO;
            case GET_NODE_PARENT -> GET_NODE_PARENT_INFO;
            case GET_WINDOWS -> GET_WINDOWS_INFO;
            case CHECK_NODE_VALID -> CHECK_NODE_VALID_INFO;

            case NODE_TOUCH -> NODE_TOUCH_INFO;
            case EDITTEXT_INPUT -> EDITTEXT_INPUT_INFO;


            case GET_IMAGE -> GET_IMAGE_INFO;
            case CROP_IMAGE -> CROP_IMAGE_INFO;
            case SAVE_IMAGE -> SAVE_IMAGE_INFO;
            case FIND_IMAGES -> FIND_IMAGES_INFO;
            case IMAGE_EQUAL -> IMAGE_EQUAL_INFO;


            case GET_COLOR -> GET_COLOR_INFO;
            case FIND_COLORS -> FIND_COLORS_INFO;


            case AREA_TO_INT -> AREA_TO_INT_INFO;
            case AREA_FROM_INT -> AREA_FROM_INT_INFO;
            case CHECK_AREA_CONTAIN_POS -> CHECK_AREA_CONTAIN_POS_INFO;
            case CHECK_AREA_RELATION -> CHECK_AREA_RELATION_INFO;
            case GET_AREA_INTERSECTION -> GET_AREA_INTERSECTION_INFO;
            case GET_AREA_CENTER -> GET_AREA_CENTER_INFO;
            case PICK_AREA -> PICK_AREA_INFO;


            case POINT_FROM_INT -> POINT_FROM_INT_INFO;
            case POINT_TO_INT -> POINT_TO_INT_INFO;
            case POINT_OFFSET -> POINT_OFFSET_INFO;
            case POINT_TO_TOUCH -> POINT_TO_TOUCH_INFO;
            case TOUCH -> TOUCH_INFO;


            case LIST_MAKE -> LIST_MAKE_INFO;
            case LIST_SIZE -> LIST_SIZE_INFO;
            case LIST_IS_EMPTY -> LIST_IS_EMPTY_INFO;
            case LIST_CONTAIN -> LIST_CONTAIN_INFO;
            case LIST_ADD -> LIST_ADD_INFO;
            case LIST_REMOVE -> LIST_REMOVE_INFO;
            case LIST_APPEND -> LIST_APPEND_INFO;
            case LIST_CLEAR -> LIST_CLEAR_INFO;
            case LIST_GET -> LIST_GET_INFO;
            case LIST_SET -> LIST_SET_INFO;
            case LIST_INDEX_OF -> LIST_INDEX_OF_INFO;
            case LIST_FOREACH -> LIST_FOREACH_INFO;

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
