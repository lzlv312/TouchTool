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
                    ActionType.OUT_CALL_START
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
                    ActionType.FIND_COLORS
            ));
        }
        return list;
    }


    public enum ActionGroupType {
        START, LOGIC, NORMAL;

        public String getName() {
            String[] strings = MainApplication.getInstance().getResources().getStringArray(R.array.action_group);
            return strings[ordinal()];
        }
    }
}
