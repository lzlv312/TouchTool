package top.bogey.touch_tool.ui.blueprint.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.FloatPickerAreaPreviewBinding;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class AreaPickerPreview extends BasePicker<Rect> {
    private final FloatPickerAreaPreviewBinding binding;
    private boolean test = false;

    public AreaPickerPreview(@NonNull Context context, ResultCallback<Rect> callback, Rect rect, boolean forOcr) {
        super(context, callback);

        dragAble = true;

        Rect area = new Rect(rect);
        binding = FloatPickerAreaPreviewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.leftEdit.setText(String.valueOf(area.left));
        binding.topEdit.setText(String.valueOf(area.top));
        binding.rightEdit.setText(String.valueOf(area.right));
        binding.bottomEdit.setText(String.valueOf(area.bottom));

        binding.leftEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                area.left = toInt(s);
            }
        });
        binding.topEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                area.top = toInt(s);
            }
        });
        binding.rightEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                area.right = toInt(s);
            }
        });
        binding.bottomEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                area.bottom = toInt(s);
            }
        });

        if (forOcr) {
            binding.switchButton.setVisibility(VISIBLE);
            binding.switchButton.setOnClickListener(v -> {
                test = !test;
                binding.title.setText(test ? R.string.picker_test_title : R.string.picker_area_title);
                binding.buttonBox.setVisibility(test ? GONE : VISIBLE);
                binding.testBox.setVisibility(test ? VISIBLE : GONE);
            });

            binding.testButton.setOnClickListener(v -> {

            });
        }

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            callback.onResult(area);
            dismiss();
        });

        binding.pickerButton.setOnClickListener(v -> new AreaPicker(context, result -> {
            binding.leftEdit.setText(String.valueOf(result.left));
            binding.topEdit.setText(String.valueOf(result.top));
            binding.rightEdit.setText(String.valueOf(result.right));
            binding.bottomEdit.setText(String.valueOf(result.bottom));
        }, area).show());
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragAble(dragAble)
                .setCallback(floatCallback)
                .setExistEditText(true)
                .show();
    }

    private int toInt(Editable s) {
        if (s == null || s.length() == 0) return 0;
        try {
            return Integer.parseInt(s.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
