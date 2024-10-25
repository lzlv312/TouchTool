package top.bogey.touch_tool.bean.action.image;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.MatchResult;

public class ImageEqualAction extends CalculateAction {
    private final transient Pin sourceImage = new Pin(new PinImage(), R.string.pin_image);
    private final transient Pin templatePin = new Pin(new PinImage(), R.string.find_images_action_template);
    private final transient Pin similarityPin = new Pin(new PinInteger(), R.string.find_images_action_similarity, true);

    public ImageEqualAction() {
        super(ActionType.IMAGE_EQUAL);
        addPins(sourceImage, templatePin, similarityPin);
    }

    public ImageEqualAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(sourceImage, templatePin, similarityPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        PinImage source = getPinValue(runnable, sourceImage);
        PinImage template = getPinValue(runnable, templatePin);
        List<MatchResult> matchResults = DisplayUtil.nativeMatchTemplate(source.getImage(), template.getImage(), 0);
        if (!matchResults.isEmpty()) {
            MatchResult matchResult = matchResults.get(0);
            similarityPin.getValue(PinInteger.class).setValue((int) matchResult.value);
        }
    }
}
