package top.bogey.touch_tool.ui.tool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.databinding.ViewToolBinding;

public class ToolView extends Fragment {
    private ViewToolBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewToolBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
