package top.bogey.touch_tool.ui.setting;

import static top.bogey.touch_tool.ui.play.PlayFloatView.UNIT_PIXEL;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.databinding.ViewSettingPlayViewBinding;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.utils.DisplayUtil;

public class SettingPlayView extends Fragment {
    private ViewSettingPlayViewBinding binding;
    private int padding;
    private String testText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        padding = (int) DisplayUtil.dp2px(activity, 8);
        testText = getString(R.string.preference_setting_manual_play_size_test_text);

        binding = ViewSettingPlayViewBinding.inflate(inflater, container, false);
        binding.toolBar.setNavigationOnClickListener(v -> activity.getOnBackPressedDispatcher().onBackPressed());

        // 手动执行
        binding.manualPlaySelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setManualPlayShowType(index);
            }
        });
        binding.manualPlaySelect.checkIndex(SettingSaver.getInstance().getManualPlayShowType());

        // 暂停/停止
        binding.manualPlayPauseSelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setManualPlayPauseType(index);
            }
        });
        binding.manualPlayPauseSelect.checkIndex(SettingSaver.getInstance().getManualPlayPauseType());

        // 隐藏悬浮窗
        binding.hideManualPlaySelect.setOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int index = group.indexOfChild(view);
                SettingSaver.getInstance().setManualPlayHideType(index);
            }
        });
        binding.hideManualPlaySelect.checkIndex(SettingSaver.getInstance().getManualPlayHideType());

        // 重置位置
        binding.manualPlayReset.setOnButtonClickListener(v -> {
            SettingSaver.getInstance().setManualPlayViewPos(new Point());
            SettingSaver.getInstance().setManualPlayViewState(true);
            Toast.makeText(activity, R.string.preference_setting_manual_play_reset_tips, Toast.LENGTH_SHORT).show();
        });

        // 手动执行悬浮窗偏移
        binding.manualPlayPadding.setSliderOnChangeListener((slider, value, fromUser) -> {
            SettingSaver.getInstance().setManualPlayViewPadding((int) value);
            refreshExpandView();
            refreshCloseView();
        });
        binding.manualPlayPadding.setValue(SettingSaver.getInstance().getManualPlayViewPadding());

        // 手动执行悬浮窗展开宽度
        binding.manualPlaySize.setSliderOnChangeListener((slider, value, fromUser) -> {
            SettingSaver.getInstance().setManualPlayViewExpandSize((int) value);
            refreshExpandView();
        });
        binding.manualPlaySize.setValue(SettingSaver.getInstance().getManualPlayViewExpandSize());

        // 手动执行悬浮窗关闭宽度
        binding.manualPlayCloseSize.setSliderOnChangeListener((slider, value, fromUser) -> {
            SettingSaver.getInstance().setManualPlayViewCloseSize((int) value);
            refreshCloseView();
        });
        binding.manualPlayCloseSize.setValue(SettingSaver.getInstance().getManualPlayViewCloseSize());

        // 手动执行悬浮窗按钮高度
        binding.manualPlayHeight.setSliderOnChangeListener((slider, value, fromUser) -> {
            SettingSaver.getInstance().setManualPlayViewButtonHeight((int) value);
            refreshExpandView();
        });
        binding.manualPlayHeight.setValue(SettingSaver.getInstance().getManualPlayViewButtonHeight());

        // 手动执行悬浮窗独立按钮大小
        binding.manualPlaySingleSize.setSliderOnChangeListener((slider, value, fromUser) -> {
            SettingSaver.getInstance().setManualPlayViewSingleSize((int) value);
            refreshSingleView();
        });
        binding.manualPlaySingleSize.setValue(SettingSaver.getInstance().getManualPlayViewSingleSize());

        refreshExpandView();
        refreshSingleView();
        refreshCloseView();

        return binding.getRoot();
    }

    private void refreshExpandView() {
        int padding = SettingSaver.getInstance().getManualPlayViewPadding();
        int size = SettingSaver.getInstance().getManualPlayViewExpandSize();
        int height = SettingSaver.getInstance().getManualPlayViewButtonHeight();

        DisplayUtil.setViewMargin(binding.playButtonBox, padding * UNIT_PIXEL, this.padding, padding * UNIT_PIXEL, this.padding);
        String testText = this.testText.substring(0, Math.min(this.testText.length(), size));
        binding.playTitle.setText(testText);

        binding.circleProgress.setVisibility(size == height ? View.VISIBLE : View.GONE);
        binding.circleProgress.setIndeterminate(true);
        binding.lineProgress.setVisibility(size == height ? View.GONE : View.VISIBLE);
        binding.lineProgress.setIndeterminate(true);

        binding.circleProgress.setIndicatorSize((int) DisplayUtil.dp2px(requireContext(), 20 + 8 * Math.min(size, height)));
        DisplayUtil.setViewWidth(binding.lineProgress, (int) DisplayUtil.dp2px(requireContext(), 8 + 8 * size));

        DisplayUtil.setViewWidth(binding.playButton, (int) DisplayUtil.dp2px(requireContext(), 20 + 8 * size));
        DisplayUtil.setViewHeight(binding.playButton, (int) DisplayUtil.dp2px(requireContext(), 20 + 8 * height));
    }

    private void refreshCloseView() {
        int padding = SettingSaver.getInstance().getManualPlayViewPadding();
        int size = SettingSaver.getInstance().getManualPlayViewCloseSize();

        DisplayUtil.setViewMargin(binding.dragSpace, padding * UNIT_PIXEL, this.padding, padding * UNIT_PIXEL, this.padding);
        DisplayUtil.setViewWidth(binding.dragButton, (int) DisplayUtil.dp2px(requireContext(), 8 + 8 * size));
    }

    private void refreshSingleView() {
        int size = SettingSaver.getInstance().getManualPlayViewSingleSize();

        binding.singleTitle.setText(testText.substring(0, 1));

        int offset = 20 + 8 * size;
        DisplayUtil.setViewWidth(binding.singleButtonCard, (int) DisplayUtil.dp2px(requireContext(), offset));
        DisplayUtil.setViewHeight(binding.singleButtonCard, (int) DisplayUtil.dp2px(requireContext(), offset));

        binding.singleProgress.setIndicatorSize((int) DisplayUtil.dp2px(requireContext(), offset));
        binding.singleProgress.setIndeterminate(true);
    }
}
