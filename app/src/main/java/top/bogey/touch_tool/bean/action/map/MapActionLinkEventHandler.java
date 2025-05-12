package top.bogey.touch_tool.bean.action.map;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.Task;

public class MapActionLinkEventHandler {

    public static void onLinkedTo(List<Pin> pins, List<Pin> keyPins, List<Pin> valuePins, Task task, Pin origin, Pin to) {
        List<Pin> keys = new ArrayList<>();
        List<Pin> values = new ArrayList<>();
        for (Pin pin : pins) {
            if (pin.isLinked()) {
                Pin linkedPin = pin.getLinkedPin(task);
                if (linkedPin == null) continue;
                if (keyPins.contains(pin)) {
                    // 两边的值都是动态的，无法确定类型，跳过
                    if (pin.getValue().isDynamic() && linkedPin.getValue().isDynamic()) continue;
                    keys.add(pin);
                } else if (valuePins.contains(pin)) {
                    // 两边的值都是动态的，无法确定类型，跳过
                    if (pin.getValue().isDynamic() && linkedPin.getValue().isDynamic()) continue;
                    values.add(pin);
                } else if (pin.isSameClass(PinMap.class)) {
                    if (linkedPin.isSameClass(PinMap.class)) {
                        if (!(pin.getValue(PinMap.class).isDynamicKey() && linkedPin.getValue(PinMap.class).isDynamicKey())) keys.add(pin);
                        if (!(pin.getValue(PinMap.class).isDynamicValue() && linkedPin.getValue(PinMap.class).isDynamicValue())) values.add(pin);
                    }
                }
            }
        }

        boolean keyFlag = keys.size() == 1 && keys.contains(origin);
        boolean valueFlag = values.size() == 1 && values.contains(origin);
        if (!keyFlag && !valueFlag) return;

        // 当针脚为首个连接的Key或首个连接的Value时，设置动态针脚类型
        PinInfo keyPinInfo = null, valuePinInfo = null;
        if (to.isSameClass(PinMap.class)) {
            PinMap toMap = to.getValue(PinMap.class);
            keyPinInfo = PinInfo.getPinInfo(toMap.getKeyType());
            valuePinInfo = PinInfo.getPinInfo(toMap.getValueType());
        } else if (to.isSameClass(PinList.class)) {
            PinList toList = to.getValue(PinList.class);
            if (keyFlag) keyPinInfo = PinInfo.getPinInfo(toList.getValueType());
            if (valueFlag) valuePinInfo = PinInfo.getPinInfo(toList.getValueType());
        } else {
            if (keyFlag) keyPinInfo = PinInfo.getPinInfo(to.getValue());
            if (valueFlag) valuePinInfo = PinInfo.getPinInfo(to.getValue());
        }

        for (Pin pin : pins) {
            boolean isDynamic = pin.getValue().isDynamic();

            if (pin.isSameClass(PinAdd.class)) {
                PinAdd add = pin.getValue(PinAdd.class);
                if (keyFlag) add.getPin(0).setValue(keyPinInfo.newInstance());
                if (valueFlag) add.getPin(1).setValue(valuePinInfo.newInstance());
            } else if (pin.isSameClass(PinMap.class)) {
                PinMap map = pin.getValue(PinMap.class);
                isDynamic = map.isDynamicKey() || map.isDynamicValue();
                if (keyFlag) map.setKeyType(keyPinInfo.getType());
                if (valueFlag) map.setValueType(valuePinInfo.getType());
                map.reset();
                // 通知针脚刷新
                pin.setValue(map);
            } else if (pin.isSameClass(PinList.class)) {
                PinList list = pin.getValue(PinList.class);
                if (keyFlag) list.setValueType(keyPinInfo.getType());
                if (valueFlag) list.setValueType(valuePinInfo.getType());
                list.reset();
                // 通知针脚刷新
                pin.setValue(list);
            } else if (keyFlag && keyPins.contains(pin)) {
                pin.setValue(keyPinInfo.newInstance());
            } else if (valueFlag && valuePins.contains(pin)) {
                pin.setValue(valuePinInfo.newInstance());
            }

            // 已连接的动态针脚需要继续更新连接的动作
            if (isDynamic && pin.isLinked() && pin != origin) {
                Pin linkedPin = pin.getLinkedPin(task);
                if (linkedPin != null) {
                    PinBase value = linkedPin.getValue();
                    boolean flag = false;
                    if (value instanceof PinMap pinMap) flag = pinMap.isHalfDynamic();
                    else flag = value.isDynamic();
                    if (flag) {
                        Action action = task.getAction(linkedPin.getOwnerId());
                        if (action != null) action.onLinkedTo(task, linkedPin, pin);
                    }
                }
            }
        }
    }

    public static void onUnLinkedFrom(List<Pin> pins, List<Pin> keyPins, List<Pin> valuePins, Pin origin) {
        int keyCount = 0;
        int valueCount = 0;

        for (Pin pin : pins) {
            if (pin.isLinked()) {
                if (keyPins.contains(pin)) {
                    keyCount++;
                } else if (valuePins.contains(pin)) {
                    valueCount++;
                } else if (pin.isSameClass(PinMap.class)) {
                    keyCount++;
                    valueCount++;
                }
            }
        }

        boolean keyFlag = keyCount == 0 && (origin.isSameClass(PinMap.class) || keyPins.contains(origin));
        boolean valueFlag = valueCount == 0 && (origin.isSameClass(PinMap.class) || valuePins.contains(origin));
        if (!keyFlag && !valueFlag) return;

        for (Pin pin : pins) {
            PinBase value = pin.getValue();
            if (value instanceof PinAdd pinAdd) {
                if (keyFlag) pinAdd.getPin(0).setValue(new PinObject(PinSubType.DYNAMIC));
                if (valueFlag) pinAdd.getPin(1).setValue(new PinObject(PinSubType.DYNAMIC));
            } else if (value instanceof PinMap pinMap) {
                if (keyFlag) pinMap.setKeyType(PinType.OBJECT);
                if (valueFlag) pinMap.setValueType(PinType.OBJECT);
                pinMap.reset();
                // 通知针脚刷新
                pin.setValue(pinMap);
            } else if (value instanceof PinList pinList) {
                pinList.setValueType(PinType.OBJECT);
                pinList.reset();
                // 通知针脚刷新
                pin.setValue(pinList);
            } else if ((keyFlag && keyPins.contains(pin)) || (valueFlag && valuePins.contains(pin))) {
                pin.setValue(new PinObject(PinSubType.DYNAMIC));
            }
        }
    }
}
