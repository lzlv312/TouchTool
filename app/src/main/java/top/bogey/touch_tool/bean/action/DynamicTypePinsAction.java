package top.bogey.touch_tool.bean.action;

import androidx.annotation.NonNull;

import java.util.List;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.task.Task;

public interface DynamicTypePinsAction {
    @NonNull
    List<Pin> getDynamicKeyTypePins();

    @NonNull
    List<Pin> getDynamicValueTypePins();
}
