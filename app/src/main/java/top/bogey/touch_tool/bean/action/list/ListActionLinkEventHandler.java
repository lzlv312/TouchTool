package top.bogey.touch_tool.bean.action.list;

import java.util.List;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.Task;

public class ListActionLinkEventHandler {
    public static void onLinkedTo(List<Pin> valuePins, Task task, Pin origin, Pin to) {
        int count = 0;
        for (Pin pin : valuePins) {
            if (pin.isDynamic()) continue;
            if (pin.isLinked()) {
                Pin linkedPin = pin.getLinkedPin(task);
                if (linkedPin == null) continue;
                count++;
            }
        }
        // 当有效连接针脚后只有一个连接时，说明是第一个连接，这时需要设置所有动态针脚为目标类型
        if (count == 1) {
            PinInfo toInfo;
            if (to.isSameClass(PinList.class)) {
                PinList toList = to.getValue(PinList.class);
                toInfo = PinInfo.getPinInfo(toList.getValueType());
            } else {
                toInfo = PinInfo.getPinInfo(to.getValue());
            }

            for (Pin pin : valuePins) {
                boolean isDynamic = pin.getValue().isDynamic();

                if (pin.isSameClass(PinObject.class)) {
                    pin.setValue(toInfo.newInstance());
                } else if (pin.isSameClass(PinAdd.class)) {
                    PinAdd add = pin.getValue(PinAdd.class);
                    add.getPin().setValue(toInfo.newInstance());
                } else if (pin.isSameClass(PinList.class)) {
                    PinList list = pin.getValue(PinList.class);
                    list.setValueType(toInfo.getType());
                    list.reset();
                    // 通知针脚刷新
                    pin.setValue(list);
                }

                // 已连接的动态针脚需要继续更新连接的动作
                if (isDynamic && pin.isLinked() && pin != origin) {
                    Pin linkedPin = pin.getLinkedPin(task);
                    if (linkedPin != null && linkedPin.getValue().isDynamic()) {
                        Action action = task.getAction(linkedPin.getOwnerId());
                        if (action != null) action.onLinkedTo(task, linkedPin, pin);
                    }
                }
            }
        }
    }

    public static void onUnLinkedFrom(List<Pin> valuePins, Pin origin) {
        // 所有动态类型针脚都断开连接，则所有动态类型针脚重置
        if (valuePins.contains(origin)) {
            boolean flag = true;
            for (Pin pin : valuePins) {
                if (pin.isLinked()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                for (Pin pin : valuePins) {
                    PinBase value = pin.getValue();
                    if (value instanceof PinAdd pinAdd) {
                        pinAdd.getPin().setValue(new PinObject(PinSubType.DYNAMIC));
                    } else if (value instanceof PinList pinList) {
                        pinList.setValueType(PinType.OBJECT);
                        pinList.reset();
                        // 通知针脚刷新
                        pin.setValue(pinList);
                    } else {
                        pin.setValue(new PinObject(PinSubType.DYNAMIC));
                    }
                }
            }
        }
    }
}
