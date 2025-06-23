package top.bogey.touch_tool.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.accessibility.selecttospeak.SelectToSpeakService;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.callback.ActivityResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

public class BaseActivity extends AppCompatActivity {
    static {
        System.loadLibrary("native");
    }

    private ActivityResultLauncher<Intent> intentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String[]> openDocumentLauncher;
    private ActivityResultLauncher<String> createDocumentLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    private ActivityResultCallback resultCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("BaseActivity", "onCreate: " + this.getClass().getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (resultCallback != null) {
                resultCallback.onResult(result.getResultCode(), result.getData());
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result && resultCallback != null) resultCallback.onResult(RESULT_OK, null);
        });

        openDocumentLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
            if (result != null && resultCallback != null) {
                Intent intent = new Intent();
                intent.setData(result);
                resultCallback.onResult(RESULT_OK, intent);
            }
        });

        createDocumentLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/*"), result -> {
            if (result != null && resultCallback != null) {
                Intent intent = new Intent();
                intent.setData(result);
                resultCallback.onResult(RESULT_OK, intent);
            }
        });

        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
            if (result != null && resultCallback != null) {
                Intent intent = new Intent();
                intent.setData(result);
                resultCallback.onResult(RESULT_OK, intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("BaseActivity", "onStart: " + this.getClass().getName());

        MainAccessibilityService.enabled.observe(this, enabled -> {
            if (MainApplication.getInstance().getService() == null) return;
            if (enabled) {
                View view = FloatWindow.getView(KeepAliveFloatView.class.getName());
                if (view != null) return;
                new KeepAliveFloatView(this).show();
            } else {
                FloatWindow.dismiss(KeepAliveFloatView.class.getName());
            }
        });

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isEnabled()) {
            View view = FloatWindow.getView(KeepAliveFloatView.class.getName());
            if (view != null) return;
            new KeepAliveFloatView(this).show();
        }
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
        FloatWindow.dismiss(KeepAliveFloatView.class.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("BaseActivity", "onResume: " + this.getClass().getName());
        restartAccessibilityServiceBySecurePermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("BaseActivity", "onPause: " + this.getClass().getName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("BaseActivity", "onStop: " + this.getClass().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BaseActivity", "onDestroy: " + this.getClass().getName());
        intentLauncher = null;
        permissionLauncher = null;
        openDocumentLauncher = null;
        createDocumentLauncher = null;
        pickMediaLauncher = null;
        resultCallback = null;
    }

    public void launchCapture(ActivityResultCallback callback) {
        if (intentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        intentLauncher.launch(manager.createScreenCaptureIntent());
    }

    public void launchNotification(ActivityResultCallback callback) {
        if (permissionLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String permission = Manifest.permission.POST_NOTIFICATIONS;
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                if (callback != null) callback.onResult(Activity.RESULT_OK, null);
            } else if (shouldShowRequestPermissionRationale(permission)) {
                AppUtil.showDialog(this, R.string.setting_need_notification_desc, result -> {
                    if (result) {
                        resultCallback = callback;
                        permissionLauncher.launch(permission);
                    } else {
                        if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
                    }
                });
            } else {
                resultCallback = callback;
                permissionLauncher.launch(permission);
            }
        } else {
            if (callback != null) callback.onResult(Activity.RESULT_OK, null);
        }
    }

    public void launcherOpenDocument(ActivityResultCallback callback, String mimeType) {
        if (openDocumentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        try {
            openDocumentLauncher.launch(new String[]{mimeType});
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void launcherCreateDocument(String fileName, ActivityResultCallback callback) {
        if (createDocumentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        try {
            createDocumentLauncher.launch(fileName);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void launcherPickMedia(ActivityResultCallback callback, ActivityResultContracts.PickVisualMedia.VisualMediaType type) {
        if (pickMediaLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;

        try {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(type)
                    .build());
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void launcherRingtone(String path, ActivityResultCallback callback) {
        if (intentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE | RingtoneManager.TYPE_ALARM);
        if (path != null && !path.isEmpty()) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(path));
        }
        try {
            intentLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void launcherBluetooth(ActivityResultCallback callback) {
        if (permissionLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }

        String permission;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            permission = Manifest.permission.BLUETOOTH;
        } else {
            permission = Manifest.permission.BLUETOOTH_CONNECT;
        }
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            if (callback != null) callback.onResult(Activity.RESULT_OK, null);
        } else if (shouldShowRequestPermissionRationale(permission)) {
            AppUtil.showDialog(this, R.string.permission_setting_bluetooth_desc, result -> {
                if (result) {
                    resultCallback = callback;
                    permissionLauncher.launch(permission);
                } else {
                    if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
                }
            });
        } else {
            resultCallback = callback;
            permissionLauncher.launch(permission);
        }
    }

    public void restartAccessibilityServiceBySecurePermission() {
        // 界面打开时尝试恢复无障碍服务
        // 如果应用服务设置关闭了，就啥都不管
        if (!SettingSaver.getInstance().isServiceEnabled()) return;

        // 是否有权限去重启无障碍服务
        if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) return;

        // 看一下服务有没有开启
        if (AppUtil.isAccessibilityServiceEnabled(this)) return;

        // 没有开启去开启
        String enabledService = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, String.format("%s:%s/%s", enabledService, getPackageName(), SelectToSpeakService.class.getName()));
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    public boolean stopAccessibilityServiceBySecurePermission() {
        // 是否有权限去重启无障碍服务
        if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) return false;

        // 看一下服务有没有开启
        if (AppUtil.isAccessibilityServiceEnabled(this)) {
            // 开启去关闭
            String enabledService = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            String replace = enabledService.replaceFirst(String.format(":?%s/%s", getPackageName(), SelectToSpeakService.class.getName()), "");
            Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, replace);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
        }
        return true;
    }
}
