package top.bogey.touch_tool.bean.action.list;

import java.util.List;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.Task;

public class ListActionLinkEventHandler {

    public static void onLinkedTo(List<Pin> valuePins, Task task, Pin origin, Pin to) {
        // 任意动态类型针脚连接上了，则动作中所有动态类型针脚也得改变
        if (valuePins.contains(origin)) {
            for (Pin pin : valuePins) {
                if (pin.isSameClass(PinList.class)) {
                    PinList list = pin.getValue(PinList.class);
                    if (list.getValueType() == to.getValue().getType()) continue;
                    list.setValueType(to.getValue().getType());
                    list.reset();
                } else {
                    if (pin.isSameClass(to)) continue;
                    PinInfo info = PinInfo.getPinInfo(to.getValue());
                    pin.setValue(info.newInstance());
                }
                if (pin != origin) pin.clearLinks(task);
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
                    if (pin.isSameClass(PinList.class)) {
                        PinList list = pin.getValue(PinList.class);
                        list.setValueType(PinType.OBJECT);
                        list.reset();
                    } else {
                        PinInfo info = PinInfo.getPinInfo(PinType.OBJECT);
                        pin.setValue(info.newInstance());
                    }
                }
            }
        }
    }
}
