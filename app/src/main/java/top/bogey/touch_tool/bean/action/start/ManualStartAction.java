package top.bogey.touch_tool.bean.action.start;

import android.graphics.Point;

import com.google.gson.JsonObject;

import java.util.Collections;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.pin.special_pin.SingleSelectPin;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.EAnchor;

public class ManualStartAction extends StartAction {
    private final transient Pin appsPin = new NotLinkAblePin(new PinApplications(PinSubType.MULTI_APP_WITH_ACTIVITY, MainApplication.getInstance().getString(R.string.common_package)), R.string.pin_app);
    private final transient Pin showTypePin = new NotLinkAblePin(new PinSingleSelect(R.array.manual_action_show_type), R.string.manual_start_action_type, false, false, true);
    private final transient Pin showPosPin = new NotLinkAblePin(new PinPoint(), R.string.manual_start_action_pos, false, false, true);
    private final transient Pin anchorPin = new NotLinkAblePin(new PinSingleSelect(R.array.anchor, 0), R.string.manual_start_action_anchor, false, false, true);
    private final transient Pin appPin = new Pin(new PinApplication(), R.string.manual_start_action_app, true);

    public ManualStartAction() {
        super(ActionType.MANUAL_START);
        addPins(appsPin, showTypePin, showPosPin, anchorPin, appPin);
    }

    public ManualStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(appsPin, showTypePin, showPosPin, anchorPin, appPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        super.execute(runnable, pin);
        TaskInfoSummary handler = TaskInfoSummary.getInstance();
        TaskInfoSummary.PackageActivity packageActivity = handler.getPackageActivity();

        PinApplication application = new PinApplication(packageActivity.packageName());
        application.setActivityClasses(Collections.singletonList(packageActivity.activityName()));
        appPin.setValue(application);

        executeNext(runnable, executePin);
    }

    @Override
    public boolean ready() {
        TaskInfoSummary handler = TaskInfoSummary.getInstance();
        TaskInfoSummary.PackageActivity packageActivity = handler.getPackageActivity();

        PinApplication application = new PinApplication(packageActivity.packageName());
        application.setActivityClasses(Collections.singletonList(packageActivity.activityName()));
        return appsPin.getValue(PinApplications.class).contains(application);
    }

    public boolean isSingleShow() {
        return showTypePin.getValue(PinSingleSelect.class).getIndex() == 1;
    }

    public EAnchor getAnchor() {
        return EAnchor.values()[anchorPin.getValue(PinSingleSelect.class).getIndex()];
    }

    public Point getShowPos() {
        return showPosPin.getValue(PinPoint.class).getValue();
    }
}
