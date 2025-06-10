package top.bogey.touch_tool.ui.blueprint.selecter.select_app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.databinding.DialogSelectAppBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class SelectAppDialog extends BottomSheetDialog {
    private final PinApplications applications;
    private final SelectAppDialogAdapter adapter;

    private final DialogSelectAppBinding binding;
    private String searchString;
    private boolean includeSystemApp;

    public SelectAppDialog(Context context, PinApplications applications, BooleanResultCallback callback) {
        super(context);
        this.applications = applications;
        adapter = new SelectAppDialogAdapter(applications, callback);

        binding = DialogSelectAppBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        binding.appIconBox.setAdapter(adapter);
        adapter.refreshApps(searchApps());

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                searchString = s.toString();
                adapter.refreshApps(searchApps());
            }
        });

        binding.exchangeButton.setOnClickListener(v -> {
            includeSystemApp = !includeSystemApp;
            adapter.refreshApps(searchApps());
        });

        Point size = DisplayUtil.getScreenSize(context);
        DisplayUtil.setViewHeight(binding.getRoot(), (int) (size.y * 0.7f));
        DisplayUtil.setViewWidth(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private List<PackageInfo> searchApps() {
        List<PackageInfo> apps;
        if (applications.isShared()) {
            apps = TaskInfoSummary.getInstance().findSendApps(searchString, includeSystemApp);
        } else {
            apps = TaskInfoSummary.getInstance().findApps(searchString, includeSystemApp);
        }
        binding.editBox.setHint(getContext().getString(includeSystemApp ? R.string.select_app_search_all : R.string.select_app_search_third, apps.size()));
        if (!applications.isSingle() && (searchString == null || searchString.isEmpty())) {
            PackageInfo info = new PackageInfo();
            info.packageName = getContext().getString(R.string.common_package);
            apps.add(0, info);
        }

        List<String> packageNames = applications.getPackageNames();
        int index = 0;
        int i = 0;
        while (i < apps.size()) {
            PackageInfo packageInfo = apps.get(i);
            if (packageNames.contains(packageInfo.packageName)) {
                apps.add(index, apps.remove(i));
                index++;
            }
            i++;
        }

        return apps;
    }
}
