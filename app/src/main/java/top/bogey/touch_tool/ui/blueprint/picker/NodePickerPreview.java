package top.bogey.touch_tool.ui.blueprint.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.databinding.FloatPickerNodePreviewBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.custom.MarkTargetFloatView;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.StringResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class NodePickerPreview extends BasePicker<String> {
    private final FloatPickerNodePreviewBinding binding;
    private final PinNodePathString nodePath;
    private boolean test;

    @SuppressLint("DefaultLocale")
    public NodePickerPreview(@NonNull Context context, StringResultCallback callback, String path) {
        super(context, callback);
        binding = FloatPickerNodePreviewBinding.inflate(LayoutInflater.from(context), this, true);
        nodePath = new PinNodePathString(path);

        binding.switchButton.setVisibility(VISIBLE);
        binding.switchButton.setOnClickListener(v -> {
            test = !test;
            binding.title.setText(test ? R.string.picker_test_title : R.string.picker_node_title);
            binding.pathText.setVisibility(test ? GONE : VISIBLE);
            binding.testBox.setVisibility(test ? VISIBLE : GONE);
        });

        binding.pathText.setText(path);

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            callback.onResult(nodePath.getValue());
            dismiss();
        });

        binding.pickerButton.setOnClickListener(v -> new NodePicker(context, result -> {
            nodePath.setValue(result);
            binding.pathText.setText(nodePath.getValue());
        }, nodePath.getValue()).show());

        binding.matchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                FloatWindow.hide(tag);
                postDelayed(() -> service.tryGetScreenShot(result -> post(() -> {
                    FloatWindow.show(tag);
                    if (result != null) {
                        NodeInfo nodeInfo = nodePath.findNode(NodeInfo.getWindows(), true);
                        if (nodeInfo == null) return;
                        Rect rect = nodeInfo.area;
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
                })), 100);
            }
        });

        binding.touchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                service.tryGetScreenShot(result -> post(() -> {
                    if (result != null) {
                        NodeInfo nodeInfo = nodePath.findNode(NodeInfo.getWindows(), true);
                        if (nodeInfo == null) return;
                        MarkTargetFloatView.showTargetArea(nodeInfo.area);
                        nodeInfo.node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }));
            }
        });
    }
}
