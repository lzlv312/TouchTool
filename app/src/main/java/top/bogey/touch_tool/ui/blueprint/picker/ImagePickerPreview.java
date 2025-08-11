package top.bogey.touch_tool.ui.blueprint.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.FloatPickerImagePreviewBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.custom.TouchPathFloatView;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class ImagePickerPreview extends BasePicker<Bitmap> {
    private final FloatPickerImagePreviewBinding binding;
    private Bitmap bitmap;
    private boolean test;

    public ImagePickerPreview(@NonNull Context context, ResultCallback<Bitmap> callback, Bitmap image) {
        super(context, callback);
        binding = FloatPickerImagePreviewBinding.inflate(LayoutInflater.from(context), this, true);
        bitmap = image;
        binding.current.setImageBitmap(image);

        binding.switchButton.setVisibility(VISIBLE);
        binding.switchButton.setOnClickListener(v -> {
            test = !test;
            binding.title.setText(test ? R.string.picker_test_title : R.string.picker_image_title);
            binding.current.setVisibility(test ? GONE : VISIBLE);
            binding.testBox.setVisibility(test ? VISIBLE : GONE);
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            callback.onResult(bitmap);
            dismiss();
        });

        binding.pickerButton.setOnClickListener(v -> new ImagePicker(context, result -> {
            bitmap = result;
            binding.current.setImageBitmap(bitmap);
        }, bitmap).show());

        binding.timeSlider.setLabelFormatter(value -> getContext().getString(R.string.picker_image_offset, (int) value));
        binding.matchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                FloatWindow.hide(tag);
                postDelayed(() -> service.tryGetScreenShot(result -> post(() -> {
                    FloatWindow.show(tag);
                    if (result != null) {
                        int similar = (int) binding.timeSlider.getValue();
                        Rect rect = DisplayUtil.matchTemplate(result, bitmap, null, similar);
                        if (rect == null || rect.isEmpty()) binding.matchedImage.setImageDrawable(null);
                        else {
                            int px = (int) DisplayUtil.dp2px(getContext(), 16);
                            Rect area = DisplayUtil.safeClipBitmapArea(result, rect.left - px, rect.top - px, rect.width() + px * 2, rect.height() + px * 2);
                            if (area == null) return;
                            Bitmap bitmap = DisplayUtil.safeClipBitmap(result, area.left, area.top, area.width(), area.height());
                            if (bitmap == null) return;
                            Paint paint = new Paint();
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(2);
                            paint.setStyle(Paint.Style.STROKE);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.translate(rect.left - area.left, rect.top - area.top);
                            canvas.drawRect(new Rect(0, 0, rect.width(), rect.height()), paint);
                            binding.matchedImage.setImageBitmap(bitmap);
                        }
                    }
                })), 100);
            }
        });

        binding.touchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                FloatWindow.hide(tag);
                postDelayed(() -> service.tryGetScreenShot(result -> post(() -> {
                    FloatWindow.show(tag);
                    if (result != null) {
                        int similar = (int) binding.timeSlider.getValue();
                        Rect rect = DisplayUtil.matchTemplate(result, bitmap, null, similar);
                        if (rect == null || rect.isEmpty()) return;
                        int x = rect.left + rect.width() / 2;
                        int y = rect.top + rect.height() / 2;
                        service.runGesture(x, y, 50, null);

                        TouchPathFloatView.showGesture(x, y);
                    }
                })), 100);
            }
        });
    }
}
