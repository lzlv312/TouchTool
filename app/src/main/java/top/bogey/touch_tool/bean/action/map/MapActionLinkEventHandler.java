package top.bogey.touch_tool.bean.action.map;

import java.util.List;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.Task;

public class MapActionLinkEventHandler {

    public static void onLinkedTo(List<Pin> keyPins, List<Pin> valuePins, Task task, Pin origin, Pin to) {
        // 任意动态类型针脚连接上了，则动作中所有动态类型针脚也得改变
        if (keyPins.contains(origin)) {
            for (Pin pin : keyPins) {
                if (pin.isSameClass(PinMap.class)) {
                    PinMap map = pin.getValue(PinMap.class);
                    if (map.getKeyType() == to.getValue().getType()) continue;
                    map.setKeyType(to.getValue().getType());
                    map.reset();
                } else {
                    if (pin.isSameClass(to)) continue;
                    PinInfo info = PinInfo.getPinInfo(to.getValue());
                    pin.setValue(info.newInstance());
                }
                if (pin != origin) pin.clearLinks(task);
            }
        }

        if (valuePins.contains(origin)) {
            for (Pin pin : valuePins) {
                if (pin.isSameClass(PinMap.class)) {
                    PinMap map = pin.getValue(PinMap.class);
                    if (map.getValueType() == to.getValue().getType()) continue;
                    map.setValueType(to.getValue().getType());
                    map.reset();
                } else {
                    if (pin.isSameClass(to)) continue;
                    PinInfo info = PinInfo.getPinInfo(to.getValue());
                    pin.setValue(info.newInstance());
                }
                if (pin != origin) pin.clearLinks(task);
            }
        }
    }

    public static void onUnLinkedFrom(List<Pin> keyPins, List<Pin> valuePins, Pin origin) {
        // 所有动态类型针脚都断开连接，则所有动态类型针脚重置
        if (keyPins.contains(origin)) {
            boolean flag = true;
            for (Pin pin : keyPins) {
                if (pin.isLinked()) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                for (Pin pin : keyPins) {
                    if (pin.isSameClass(PinMap.class)) {
                        PinMap map = pin.getValue(PinMap.class);
                        map.setKeyType(PinType.OBJECT);
                        map.reset();
                    } else {
                        PinInfo info = PinInfo.getPinInfo(PinType.OBJECT);
                        pin.setValue(info.newInstance());
                    }
                }
            }
        }

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
                    if (pin.isSameClass(PinMap.class)) {
                        PinMap map = pin.getValue(PinMap.class);
                        map.setValueType(PinType.OBJECT);
                        map.reset();
                    } else {
                        PinInfo info = PinInfo.getPinInfo(PinType.OBJECT);
                        pin.setValue(info.newInstance());
                    }
                }
            }
        }
    }
}
