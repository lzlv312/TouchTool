package top.bogey.touch_tool.ui.setting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.WidgetSettingNormalBinding;

public class SettingWidgetNormal extends FrameLayout {
    private final WidgetSettingNormalBinding binding;
    private final MaterialButton button;

    public SettingWidgetNormal(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetSettingNormalBinding.inflate(LayoutInflater.from(context), this, true);


        try (TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingWidgetNormal)) {
            int icon = typedArray.getResourceId(R.styleable.SettingWidgetNormal_icon, 0);
            binding.icon.setImageResource(icon);

            String title = typedArray.getString(R.styleable.SettingWidgetNormal_title);
            binding.title.setText(title);

            String description = typedArray.getString(R.styleable.SettingWidgetNormal_description);
            binding.description.setText(description);
            binding.description.setVisibility(description == null || description.isEmpty() ? GONE : VISIBLE);

            int buttonType = typedArray.getInt(R.styleable.SettingWidgetNormal_buttonVisible, 0);
            button = switch (buttonType) {
                case 0 -> {
                    binding.button.setVisibility(VISIBLE);
                    yield binding.button;
                }
                case 1 -> {
                    binding.button2.setVisibility(VISIBLE);
                    yield binding.button2;
                }
                case 2 -> {
                    binding.button3.setVisibility(VISIBLE);
                    yield binding.button3;
                }
                default -> null;
            };

            String buttonText = typedArray.getString(R.styleable.SettingWidgetNormal_buttonText);
            setButtonText(buttonText);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        binding.getRoot().setOnClickListener(listener);
    }

    public void setOnButtonClickListener(OnClickListener listener) {
        if (button == null) {
            setOnClickListener(listener);
            return;
        }
        button.setOnClickListener(listener);
    }

    public void setButtonText(String text) {
        if (button == null) return;
        button.setText(text);
    }

    public void setDescription(String description) {
        binding.description.setText(description);
    }
}
