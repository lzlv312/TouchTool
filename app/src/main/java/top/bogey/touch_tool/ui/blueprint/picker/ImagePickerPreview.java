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

import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.FloatPickerImagePreviewBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;

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
            binding.buttonBox.setVisibility(test ? GONE : VISIBLE);
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
                service.tryGetScreenShot(result -> {
                    if (result != null) {
                        post(() -> {
                            int similar = (int) binding.timeSlider.getValue();
                            List<Rect> rectList = DisplayUtil.matchTemplate(result, bitmap, null, similar);
                            if (rectList == null || rectList.isEmpty()) binding.matchedImage.setImageDrawable(null);
                            else {
                                Rect rect = rectList.get(0);
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
                        });
                    }
                });
            }
        });
    }
}
