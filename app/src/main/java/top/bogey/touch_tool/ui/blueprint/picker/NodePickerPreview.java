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

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.databinding.FloatPickerNodePreviewBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;

@SuppressLint("ViewConstructor")
public class NodePickerPreview extends BasePicker<NodeInfo> {
    private final FloatPickerNodePreviewBinding binding;
    private boolean test;
    private NodeInfo nodeInfo;

    @SuppressLint("DefaultLocale")
    public NodePickerPreview(@NonNull Context context, ResultCallback<NodeInfo> callback, NodeInfo node) {
        super(context, callback);
        binding = FloatPickerNodePreviewBinding.inflate(LayoutInflater.from(context), this, true);
        nodeInfo = node;

        binding.switchButton.setVisibility(VISIBLE);
        binding.switchButton.setOnClickListener(v -> {
            test = !test;
            binding.title.setText(test ? R.string.picker_test_title : R.string.picker_node_title);
            binding.buttonBox.setVisibility(test ? GONE : VISIBLE);
            binding.testBox.setVisibility(test ? VISIBLE : GONE);
        });

        refreshUI(nodeInfo);

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            callback.onResult(nodeInfo);
            dismiss();
        });

        binding.pickerButton.setOnClickListener(v -> new NodePicker(context, result -> {
            refreshUI(result);
            nodeInfo = result;
        }, nodeInfo).show());

        binding.matchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                service.tryGetScreenShot(result -> post(() -> {
                    if (result != null) {
                        List<NodeInfo> roots = new ArrayList<>();
                        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
                            NodeInfo root = new NodeInfo(window);
                            roots.add(root);
                        }

                        PinNodePathString pinNodePathString = new PinNodePathString();
                        pinNodePathString.setValue(nodeInfo);
                        NodeInfo info = pinNodePathString.findNode(roots);
                        if (info == null) return;

                        Rect rect = info.area;
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
                }));
            }
        });

        binding.touchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isEnabled()) {
                service.tryGetScreenShot(result -> post(() -> {
                    if (result != null) {
                        List<NodeInfo> roots = new ArrayList<>();
                        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
                            NodeInfo root = new NodeInfo(window);
                            roots.add(root);
                        }

                        PinNodePathString pinNodePathString = new PinNodePathString();
                        pinNodePathString.setValue(nodeInfo);
                        NodeInfo info = pinNodePathString.findNode(roots);
                        if (info == null || info.nodeInfo == null) return;
                        info.nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }));
            }
        });
    }

    private void refreshUI(NodeInfo nodeInfo) {
        if (nodeInfo == null) {
            binding.nodeIdText.setText(null);
            binding.nodeTextText.setText(null);
            binding.usableTip.setVisibility(GONE);
            binding.visibleTip.setVisibility(GONE);
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(nodeInfo.clazz);
        if (nodeInfo.id != null && !nodeInfo.id.isEmpty()) builder.append("[id=").append(nodeInfo.id).append("]");
        if (nodeInfo.index > 1) builder.append("[").append(nodeInfo.index).append("]");
        binding.nodeIdText.setText(builder.toString());

        binding.nodeTextText.setText(nodeInfo.text);

        binding.usableTip.setVisibility(nodeInfo.usable ? VISIBLE : GONE);
        binding.visibleTip.setVisibility(nodeInfo.visible ? VISIBLE : GONE);
    }
}
