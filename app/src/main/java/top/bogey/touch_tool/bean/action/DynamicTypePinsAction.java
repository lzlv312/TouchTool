package top.bogey.touch_tool.bean.action;

import java.util.List;

import top.bogey.touch_tool.bean.pin.Pin;

public interface DynamicTypePinsAction {
    List<Pin> getDynamicKeyTypePins();
    List<Pin> getDynamicValueTypePins();
}
