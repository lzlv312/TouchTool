package top.bogey.touch_tool.bean.action.image;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.logic.FindExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinNumber;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;

public class FindImagesAction extends FindExecuteAction {
    private final transient Pin sourcePin = new Pin(new PinImage(), R.string.pin_image);
    private final transient Pin templatePin = new Pin(new PinImage(), R.string.find_images_action_template);
    private final transient Pin similarityPin = new Pin(new PinInteger(), R.string.find_images_action_similarity);
    private final transient Pin areasPin = new Pin(new PinList(PinType.AREA), R.string.pin_area, true);

    public FindImagesAction() {
        super(ActionType.FIND_IMAGES);
        intervalPin.getValue(PinInteger.class).setValue(200);
        addPins(sourcePin, templatePin, similarityPin, areasPin);
    }

    public FindImagesAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(sourcePin, templatePin, similarityPin, areasPin);
    }

    @Override
    public boolean find(TaskRunnable runnable) {
        PinImage source = getPinValue(runnable, sourcePin);
        PinImage template = getPinValue(runnable, templatePin);
        PinNumber<?> similarity = getPinValue(runnable, similarityPin);

        List<Rect> rectList = DisplayUtil.matchTemplate(source.getImage(), template.getImage(), null, similarity.intValue());
        rectList.forEach(rect -> areasPin.getValue(PinList.class).add(new PinArea(rect)));

        return !areasPin.getValue(PinList.class).isEmpty();
    }
}
