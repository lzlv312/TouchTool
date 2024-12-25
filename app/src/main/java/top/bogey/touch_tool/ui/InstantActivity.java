package top.bogey.touch_tool.ui;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.OutCallStartAction;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.action.start.TimeStartAction;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class InstantActivity extends BaseActivity {
    public static final String INTENT_KEY_DO_ACTION = "INTENT_KEY_DO_ACTION";

    public static final String TASK_ID = "TASK_ID";
    public static final String ACTION_ID = "ACTION_ID";

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

        Uri uri = intent.getData();
        if (uri != null) {
            if ("ttp".equals(uri.getScheme()) && "do_action".equals(uri.getHost()) && uri.getQuery() != null) {
                MainAccessibilityService service = MainApplication.getInstance().getService();
                if (service != null && service.isEnabled()) {
                    HashMap<String, String> params = new HashMap<>();
                    for (String name : uri.getQueryParameterNames()) {
                        params.put(name, uri.getQueryParameter(name));
                    }
                    String taskId = params.remove(TASK_ID);
                    String actionId = params.remove(ACTION_ID);
                    if (taskId != null && actionId != null) {
                        Task task = Saver.getInstance().getTask(taskId);
                        if (task != null) {
                            Action action = task.getAction(actionId);
                            if (action instanceof OutCallStartAction) {
                                Task copy = task.copy();
                                params.forEach((key, value) -> {
                                    Variable var = copy.getVar(key);
                                    if (var != null) var.getValue().cast(value);
                                });

                                service.runTask(task, (StartAction) action);
                            }
                        }
                    }
                }
            }
        }

        boolean doAction = intent.getBooleanExtra(INTENT_KEY_DO_ACTION, false);
        if (doAction) {
            String taskId = intent.getStringExtra(TASK_ID);
            String actionId = intent.getStringExtra(ACTION_ID);

            if (taskId != null && actionId != null) {
                Task task = Saver.getInstance().getTask(taskId);
                if (task != null) {
                    Action action = task.getAction(actionId);
                    MainAccessibilityService service = MainApplication.getInstance().getService();
                    if (service != null && service.isEnabled()) {
                        if (action instanceof StartAction startAction) {
                            service.runTask(task, startAction);
                        }
                        if (action instanceof TimeStartAction timeStartAction) {
                            service.addAlarm(task, timeStartAction);
                        }
                    }
                }
            }
        }

        setIntent(null);
    }
}
