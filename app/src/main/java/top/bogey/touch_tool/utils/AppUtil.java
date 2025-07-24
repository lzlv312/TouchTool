package top.bogey.touch_tool.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.DialogInputTextBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.ui.BaseActivity;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.callback.StringResultCallback;

public class AppUtil {
    public static final String LOG_DIR_NAME = "log";
    public final static String TASK_DIR_NAME = "task";
    public final static String DOCUMENT_DIR_NAME = "document";

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
            Intent intent = Intent.parseUri(scheme, 0);
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

    public static void copyToClipboard(Context context, String text) {
        AppUtil.copyToClipboard(context, text, true);
    }

    public static void copyToClipboard(Context context, String text, boolean showToast) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        clipboard.setPrimaryClip(clip);
        if (showToast) Toast.makeText(context, R.string.copy_tips, Toast.LENGTH_SHORT).show();
    }

    public static void copyToClipboard(Context context, Bitmap image, boolean showToast) {
        if (image == null) return;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        File file = writeFile(context, DOCUMENT_DIR_NAME, "Picture_" + formatDateTime(context, System.currentTimeMillis(), false, true) + ".jpg", outputStream.toByteArray());
        if (file == null) return;
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newUri(context.getContentResolver(), "Image", uri);
        clipboard.setPrimaryClip(clip);
        if (showToast) Toast.makeText(context, R.string.copy_tips, Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatRegex(String value) {
        if (value == null) return null;
        final String[] metaCharacters = {"\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "/"};

        // 逐个转义特殊字符
        for (String metaCharacter : metaCharacters) {
            if (value.contains(metaCharacter)) {
                value = value.replace(metaCharacter, "\\" + metaCharacter);
            }
        }
        return value;
    }

    public static boolean isStringContains(String str, String value) {
        Pattern pattern = getPattern(value);
        if (pattern == null) {
            return str.contains(value);
        } else {
            Matcher matcher = pattern.matcher(str);
            return matcher.find();
        }
    }

    public static String formatDate(Context context, long time, boolean ignoreYear) {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        if (current.get(Calendar.YEAR) != calendar.get(Calendar.YEAR) || !ignoreYear) builder.append(context.getString(R.string.year, current.get(Calendar.YEAR)));
        builder.append(context.getString(R.string.month, current.get(Calendar.MONTH) + 1));
        builder.append(context.getString(R.string.day, current.get(Calendar.DAY_OF_MONTH)));
        return builder.toString();
    }

    public static String formatTime(Context context, long time, boolean ignoreMillisecond) {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.hour, current.get(Calendar.HOUR_OF_DAY)));
        builder.append(context.getString(R.string.minute, current.get(Calendar.MINUTE)));
        builder.append(context.getString(R.string.second, current.get(Calendar.SECOND)));
        if (!ignoreMillisecond) builder.append(context.getString(R.string.millisecond, current.get(Calendar.MILLISECOND)));
        return builder.toString();
    }

    public static String formatDateTime(Context context, long time, boolean ignoreYear, boolean ignoreMillisecond) {
        return formatDate(context, time, ignoreYear) + " " + formatTime(context, time, ignoreMillisecond);
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
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
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

    public static File writeFile(Context context, String parent, String fileName, byte[] content) {
        File fileParent = context.getCacheDir();
        if (parent != null) fileParent = new File(fileParent, parent);

        File file = new File(fileParent, fileName);
        if (!file.exists()) {
            if (!fileParent.exists() && !fileParent.mkdirs()) return null;
            try {
                if (!file.createNewFile()) return null;
            } catch (IOException e) {
                return null;
            }
        }

        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content);
            outputStream.flush();
        } catch (IOException e) {
            return null;
        }

        return file;
    }

    public static void exportFile(BaseActivity activity, String fileName, byte[] content) {
        activity.launcherCreateDocument(fileName, (code, intent) -> {
            if (code == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                if (uri == null) return;
                try (OutputStream outputStream = activity.getContentResolver().openOutputStream(uri)) {
                    if (outputStream == null) return;
                    outputStream.write(content);
                    outputStream.flush();
                } catch (IOException ignored) {
                }
            }
        });
    }

    public static void shareImage(Context context, Bitmap image) {
        if (image == null) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/*");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        File file = writeFile(context, DOCUMENT_DIR_NAME, "Picture_" + formatDateTime(context, System.currentTimeMillis(), false, true) + ".jpg", outputStream.toByteArray());
        if (file != null) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(intent);
        }
    }

    public static void shareText(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/*");
        File file = writeFile(context, DOCUMENT_DIR_NAME, "Text_" + formatDateTime(context, System.currentTimeMillis(), false, true) + ".txt", text.getBytes());
        if (file != null) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(intent);
        }
    }

    public static byte[] readFile(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return new byte[0];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            byte[] byteArray = outputStream.toByteArray();
            outputStream.close();
            return byteArray;
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static void saveImage(Context context, Bitmap image) {
        if (image == null) return;

        String fileName = "Picture_" + formatDateTime(context, System.currentTimeMillis(), false, true) + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name));

            try {
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (uri == null) return;

                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    if (outputStream == null) return;
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
            } catch (IOException ignored) {

            }
        } else {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
            try (OutputStream outputStream = new FileOutputStream(file)) {
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
            } catch (IOException ignored) {
            }
        }
    }

    public static List<AccessibilityNodeInfo> getWindows(AccessibilityService service) {
        List<AccessibilityNodeInfo> windows = new ArrayList<>();
        List<AccessibilityWindowInfo> list = service.getWindows();
        for (AccessibilityWindowInfo window : list) {
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
