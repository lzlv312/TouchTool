package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import java.util.Collections;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinIconExecute;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.service.TaskRunnable;

public class ManualStartAction extends StartAction {
    private final transient Pin appsPin = new NotLinkAblePin(new PinApplications(PinSubType.MULTI_APP_WITH_ACTIVITY, MainApplication.getInstance().getString(R.string.common_package)), R.string.pin_app);
    private final transient Pin appPin = new Pin(new PinApplication(), R.string.manual_start_action_app, true);

    public ManualStartAction() {
        super(ActionType.MANUAL_START);
        executePin = new Pin(new PinIconExecute(), R.string.pin_execute, true);
        addPins(appsPin, appPin);
    }

    public ManualStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(appsPin, appPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        TaskInfoSummary handler = TaskInfoSummary.getInstance();
        TaskInfoSummary.PackageActivity packageActivity = handler.getPackageActivity();

        PinApplication application = new PinApplication(packageActivity.packageName());
        application.setActivityClasses(Collections.singletonList(packageActivity.activityName()));
        appPin.setValue(application);

        super.execute(runnable, pin);
    }

    @Override
    public boolean ready() {
        TaskInfoSummary handler = TaskInfoSummary.getInstance();
        TaskInfoSummary.PackageActivity packageActivity = handler.getPackageActivity();

        PinApplication application = new PinApplication(packageActivity.packageName());
        application.setActivityClasses(Collections.singletonList(packageActivity.activityName()));
        return appsPin.getValue(PinApplications.class).contains(application);
    }
}
