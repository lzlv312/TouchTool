package top.bogey.touch_tool.bean.action.start;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.special_pin.NotLinkAblePin;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class NotificationStartAction extends StartAction {
    private final transient Pin appsPin = new NotLinkAblePin(new PinApplications(PinSubType.MULTI_APP, MainApplication.getInstance().getString(R.string.common_package)), R.string.pin_app);
    private final transient Pin matchPin = new NotLinkAblePin(new PinString("."), R.string.notification_start_action_match);
    private final transient Pin notifyAppPin = new Pin(new PinApplication(PinSubType.SINGLE_APP), R.string.notification_start_action_notify_app, true);
    private final transient Pin notifyTextPin = new Pin(new PinString(), R.string.notification_start_action_notify_text, true);

    public NotificationStartAction() {
        super(ActionType.NOTIFICATION_START);
        addPins(appsPin, matchPin, notifyAppPin, notifyTextPin);
    }

    public NotificationStartAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(appsPin, matchPin, notifyAppPin, notifyTextPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        TaskInfoSummary.Notification notification = TaskInfoSummary.getInstance().getNotification();
        PinApplication application = new PinApplication(notification.packageName());
        notifyAppPin.setValue(application);
        notifyTextPin.getValue(PinString.class).setValue(notification.content());
    }

    @Override
    public boolean ready(TaskRunnable runnable) {
        TaskInfoSummary.Notification notification = TaskInfoSummary.getInstance().getNotification();
        PinApplication application = new PinApplication(notification.packageName());
        if (!appsPin.getValue(PinApplications.class).contains(application)) return false;
        return Objects.equals(notification.content(), notifyTextPin.getValue(PinString.class).getValue());
    }
}
