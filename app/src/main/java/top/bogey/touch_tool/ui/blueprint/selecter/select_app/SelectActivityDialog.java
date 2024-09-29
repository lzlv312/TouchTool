package top.bogey.touch_tool.ui.blueprint.selecter.select_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.text.Editable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplications;
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
        List<ActivityInfo> activityInfoList = new ArrayList<>();
        if (info.activities != null) Collections.addAll(activityInfoList, info.activities);
        adapter.refreshActivities(activityInfoList);

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
                            if (activityInfo.name.contains(searchString)) newActivities.add(activityInfo);
                        }
                    } else {
                        for (ActivityInfo activityInfo : activityInfoList) {
                            if (pattern.matcher(activityInfo.name).find()) newActivities.add(activityInfo);
                        }
                    }
                    adapter.refreshActivities(newActivities);
                }
            }
        });
    }
}
