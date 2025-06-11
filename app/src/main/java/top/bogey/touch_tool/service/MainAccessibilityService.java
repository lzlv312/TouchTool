package top.bogey.touch_tool.service;

import static top.bogey.touch_tool.service.TaskInfoSummary.OCR_SERVICE_ACTION;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.hardware.HardwareBuffer;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import top.bogey.ocr.IOcr;
import top.bogey.ocr.IOcrCallback;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.action.start.TimeStartAction;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.service.capture.CaptureService;
import top.bogey.touch_tool.service.receiver.SystemEventReceiver;
import top.bogey.touch_tool.ui.PermissionActivity;
import top.bogey.touch_tool.utils.callback.BitmapResultCallback;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.thread.TaskQueue;
import top.bogey.touch_tool.utils.thread.TaskThreadPoolExecutor;

public class MainAccessibilityService extends AccessibilityService {
    static {
        System.loadLibrary("native");
    }

    public static final MutableLiveData<Boolean> enabled = new MutableLiveData<>(false);
    public static final MutableLiveData<Boolean> connected = new MutableLiveData<>(false);

    private SystemEventReceiver receiver;
    private final TaskInfoSummary taskInfoSummary = TaskInfoSummary.getInstance();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        Log.d("TAG", "onAccessibilityEvent: " + event);

