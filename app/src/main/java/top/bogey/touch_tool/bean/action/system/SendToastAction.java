package top.bogey.touch_tool.bean.action.system;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.ExecuteAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.InstantActivity;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.ui.custom.ToastFloatView;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

public class SendToastAction extends ExecuteAction {
    private final transient Pin contentPin = new Pin(new PinString(), R.string.send_toast_action_content);
    private final transient Pin lengthPin = new Pin(new PinSingleSelect(R.array.toast_length_type), R.string.send_toast_action_duration, false, false, true);

    public SendToastAction() {
        super(ActionType.SEND_TOAST);
        addPins(contentPin, lengthPin);
    }

    public SendToastAction(JsonObject jsonObject) {
        super(jsonObject);
        reAddPins(contentPin, lengthPin);
    }

    @Override
    public void execute(TaskRunnable runnable, Pin pin) {
        PinObject content = getPinValue(runnable, contentPin);
        PinSingleSelect length = getPinValue(runnable, lengthPin);

        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(keepView.getContext(), content.toString(), length.getIndex()).show();
        });

        executeNext(runnable, outPin);
    }
}
