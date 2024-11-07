package top.bogey.touch_tool.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.DialogInputTextBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.callback.StringResultCallback;

public class AppUtil {
    // 判断当前环境是否为发布环境
    public static boolean isRelease(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0;
    }

    public static void showDialog(Context context, @StringRes int msg, BooleanResultCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title)
                .setMessage(msg)
                .setPositiveButton(R.string.enter, (dialog, which) -> callback.onResult(true))
                .setNegativeButton(R.string.cancel, (dialog, which) -> callback.onResult(false))
                .show();
    }

    public static void showDialog(Context context, String msg, BooleanResultCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title)
                .setMessage(msg)
                .setPositiveButton(R.string.enter, (dialog, which) -> callback.onResult(true))
                .setNegativeButton(R.string.cancel, (dialog, which) -> callback.onResult(false))
                .show();
    }

    public static void showEditDialog(Context context, @StringRes int title, CharSequence defaultValue, StringResultCallback callback) {
        DialogInputTextBinding binding = DialogInputTextBinding.inflate(LayoutInflater.from(context));
        binding.titleEdit.setText(defaultValue);

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    if (binding.titleEdit.getText() == null) {
                        callback.onResult(null);
                    } else {
                        callback.onResult(binding.titleEdit.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> callback.onResult(null))
                .show();
    }

    public static void gotoAppDetailView(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoIgnoreBattery(Context context) {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isIgnoredBattery(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public static TaskInfoSummary.PhoneState getPhoneState(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = powerManager.isInteractive();
        if (screenOn) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean locked = keyguardManager.isKeyguardLocked();
            if (locked) return TaskInfoSummary.PhoneState.LOCKED;
            else return TaskInfoSummary.PhoneState.ON;
        }
        return TaskInfoSummary.PhoneState.OFF;
    }

    public static void wakePhone(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, context.getPackageName());
        wakeLock.acquire(1000);
        wakeLock.release();
    }

    public static void gotoApp(Context context, String packageName, Map<String, String> params) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (params != null) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        intent.putExtra(entry.getKey(), entry.getValue());
                    }
                }

                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoActivity(Context context, String packageName, String activityName, Map<String, String> params) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setComponent(new ComponentName(packageName, activityName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (params != null) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        intent.putExtra(entry.getKey(), entry.getValue());
                    }
                }

                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoScheme(Context context, String scheme) {
        try {
            Intent intent = Intent.parseUri(scheme, Intent.URI_INTENT_SCHEME | Intent.URI_ANDROID_APP_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        for (AccessibilityServiceInfo info : manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)) {
            if (info.getId().contains(context.getPackageName() + "/")) {
                return true;
            }
        }
        return false;
    }

    public static <T> void chineseSort(List<T> list, Function<T, String> function) {
        Collator collator = Collator.getInstance(Locale.CHINA);
        list.sort((o1, o2) -> collator.compare(function.apply(o1), function.apply(o2)));
    }

    public static Pattern getPattern(String pattern) {
        try {
            return Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            return null;
        }
    }

    @SuppressLint("DefaultLocale")
    public static String formatDate(long time) {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        if (current.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) builder.append(current.get(Calendar.YEAR)).append("-");
        builder.append(String.format("%02d", current.get(Calendar.MONTH) + 1)).append("-");
        builder.append(String.format("%02d", current.get(Calendar.DAY_OF_MONTH)));
        return builder.toString();
    }

    @SuppressLint("DefaultLocale")
    public static String formatTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    @SuppressLint("DefaultLocale")
    public static String formatTimeMillisecond(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return String.format("%02d:%02d:%02d.%03d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
    }

    public static String formatDateTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatDate(time) + " " + formatTime(time);
    }

    public static String formatDuration(Context context, long duration) {
        long hour = duration / 3600000;
        long minute = (duration % 3600000) / 60000;

        StringBuilder builder = new StringBuilder();
        if (hour > 0) builder.append(context.getString(R.string.hours, hour));
        if (minute > 0) builder.append(context.getString(R.string.minutes, minute));
        return builder.toString();
    }

    public static long mergeDateTime(long date, long time) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(time);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DATE), timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), 0);
        return calendar.getTimeInMillis();
    }

    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void copyFile(String source, String destination) {
        if (source == null || source.isEmpty() || destination == null || destination.isEmpty()) return;

        File sourceFile = new File(source);
        String[] files = sourceFile.list();
        if (files == null) return;

        for (String file : files) {
            String sourcePath = source + File.separator + file;
            String destinationPath = destination + File.separator + file;
            if (new File(sourcePath).isDirectory()) {
                if (new File(destinationPath).mkdirs()) {
                    copyFile(sourcePath, destinationPath);
                }
            } else {
                try (InputStream inputStream = new BufferedInputStream(new FileInputStream(sourcePath))) {
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationPath));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static long getFileSize(File file) {
        if (file == null || !file.exists()) return 0;
        if (file.isFile()) return file.length();
        long size = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                size += getFileSize(f);
            }
        }
        return size;
    }

    @SuppressLint("DefaultLocale")
    public static String getFileSizeString(File file) {
        long size = getFileSize(file);
        double kb = size / 1024.0;
        if (kb < 1024) return String.format("%.1fKB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.1fMB", mb);
        return String.format("%.1fGB", mb / 1024.0);
    }

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) return false;
        if (file.isFile()) return file.delete();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!deleteFile(f)) return false;
            }
        }
        return true;
    }

    public static Uri writeToInner(Context context, String path, byte[] content) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try(OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
    }

    public static void saveImage(Context context, Bitmap image) {
        if (image == null) return;
        String fileName = "share_" + System.currentTimeMillis() + ".jpg";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (OutputStream outputStream = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<AccessibilityNodeInfo> getWindows(AccessibilityService service) {
        List<AccessibilityNodeInfo> windows = new ArrayList<>();
        for (AccessibilityWindowInfo window : service.getWindows()) {
            if (window == null) continue;
            if (window.getType() == AccessibilityWindowInfo.TYPE_ACCESSIBILITY_OVERLAY) continue;
            AccessibilityNodeInfo root = window.getRoot();
            if (root == null) continue;
            if (root.getChildCount() == 0) continue;
            windows.add(root);
        }
        return windows;
    }
}