        if (event.getClassName() == null) return;

        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        if (packageName.isEmpty() || className.isEmpty()) return;
        Log.d("TAG", "onAccessibilityEvent: " + packageName + "/" + className);

        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            taskInfoSummary.enterActivity(packageName, className);
        } else if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (!className.contains(Notification.class.getSimpleName())) return;

            Notification notification = (Notification) event.getParcelableData();
            if (notification == null) return;
            if (notification.extras == null) return;
            String title = notification.extras.getString(Notification.EXTRA_TITLE);
            String text = notification.extras.getString(Notification.EXTRA_TEXT);
            if (title == null && text == null) return;
            Log.d("TAG", "onAccessibilityEvent: notification = " + title + "/" + text);

            taskInfoSummary.setNotification(packageName, title, text);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        connected.setValue(true);
        setEnabled(SettingSaver.getInstance().isEnabled());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        connected.setValue(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.getInstance().setService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connected.setValue(false);
        setEnabled(false);
        MainApplication.getInstance().setService(null);
    }

    public boolean isConnected() {
        return Boolean.TRUE.equals(connected.getValue());
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled.getValue()) && isConnected();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void setEnabled(boolean bool) {
        enabled.setValue(bool);

        if (isEnabled()) {
            receiver = new SystemEventReceiver(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(receiver, receiver.getFilter(), RECEIVER_EXPORTED);
            } else {
                registerReceiver(receiver, receiver.getFilter());
            }

            resetAllAlarm();
        } else {
            if (receiver != null) unregisterReceiver(receiver);
            receiver = null;
            if (tts != null) tts.shutdown();
            tts = null;

            stopAllTask();
            stopCapture();
            stopSound(null);
            cancelAllAlarm();
        }
    }

    // 任务 ----------------------------------------------------------------------------- start
    private final ExecutorService taskService = new TaskThreadPoolExecutor(5, 30, 30, TimeUnit.SECONDS, new TaskQueue<>(20));

    private final Set<TaskRunnable> tasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<TaskListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addListener(TaskListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TaskListener listener) {
        listeners.remove(listener);
    }

    public TaskRunnable runTask(Task task, StartAction startAction) {
        return runTask(task, startAction, null);
    }

    public TaskRunnable runTask(Task task, StartAction startAction, TaskListener listener) {
        if (task == null || startAction == null) return null;
        if (!isEnabled()) return null;

        if (startAction.getRestartType() == StartAction.RestartType.CANCEL) {
            if (isTaskRunning(task, startAction)) return null;
        }

        TaskRunnable runnable = new TaskRunnable(task, startAction);
        if (listener != null) runnable.addListener(listener);

        runnable.addListener(new TaskListener() {
            @Override
            public void onStart(TaskRunnable runnable) {
                StartAction startAction = runnable.getStartAction();
                if (startAction.getRestartType() == StartAction.RestartType.RESTART) {
                    stopTask(runnable.getStartTask());
                }
                tasks.add(runnable);
            }

            @Override
            public void onExecute(TaskRunnable runnable, Action action, int progress) {

            }

            @Override
            public void onCalculate(TaskRunnable runnable, Action action) {

            }

            @Override
            public void onFinish(TaskRunnable runnable) {
                tasks.remove(runnable);
            }
        });

        listeners.stream().filter(Objects::nonNull).forEach(runnable::addListener);
        Future<?> future = taskService.submit(runnable);
        runnable.setFuture(future);
        return runnable;
    }

    public boolean isTaskRunning(Task task) {
        if (task == null) return false;
        for (TaskRunnable runnable : tasks) {
            if (runnable.isInterrupt()) continue;

            Task startTask = runnable.getStartTask();
            if (task.getId().equals(startTask.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isTaskRunning(Task task, Action action) {
        if (task == null || action == null) return false;
        for (TaskRunnable runnable : tasks) {
            if (runnable.isInterrupt()) continue;

            Task startTask = runnable.getStartTask();
            Action startAction = runnable.getStartAction();
            if (task.getId().equals(startTask.getId()) && action.getId().equals(startAction.getId())) {
                return true;
            }
        }
        return false;
    }

    public void stopTask(Task task) {
        if (task == null) return;
        for (TaskRunnable runnable : tasks) {
            if (runnable.isInterrupt()) continue;

            Task startTask = runnable.getStartTask();
            if (task.getId().equals(startTask.getId())) {
                runnable.stop();
            }
        }
    }

    public void stopAllTask() {
        for (TaskRunnable runnable : tasks) {
            if (runnable.isInterrupt()) continue;
            runnable.stop();
        }
    }
    // 任务 ----------------------------------------------------------------------------- end

    // 定时 ----------------------------------------------------------------------------- start
    private PendingIntent getAlarmPendingIntent(String taskId, String actionId) {
        return null;
    }

    public void resetAllAlarm() {
        List<Task> tasks = Saver.getInstance().getTasks(TimeStartAction.class);
        for (Task task : tasks) {
            for (Action action : task.getActions(TimeStartAction.class)) {
                TimeStartAction timeStartAction = (TimeStartAction) action;
                cancelAlarm(task, timeStartAction);
                if (timeStartAction.isEnable()) {
                    addAlarm(task, timeStartAction);
                }
            }
        }
    }

    public void cancelAllAlarm() {
        List<Task> tasks = Saver.getInstance().getTasks(TimeStartAction.class);
        for (Task task : tasks) {
            for (Action action : task.getActions(TimeStartAction.class)) {
                TimeStartAction timeStartAction = (TimeStartAction) action;
                cancelAlarm(task, timeStartAction);
            }
        }
    }

    public void cancelAlarm(Task task, TimeStartAction timeStartAction) {
        if (task == null || timeStartAction == null) return;
        PendingIntent intent = getAlarmPendingIntent(task.getId(), timeStartAction.getId());
        if (intent != null) {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(intent);
        }
    }

    @SuppressLint("MissingPermission")
    public void addAlarm(Task task, TimeStartAction timeStartAction) {
        if (task == null || timeStartAction == null) return;
        if (!timeStartAction.isEnable()) return;
        if (!isEnabled()) return;
        if (!SettingSaver.getInstance().isAlarmEnabled()) return;

        PendingIntent pendingIntent = getAlarmPendingIntent(task.getId(), timeStartAction.getId());
        if (pendingIntent == null) return;

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long timeMillis = System.currentTimeMillis();
        long startTime = timeStartAction.getStartTime();
        long periodic = timeStartAction.getPeriodic();

        long nextStartTime = startTime;
        if (periodic > 0) {
            long l = timeMillis - startTime;
            // 当前时间没达到定时时间，下次执行时间就是开始时间
            if (l > 0) {
                // 当前时间大于开始时间，需要计算下次开始的时间，防止定时任务刚设定就执行了
                int loop = (int) Math.ceil(l * 1f / periodic);
                nextStartTime = startTime + loop * periodic;
                // 如果算出的下个开始时间没有大于现在时间10s，应该是算错了，下个开始时间再加个间隔
                if (nextStartTime - timeMillis < 10 * 1000) {
                    nextStartTime += periodic;
                }
            }
        }
        if (nextStartTime < timeMillis) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (manager.canScheduleExactAlarms()) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextStartTime, pendingIntent);
            } else {
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextStartTime, pendingIntent);
            }
        } else {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextStartTime, pendingIntent);
        }
    }

    public void replaceAlarm(Task task) {
        if (task == null) return;
        Task originTask = Saver.getInstance().getOriginTask(task.getId());
        List<Action> actions = task.getActions(TimeStartAction.class);

        if (originTask != null) {
            originTask.getActions(TimeStartAction.class).forEach(startAction -> {
                TimeStartAction timeStartAction = (TimeStartAction) startAction;
                if (!timeStartAction.isEnable()) return;

                boolean isExist = false;
                for (Action action : actions) {
                    if (action.getId().equals(timeStartAction.getId())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    cancelAlarm(task, timeStartAction);
                }
            });
        }

        actions.forEach(startAction -> {
            TimeStartAction timeStartAction = (TimeStartAction) startAction;
            if (timeStartAction.isEnable()) {
                addAlarm(task, timeStartAction);
            } else {
                cancelAlarm(task, timeStartAction);
            }
        });
    }

    // 定时 ----------------------------------------------------------------------------- end

    // 录屏 ----------------------------------------------------------------------------- start
    private BooleanResultCallback captureCallback;
    private ServiceConnection captureConnection = null;
    private CaptureService.CaptureBinder captureBinder = null;

    public boolean isCaptureEnabled() {
        if (isEnabled()) {
            return captureBinder != null;
        }
        return false;
    }

    public boolean startCapture(BooleanResultCallback callback) {
        if (captureBinder == null) {
            captureCallback = callback;
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.putExtra(PermissionActivity.CAPTURE_PERMISSION, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    public void stopCapture() {
        if (captureConnection != null) {
            unbindService(captureConnection);
            captureConnection = null;
            stopService(new Intent(this, CaptureService.class));
        }
        captureBinder = null;
    }

    public void bindCapture(boolean result, Intent data) {
        if (result) {
            captureConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    captureBinder = (CaptureService.CaptureBinder) service;
                    callCaptureCallback(true);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    stopCapture();
                }
            };

            Intent intent = new Intent(this, CaptureService.class);
            intent.putExtra(CaptureService.DATA, data);
            if (!bindService(intent, captureConnection, Context.BIND_AUTO_CREATE))
                callCaptureCallback(false);
        } else {
            callCaptureCallback(false);
        }
    }

    private void callCaptureCallback(boolean result) {
        if (captureCallback != null) {
            captureCallback.onResult(result);
            captureCallback = null;
        }
    }

    // 录屏 ----------------------------------------------------------------------------- end

    // 截图 ----------------------------------------------------------------------------- start
    private Bitmap lastScreenShot;

    public Bitmap getScreenShotByCapture() {
        return captureBinder != null ? captureBinder.getScreenShot() : null;
    }

    public boolean getScreenByAccessibility(BitmapResultCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            takeScreenshot(0, executorService, new TakeScreenshotCallback() {
                @Override
                public void onSuccess(@NonNull ScreenshotResult screenshot) {
                    try (HardwareBuffer hardwareBuffer = screenshot.getHardwareBuffer()) {
                        Bitmap bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, screenshot.getColorSpace());
                        if (bitmap != null) {
                            lastScreenShot = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                            bitmap.recycle();
                            callback.onResult(lastScreenShot);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onResult(lastScreenShot);
                    }
                }

                @Override
                public void onFailure(int errorCode) {
                    callback.onResult(lastScreenShot);
                }
            });
            return true;
        }
        return false;
    }

    public void tryGetScreenShot(BitmapResultCallback callback) {
        Bitmap bitmap = getScreenShotByCapture();
        if (bitmap != null) {
            callback.onResult(bitmap);
        } else {
            getScreenByAccessibility(callback);
        }
    }

    // 截图 ----------------------------------------------------------------------------- end

    // Ocr ----------------------------------------------------------------------------- start
    private final Map<String, IOcr> ocrBinderMap = new HashMap<>();

    public synchronized void runOcr(String packageName, Bitmap bitmap, ResultCallback<List<OcrResult>> callback) {
        IOcr iOcr = ocrBinderMap.get(packageName);
        if (iOcr == null || !iOcr.asBinder().isBinderAlive()) {
            ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    IOcr iOcr = IOcr.Stub.asInterface(service);
                    ocrBinderMap.put(packageName, iOcr);
                    try {
                        iOcr.runOcr(bitmap, new IOcrCallback.Stub() {
                            @Override
                            public void onResult(List<OcrResult> result) {
                                callback.onResult(result);
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    ocrBinderMap.remove(packageName);
                }
            };

            Intent intent = new Intent(OCR_SERVICE_ACTION);
            intent.setComponent(new ComponentName(packageName, OCR_SERVICE_ACTION));
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } else {
            try {
                iOcr.runOcr(bitmap, new IOcrCallback.Stub() {
                    @Override
                    public void onResult(List<OcrResult> result) {
                        callback.onResult(result);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    // Ocr ----------------------------------------------------------------------------- end

    // 按键 ----------------------------------------------------------------------------- start
    private Handler handler;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (handler == null) handler = new Handler();

        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                handler.postDelayed(() -> setEnabled(!isEnabled()), 5000);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                handler.removeCallbacksAndMessages(null);
            }
        }
        return super.onKeyEvent(event);
    }
    // 按键 ----------------------------------------------------------------------------- end

    // 手势 ----------------------------------------------------------------------------- start
    public void runGesture(int x, int y, int time, BooleanResultCallback callback) {
        if (x >= 0 && y >= 0 && time > 0) {
            Path path = new Path();
            path.moveTo(x, y);
            runGesture(Collections.singleton(new GestureDescription.StrokeDescription(path, 0, time)), callback);
            return;
        }
        if (callback != null) callback.onResult(false);
    }

    public void runGesture(Set<GestureDescription.StrokeDescription> strokeDescriptions, BooleanResultCallback callback) {
        if (strokeDescriptions == null || strokeDescriptions.isEmpty()) {
            if (callback != null) callback.onResult(false);
            return;
        }

        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription strokeDescription : strokeDescriptions) {
            builder.addStroke(strokeDescription);
        }
        dispatchGesture(builder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                if (callback != null) callback.onResult(true);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                if (callback != null) callback.onResult(false);
            }
        }, null);
    }

    public void runGesture(List<Set<GestureDescription.StrokeDescription>> strokeDescriptions, BooleanResultCallback callback) {
        if (strokeDescriptions == null || strokeDescriptions.isEmpty()) {
            if (callback != null) callback.onResult(false);
            return;
        }

        GestureHandler gestureHandler = new GestureHandler(strokeDescriptions, callback);
        gestureHandler.dispatchGesture();
    }

    private class GestureHandler {
        private final List<Set<GestureDescription.StrokeDescription>> strokeList;
        private final BooleanResultCallback callback;

        public GestureHandler(List<Set<GestureDescription.StrokeDescription>> strokeList, BooleanResultCallback callback) {
            this.strokeList = strokeList;
            this.callback = callback;
        }

        private void dispatchGesture() {
            if (strokeList.isEmpty()) {
                if (callback != null) callback.onResult(true);
                return;
            }
            runGesture(strokeList.remove(0), result -> {
                if (result) {
                    dispatchGesture();
                } else {
                    if (callback != null) callback.onResult(false);
                }
            });
        }
    }
    // 手势 ----------------------------------------------------------------------------- end

    // 播放声音 ----------------------------------------------------------------------------- start
    private final Map<String, MediaPlayer> playerMap = new HashMap<>();

    public void playSound(String path) {
        stopSound(path);
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(MainApplication.getInstance(), Uri.parse(path));
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(mp -> stopSound(path));
            mediaPlayer.prepareAsync();
            playerMap.put(path, mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSound(String path) {
        if (path == null || path.isEmpty()) {
            playerMap.forEach((s, mediaPlayer) -> {
                mediaPlayer.stop();
                mediaPlayer.release();
            });
            playerMap.clear();
            return;
        }

        MediaPlayer mediaPlayer = playerMap.remove(path);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private TextToSpeech tts;

    public void speak(String text) {
        if (tts == null) {
            tts = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.CHINA);
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // 播放声音 ----------------------------------------------------------------------------- end
}
