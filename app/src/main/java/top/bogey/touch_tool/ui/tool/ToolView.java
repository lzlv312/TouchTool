package top.bogey.touch_tool.ui.tool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.databinding.ViewToolBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.blueprint.picker.NodePickerPreview;
import top.bogey.touch_tool.ui.tool.app_info.AppInfoFloatView;
import top.bogey.touch_tool.ui.tool.log.LogFloatView;

public class ToolView extends Fragment {
    private ViewToolBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewToolBinding.inflate(inflater, container, false);

        binding.captureButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                if (service.isCaptureEnabled()) {
                    service.stopCapture();
                } else {
                    service.startCapture(null);
                }
            }
        });

        binding.appInfoButton.setOnClickListener(v -> new AppInfoFloatView(requireActivity()).show());

        binding.nodePickerButton.setOnClickListener(v -> new NodePickerPreview(requireActivity(), null,  null).show());

        binding.logButton.setOnClickListener(v -> new LogFloatView(requireActivity()).show());

        return binding.getRoot();
    }
}
