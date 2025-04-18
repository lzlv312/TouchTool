package top.bogey.touch_tool.bean.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;

public class ActionMap {

    public static List<ActionType> getTypes(ActionGroupType groupType) {
        ArrayList<ActionType> list = new ArrayList<>();
        switch (groupType) {
            case START -> list.addAll(Arrays.asList(
                    ActionType.MANUAL_START,
                    ActionType.APPLICATION_START,
                    ActionType.TIME_START,
                    ActionType.NOTIFICATION_START,
                    ActionType.NETWORK_START,
                    ActionType.BATTERY_START,
                    ActionType.SCREEN_START,
                    ActionType.BLUETOOTH_START,
                    ActionType.OUT_CALL_START,
                    ActionType.CUSTOM_START,
                    ActionType.CUSTOM_END
            ));

            case LOGIC -> list.addAll(Arrays.asList(
                    ActionType.IF_LOGIC,
                    ActionType.FOR_LOGIC,
                    ActionType.WHILE_LOGIC,
                    ActionType.RANDOM_LOGIC,
                    ActionType.SEQUENCE_LOGIC,
                    ActionType.PARALLEL_LOGIC,
                    ActionType.SWITCH_LOGIC,
                    ActionType.CHOICE_LOGIC
            ));

            case NORMAL -> list.addAll(Arrays.asList(
                    ActionType.DELAY,

                    ActionType.LOG,
                    ActionType.STICK,
                    ActionType.CLOSE_STICK,
                    ActionType.CLOSE_ALL_STICK,

                    ActionType.TOUCH,
                    ActionType.NODE_TOUCH,
                    ActionType.EDITTEXT_INPUT,

                    ActionType.OPEN_APP
            ));

            case SYSTEM -> list.addAll(Arrays.asList(
                    ActionType.OPEN_APP,
                    ActionType.OPEN_URI_SCHEME,
                    ActionType.SHARE_TO,

                    ActionType.PLAY_RINGTONE,
                    ActionType.STOP_RINGTONE,
                    ActionType.TEXT_TO_SPEECH,

                    ActionType.WRITE_TO_CLIPBOARD,

                    ActionType.SWITCH_SCREEN,
                    ActionType.GET_SCREEN_STATUS,

                    ActionType.SWITCH_CAPTURE,
                    ActionType.CHECK_CAPTURE_READY,

                    ActionType.GET_CURRENT_APPLICATION,
                    ActionType.CHECK_IN_APPLICATION,

                    ActionType.GET_BATTERY_STATUS,

                    ActionType.GET_NETWORK_STATUS,

                    ActionType.GET_CURRENT_DATE,
                    ActionType.GET_CURRENT_TIME
            ));

            case NUMBER -> list.addAll(Arrays.asList(
                    ActionType.NUMBER_ADD,
                    ActionType.NUMBER_SUB,
                    ActionType.NUMBER_MUL,
                    ActionType.NUMBER_DIV,
                    ActionType.NUMBER_MOD,
                    ActionType.NUMBER_EQUAL,
                    ActionType.NUMBER_LESS,
                    ActionType.NUMBER_GREATER,
                    ActionType.NUMBER_RANDOM,
                    ActionType.NUMBER_TO_INT,

                    ActionType.CHECK_NUMBER_IN_VALUE_AREA,
                    ActionType.NUMBER_TO_VALUE_AREA,

                    ActionType.MATH_EXPRESSION
            ));

            case STRING -> list.addAll(Arrays.asList(
                    ActionType.STRING_FROM_OBJECT,
                    ActionType.STRING_TO_NUMBER,
                    ActionType.STRING_APPEND,
                    ActionType.STRING_REGEX,
                    ActionType.STRING_SPLIT,
                    ActionType.STRING_EQUAL,
                    ActionType.STRING_REPLACE,
                    ActionType.GET_OCR_TEXT,
                    ActionType.FIND_OCR_TEXT
            ));

            case BOOLEAN -> list.addAll(Arrays.asList(
                    ActionType.BOOLEAN_OR,
                    ActionType.BOOLEAN_AND,
                    ActionType.BOOLEAN_NOT
            ));

            case NODE -> list.addAll(Arrays.asList(
                    ActionType.FIND_NODE_BY_PATH,
                    ActionType.FIND_NODES_BY_TEXT,
                    ActionType.FIND_NODES_BY_ID,
                    ActionType.FIND_NODES_IN_AREA,
                    ActionType.GET_NODE_INFO,
                    ActionType.GET_NODE_CHILDREN,
                    ActionType.GET_NODE_PARENT,
                    ActionType.GET_WINDOWS,
                    ActionType.CHECK_NODE_VALID,

                    ActionType.NODE_TOUCH,
                    ActionType.EDITTEXT_INPUT
            ));

            case IMAGE -> list.addAll(Arrays.asList(
                    ActionType.GET_IMAGE,
                    ActionType.CROP_IMAGE,
                    ActionType.SAVE_IMAGE,
                    ActionType.FIND_IMAGES,
                    ActionType.CREATE_QRCODE,
                    ActionType.PARSE_QRCODE,
                    ActionType.GET_COLOR,
                    ActionType.FIND_COLORS
            ));

            case AREA -> list.addAll(Arrays.asList(
                    ActionType.AREA_TO_INT,
                    ActionType.AREA_FROM_INT,
                    ActionType.CHECK_AREA_CONTAIN_POS,
                    ActionType.CHECK_AREA_RELATION,
                    ActionType.GET_AREA_INTERSECTION,
                    ActionType.GET_AREA_CENTER,
                    ActionType.GET_AREA_RANDOM,
                    ActionType.PICK_AREA
            ));

            case POINT -> list.addAll(Arrays.asList(
                    ActionType.POINT_FROM_INT,
                    ActionType.POINT_TO_INT,
                    ActionType.POINT_OFFSET,
                    ActionType.POINT_TO_TOUCH,
                    ActionType.TOUCH,
                    ActionType.TOUCH_POINT
            ));

            case LIST -> list.addAll(Arrays.asList(
                    ActionType.LIST_MAKE,
                    ActionType.LIST_FOREACH,
                    ActionType.LIST_ADD,
                    ActionType.LIST_GET,
                    ActionType.LIST_SIZE,
                    ActionType.LIST_IS_EMPTY,
                    ActionType.LIST_CONTAIN,
                    ActionType.LIST_REMOVE,
                    ActionType.LIST_APPEND,
                    ActionType.LIST_CLEAR,
                    ActionType.LIST_SET,
                    ActionType.LIST_INDEX_OF
            ));
        }
        return list;
    }

    public enum ActionGroupType {
        START, LOGIC, NORMAL, SYSTEM, NUMBER, STRING, BOOLEAN, NODE, IMAGE, AREA, POINT, LIST;

        public String getName() {
            String[] strings = MainApplication.getInstance().getResources().getStringArray(R.array.action_group);
            return strings[ordinal()];
        }
    }
}
