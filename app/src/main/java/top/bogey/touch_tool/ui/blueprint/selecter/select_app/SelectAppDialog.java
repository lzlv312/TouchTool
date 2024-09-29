package top.bogey.touch_tool.ui.blueprint.selecter.select_app;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.pins.pin_application.PinApplications;
import top.bogey.touch_tool.databinding.DialogSelectAppBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class SelectAppDialog extends BottomSheetDialogFragment {
    private final PinApplications applications;
    private final SelectAppDialogAdapter adapter;

    private DialogSelectAppBinding binding;
    private String searchString;
    private boolean includeSystemApp;

    public SelectAppDialog(PinApplications applications, BooleanResultCallback callback) {
        super();
        this.applications = applications;
        adapter = new SelectAppDialogAdapter(applications, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSelectAppBinding.inflate(inflater, container, false);

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

        return binding.getRoot();
    }

    private List<PackageInfo> searchApps() {
        List<PackageInfo> apps;
        if (applications.isShared()) {
            apps = TaskInfoSummary.getInstance().findSendApps(searchString, includeSystemApp);
        } else {
            apps = TaskInfoSummary.getInstance().findApps(searchString, includeSystemApp);
        }
        binding.infoText.setText(getString(includeSystemApp ? R.string.select_app_all : R.string.select_app_third, apps.size()));
        if (!applications.isSingle() && (searchString == null || searchString.isEmpty())) {
            PackageInfo info = new PackageInfo();
            info.packageName = getString(R.string.common_package);
            apps.add(0, info);
        }
        return apps;
    }
}
