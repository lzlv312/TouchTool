package top.bogey.touch_tool.bean.action.system;

import com.google.gson.JsonObject;

import java.util.Collections;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.CalculateAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class GetCurrentAppAction extends CalculateAction {
    private final transient Pin appPin = new Pin(new PinApplication(), R.string.pin_app, true);

    public GetCurrentAppAction() {
        super(ActionType.GET_CURRENT_APPLICATION);
        addPin(appPin);
    }

    public GetCurrentAppAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPin(appPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, Pin pin) {
        TaskInfoSummary.PackageActivity packageActivity = TaskInfoSummary.getInstance().getPackageActivity();
        PinApplication application = appPin.getValue(PinApplication.class);
        application.setPackageName(packageActivity.packageName());
        application.setActivityClasses(Collections.singletonList(packageActivity.activityName()));
    }
}
