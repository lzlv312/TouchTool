package top.bogey.touch_tool.ui;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.InnerStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.action.start.TimeStartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class InstantActivity extends BaseActivity {
    public static final String INTENT_KEY_DO_ACTION = "INTENT_KEY_DO_ACTION";

    public static final String TASK_ID = "TASK_ID";
    public static final String ACTION_ID = "ACTION_ID";
    public static final String PIN_ID = "PIN_ID";

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        handleIntent(intent);
        moveTaskToBack(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        moveTaskToBack(true);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        setIntent(null);

        Uri uri = intent.getData();
        if (uri != null) {
            if ("tt".equals(uri.getScheme()) && "do_action".equals(uri.getHost()) && uri.getQuery() != null) {
                HashMap<String, String> params = new HashMap<>();
                for (String name : uri.getQueryParameterNames()) {
                    params.put(name, uri.getQueryParameter(name));
                }
                String taskId = params.remove(TASK_ID);
                String actionId = params.remove(ACTION_ID);
                doAction(taskId, actionId, null, params);
            }
        }

        boolean doAction = intent.getBooleanExtra(INTENT_KEY_DO_ACTION, false);
        if (doAction) {
            String taskId = intent.getStringExtra(TASK_ID);
            String actionId = intent.getStringExtra(ACTION_ID);
            String pinId = intent.getStringExtra(PIN_ID);
            doAction(taskId, actionId, pinId, null);
        }
    }

    public static void doAction(String taskId, String actionId, String pinId, Map<String, String> params) {
        if (taskId == null || actionId == null) return;

        Task task = Saver.getInstance().getTask(taskId);
        if (task == null) return;

        Action action = task.getAction(actionId);
        if (action == null || (action instanceof StartAction startAction && !startAction.isEnable())) return;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;

        if (action instanceof TimeStartAction timeStartAction) {
            service.addAlarm(task, timeStartAction);
        }

        if (action instanceof StartAction startAction) {
            Task copy = task.copy();
            if (params != null) {
                params.forEach((key, value) -> {
                    Variable var = copy.findVariableByName(key);
                    if (var != null) var.getSaveValue().cast(value);
                });
            }
            service.runTask(copy, startAction);
        } else {
            if (pinId == null) return;
            Pin pin = action.getPinById(pinId);
            if (pin == null) return;

            InnerStartAction innerStartAction = new InnerStartAction(pin);
            service.runTask(task, innerStartAction);
        }
    }
}
