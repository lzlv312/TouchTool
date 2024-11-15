package top.bogey.touch_tool.ui.blueprint.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.databinding.FloatPickerNodeBinding;
import top.bogey.touch_tool.ui.setting.SettingSaver;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class NodePicker extends FullScreenPicker<NodeInfo> implements NodePickerTreeAdapter.SelectNode {
    private final FloatPickerNodeBinding binding;
    private final NodePickerTreeAdapter adapter;

    private final List<NodeInfo> roots = new ArrayList<>();

    private final Paint gridPaint;
    private final Paint markPaint;

    private NodeInfo nodeInfo;

    public NodePicker(@NonNull Context context, ResultCallback<NodeInfo> callback, NodeInfo node) {
        super(context, callback);
        editable = true;
        nodeInfo = node;
        binding = FloatPickerNodeBinding.inflate(LayoutInflater.from(context), this, true);

        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
            NodeInfo root = new NodeInfo(window);
            roots.add(root);
        }

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStyle(Paint.Style.STROKE);

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        adapter = new NodePickerTreeAdapter(new TreeNodeManager(), this, roots);
        binding.widgetRecyclerView.setAdapter(adapter);

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) callback.onResult(nodeInfo);
            dismiss();
        });

        binding.detailButton.setOnClickListener(v -> {
            BottomSheetBehavior<?> sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                adapter.searchNodes(s.toString());
            }
        });

        binding.typeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int type = group.indexOfChild(view);
                SettingSaver.getInstance().setSelectNodeType(type);
                postInvalidate();
            }
        });
        binding.typeGroup.check(binding.typeGroup.getChildAt(SettingSaver.getInstance().getSelectNodeType()).getId());
    }

    @Override
    protected void realShow() {
        PinNodePathString nodePathString = new PinNodePathString();
        nodePathString.setValue(nodeInfo);
        nodeInfo = nodePathString.findNode(roots);
        selectNode(nodeInfo);
        adapter.setSelectedNode(nodeInfo);
    }

    @Override
    public void selectNode(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;

        binding.markBox.setVisibility(GONE);
        binding.idTitle.setVisibility(GONE);

        if (nodeInfo != null) {
            binding.markBox.setVisibility(VISIBLE);
            binding.idTitle.setVisibility(VISIBLE);

            binding.idTitle.setText(nodeInfo.id);

            Rect area = new Rect(nodeInfo.area);
            area.offset(-location[0], -location[1]);
            DisplayUtil.setViewWidth(binding.markBox, area.width());
            DisplayUtil.setViewHeight(binding.markBox, area.height());
            binding.markBox.setX(area.left);
            binding.markBox.setY(area.top);

            float x = area.left + (area.width() - binding.buttonBox.getWidth()) / 2f;
            x = Math.max(0, Math.min(getWidth() - binding.buttonBox.getWidth(), x));
            binding.buttonBox.setX(x);

            float offset = DisplayUtil.dp2px(getContext(), 8);
            if (getHeight() < area.height() + binding.buttonBox.getHeight() + offset) {
                // 内部
                binding.buttonBox.setY(area.bottom - binding.buttonBox.getHeight() - offset);
            } else if (getHeight() < area.bottom + binding.buttonBox.getHeight() + offset) {
                // 外部 顶上
                binding.buttonBox.setY(area.top - binding.buttonBox.getHeight() - offset);
            } else {
                // 外部 底下
                binding.buttonBox.setY(area.bottom + offset);
            }
        } else {
            binding.buttonBox.setX((getWidth() - binding.buttonBox.getWidth()) / 2f);
            binding.buttonBox.setY(getHeight() - DisplayUtil.dp2px(getContext(), 64) - binding.buttonBox.getHeight());
        }
        invalidate();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        canvas.saveLayer(getLeft(), getTop(), getRight(), getBottom(), gridPaint);
        for (int i = roots.size() - 1; i >= 0; i--) {
            canvas.save();
            NodeInfo root = roots.get(i);
            Rect area = new Rect(root.area);
            area.offset(-location[0], -location[1]);
            if (area.width() != getWidth() || area.height() != getHeight()) {
                canvas.drawRect(area, markPaint);
            }
            drawNode(canvas, root);
            canvas.restore();
        }
        canvas.restore();

        if (nodeInfo != null) {
            canvas.save();
            Rect area = new Rect(nodeInfo.area);
            area.offset(-location[0], -location[1]);
            canvas.drawRect(area, markPaint);
            canvas.restore();

            drawChild(canvas, binding.markBox, getDrawingTime());
            drawChild(canvas, binding.idTitle, getDrawingTime());
        }

        drawChild(canvas, binding.buttonBox, getDrawingTime());
        drawChild(canvas, binding.bottomSheet, getDrawingTime());
    }

    private void drawNode(Canvas canvas, NodeInfo node) {
        if (node == null) return;
        if (!node.visible) return;

        Rect area = new Rect(node.area);
        area.offset(-location[0], -location[1]);

        int type = SettingSaver.getInstance().getSelectNodeType();
        if (type != 0 && !node.usable) {
            gridPaint.setColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSecondary));
            gridPaint.setStrokeWidth(1);
            canvas.drawRect(area, gridPaint);
        }

        for (NodeInfo child : node.children) {
            drawNode(canvas, child);
        }

        if (node.usable) {
            gridPaint.setColor(DisplayUtil.getAttrColor(getContext(), R.attr.colorPrimaryLight));
            gridPaint.setStrokeWidth(3);
            area.offset(2, 2);
            area.right -= 4;
            area.bottom -= 4;
            canvas.drawRect(area, gridPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int type = SettingSaver.getInstance().getSelectNodeType();
            NodeInfo node = null;
            switch (type) {
                case 0 -> node = findNodeByTop(Math.round(x), Math.round(y), true);
                case 1 -> node = findNodeByTop(Math.round(x), Math.round(y), false);
                case 2 -> node = findNodeByDepth(Math.round(x), Math.round(y), false);
            }
            if (node != null) {
                selectNode(node);
                adapter.setSelectedNode(node);
            }
        }
        return true;
    }

    private NodeInfo findNodeByTop(int x, int y, boolean justUsable) {
        for (NodeInfo root : roots) {
            List<NodeInfo> children = root.findChildrenInArea(new Rect(x, y, x, y));
            for (int i = children.size() - 1; i >= 0; i--) {
                NodeInfo child = children.get(i);
                if (!justUsable || child.usable) return child;
            }
        }
        return null;
    }

    private NodeInfo findNodeByDepth(int x, int y, boolean justUsable) {
        Map<NodeInfo, Integer> map = new HashMap<>();
        for (int i = 0; i < roots.size(); i++) {
            NodeInfo root = roots.get(i);
            root.findChildrenInArea(map, new Rect(x, y, x, y), (roots.size() - i) * Byte.MAX_VALUE);
        }

        int depth = 0;
        NodeInfo node = null;
        for (Map.Entry<NodeInfo, Integer> entry : map.entrySet()) {
            if (depth < entry.getValue() && (!justUsable || entry.getKey().usable)) {
                depth = entry.getValue();
                node = entry.getKey();
            }
        }
        return node;
    }
}
