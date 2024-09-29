package top.bogey.touch_tool.ui.shortcut;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.databinding.ViewShortcutBinding;

public class ShortcutView extends Fragment {
    private ViewShortcutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewShortcutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
