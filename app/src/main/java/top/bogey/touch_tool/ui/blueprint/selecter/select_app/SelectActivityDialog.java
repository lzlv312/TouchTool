package top.bogey.touch_tool.ui.blueprint.selecter.select_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Editable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.databinding.DialogSelectActivityBinding;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class SelectActivityDialog extends FrameLayout {

    public SelectActivityDialog(@NonNull Context context, PinApplications applications, PinApplication application, BooleanResultCallback callback, PackageInfo info) {
        super(context);
        DialogSelectActivityBinding binding = DialogSelectActivityBinding.inflate(LayoutInflater.from(context), this, true);

        SelectActivityDialogAdapter adapter = new SelectActivityDialogAdapter(applications, application, callback);
        binding.activityBox.setAdapter(adapter);
        List<ActivityInfo> activityInfoList = getActivities(applications.isShared(), info);
        adapter.refreshActivities(activityInfoList);

        PackageManager manager = getContext().getPackageManager();
        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();
                if (searchString.isEmpty()) {
                    adapter.refreshActivities(activityInfoList);
                } else {
                    List<ActivityInfo> newActivities = new ArrayList<>();
                    Pattern pattern = AppUtil.getPattern(searchString);
                    if (pattern == null) {
                        for (ActivityInfo activityInfo : activityInfoList) {
                            if (applications.isShared()) {
                                CharSequence name = activityInfo.loadLabel(manager);
                                if (name.toString().contains(searchString)) newActivities.add(activityInfo);
                            } else {
                                if (activityInfo.name.contains(searchString)) newActivities.add(activityInfo);
                            }
                        }
                    } else {
                        for (ActivityInfo activityInfo : activityInfoList) {
                            if (applications.isShared()) {
                                CharSequence name = activityInfo.loadLabel(manager);
                                if (pattern.matcher(name).find()) newActivities.add(activityInfo);
                            } else {
                                if (pattern.matcher(activityInfo.name).find()) newActivities.add(activityInfo);
                            }
                        }
                    }
                    adapter.refreshActivities(newActivities);
                }
            }
        });
    }

    private List<ActivityInfo> getActivities(boolean isShared, PackageInfo info) {
        List<ActivityInfo> activityInfoList = new ArrayList<>();
        if (isShared) {
            PackageManager manager = getContext().getPackageManager();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.setPackage(info.packageName);
            List<ResolveInfo> resolveInfos = manager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            for (ResolveInfo resolveInfo : resolveInfos) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                activityInfo.processName = (String) resolveInfo.loadLabel(manager);
                activityInfoList.add(activityInfo);
            }
        } else {
            if (info.activities != null) {
                for (ActivityInfo activityInfo : info.activities) {
                    if (activityInfo.exported) activityInfoList.add(activityInfo);
                }
            }
        }
        return activityInfoList;
    }
}
