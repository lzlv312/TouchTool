package top.bogey.touch_tool.ui.setting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.WidgetSettingNormalBinding;

public class SettingWidgetNormal extends FrameLayout {
    private final WidgetSettingNormalBinding binding;

    public SettingWidgetNormal(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetSettingNormalBinding.inflate(LayoutInflater.from(context), this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingWidgetNormal);
        try {
            int icon = typedArray.getResourceId(R.styleable.SettingWidgetNormal_icon, 0);
            binding.icon.setImageResource(icon);

            String title = typedArray.getString(R.styleable.SettingWidgetNormal_title);
            binding.title.setText(title);

            String description = typedArray.getString(R.styleable.SettingWidgetNormal_description);
            binding.description.setText(description);
            binding.description.setVisibility(description == null || description.isEmpty() ? GONE : VISIBLE);

        } finally {
            typedArray.recycle();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        binding.getRoot().setOnClickListener(listener);
    }

    public void setDescription(String description) {
        binding.description.setText(description);
    }
}
