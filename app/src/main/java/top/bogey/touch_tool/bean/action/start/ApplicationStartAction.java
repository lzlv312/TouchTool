package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import java.util.Collections;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplications;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class ApplicationStartAction extends StartAction {
    private final transient Pin appsPin = new Pin(new PinApplications(PinSubType.MULTI_APP_WITH_ACTIVITY, MainApplication.getInstance().getString(R.string.common_package)), R.string.pin_app) {
        @Override
        public boolean linkAble() {
            return false;
        }
    };
    private final transient Pin breakPin = new Pin(new PinBoolean(true), R.string.application_start_action_break) {
        @Override
        public boolean linkAble() {
            return false;
        }
    };
    private final transient Pin appPin = new Pin(new PinApplication(), R.string.application_start_action_app, true);

    public ApplicationStartAction() {
        super(ActionType.APPLICATION_START);
        addPins(appsPin, breakPin, appPin);
    }

    public ApplicationStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(appsPin, breakPin, appPin);
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
    public boolean ready(TaskRunnable runnable) {
        TaskInfoSummary handler = TaskInfoSummary.getInstance();
        TaskInfoSummary.PackageActivity packageActivity = handler.getPackageActivity();

        PinApplication application = new PinApplication(packageActivity.packageName());
        application.setActivityClasses(Collections.singletonList(packageActivity.activityName()));
        return appsPin.getValue(PinApplications.class).contains(application);
    }

    @Override
    public boolean stop(TaskRunnable runnable) {
        if (super.stop(runnable)) return true;

        if (breakPin.getValue(PinBoolean.class).getValue()) {
            return !ready(runnable);
        }
        return false;
    }
}
