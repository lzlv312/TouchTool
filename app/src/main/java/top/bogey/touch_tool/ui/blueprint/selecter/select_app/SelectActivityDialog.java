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
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class SelectActivityDialog extends FrameLayout {

    public SelectActivityDialog(@NonNull Context context, PinApplications applications, PinApplication application, PackageInfo info) {
        super(context);
        DialogSelectActivityBinding binding = DialogSelectActivityBinding.inflate(LayoutInflater.from(context), this, true);

        SelectActivityDialogAdapter adapter = new SelectActivityDialogAdapter(applications, application);
        binding.activityBox.setAdapter(adapter);
        List<SelectActivityInfo> activityInfoList = getActivities(applications.isShared(), application, info);
        adapter.refreshActivities(activityInfoList);

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();
                if (searchString.isEmpty()) {
                    adapter.refreshActivities(activityInfoList);
                } else {
                    List<SelectActivityInfo> newActivities = new ArrayList<>();
                    Pattern pattern = AppUtil.getPattern(searchString);
                    if (pattern == null) {
                        for (SelectActivityInfo activityInfo : activityInfoList) {
                            if (applications.isShared()) {
                                if (activityInfo.label.contains(searchString)) newActivities.add(activityInfo);
                            } else {
                                if (activityInfo.name.contains(searchString)) newActivities.add(activityInfo);
                            }
                        }
                    } else {
                        for (SelectActivityInfo activityInfo : activityInfoList) {
                            if (applications.isShared()) {
                                if (pattern.matcher(activityInfo.label).find()) newActivities.add(activityInfo);
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

    private List<SelectActivityInfo> getActivities(boolean isShared, PinApplication application, PackageInfo info) {
        List<SelectActivityInfo> activityInfoList = new ArrayList<>();
        PackageManager manager = getContext().getPackageManager();
        String launcherActivityName = "";
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(info.packageName);
        List<ResolveInfo> resolveInfos = manager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        if (!resolveInfos.isEmpty()) {
            launcherActivityName = resolveInfos.get(0).activityInfo.name;
        }

        if (isShared) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.setPackage(info.packageName);
            resolveInfos = manager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            for (ResolveInfo resolveInfo : resolveInfos) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                activityInfo.processName = (String) resolveInfo.loadLabel(manager);
                activityInfoList.add(new SelectActivityInfo(
                        activityInfo,
                        activityInfo.name,
                        activityInfo.loadLabel(manager).toString(),
                        launcherActivityName.equals(activityInfo.name)
                ));
            }
        } else {
            if (info.activities != null) {
                for (ActivityInfo activityInfo : info.activities) {
                    if (activityInfo.exported)
                        activityInfoList.add(new SelectActivityInfo(
                                activityInfo,
                                activityInfo.name,
                                null,
                                launcherActivityName.equals(activityInfo.name)
                        ));
                }
            }
        }

        AppUtil.chineseSort(activityInfoList, activityInfo -> activityInfo.name);
        int index = 0;

        int i = 0;
        while (i < activityInfoList.size()) {
            SelectActivityInfo activityInfo = activityInfoList.get(i);
            if (activityInfo.isLauncher) {
                activityInfoList.add(0, activityInfoList.remove(i));
                index++;
            } else if (application.getActivityClasses() != null && application.getActivityClasses().contains(activityInfo.name)) {
                activityInfoList.add(index, activityInfoList.remove(i));
                index++;
            }
            i++;
        }

        return activityInfoList;
    }

    public record SelectActivityInfo(ActivityInfo activityInfo, String name, String label, boolean isLauncher) {
    }
}
