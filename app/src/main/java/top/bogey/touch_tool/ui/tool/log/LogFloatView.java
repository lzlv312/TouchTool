package top.bogey.touch_tool.ui.tool.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.log.ActionLog;
import top.bogey.touch_tool.bean.save.log.LogInfo;
import top.bogey.touch_tool.bean.save.log.LogSave;
import top.bogey.touch_tool.bean.save.log.LogSaveListener;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatLogBinding;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class LogFloatView extends FrameLayout implements FloatInterface, LogSaveListener {
    private final FloatLogBinding binding;
    private final LogFloatViewAdapter adapter;
    private final Task task;
    private final String tag = LogFloatView.class.getName();

    private final int minWidth, minHeight;
    private final int maxWidth, maxHeight;
    private int originWidth = 0, originHeight = 0;
    private int width = 0, height = 0;

    private float lastX = 0, lastY = 0;
    private boolean canDrag = false;
    private boolean dragging = false;
    private boolean expanded = true;

    @SuppressLint("ClickableViewAccessibility")
    public LogFloatView(@NonNull Context context, Task task) {
        super(context);
        this.task = task;

        minWidth = (int) DisplayUtil.dp2px(context, 168);
        minHeight = (int) DisplayUtil.dp2px(context, 128);
        Point size = DisplayUtil.getScreenSize(context);
        maxWidth = size.x;
        maxHeight = (int) (size.y * 0.8f);

        binding = FloatLogBinding.inflate(LayoutInflater.from(context), this, true);
        adapter = new LogFloatViewAdapter();

        binding.title.setText(task.getTitle());

        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.exportButton.setOnClickListener(v -> {
            StringBuilder builder = new StringBuilder();
            for (LogInfo log : adapter.getLogs()) {
                builder.append("[").append(AppUtil.formatDateTime(context, log.getTime(), false, false)).append("] ");
                if (log.getLogObject() instanceof ActionLog actionLog) {
                    builder.append("[").append(actionLog.isExecute() ? "->" : "<-").append("] ");
                }
                builder.append(log.getLog()).append("\n");
            }
            AppUtil.shareText(context, builder.toString());
        });

        binding.expandButton.setOnClickListener(v -> {
            expanded = !expanded;
            if (expanded) {
                binding.contentBox.setVisibility(VISIBLE);
                int margin = (int) DisplayUtil.dp2px(context, 8);
                DisplayUtil.setViewMargin(binding.expandButton, margin, 0, 0, 0);
                ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
                params.width = width;
                params.height = height;
                binding.getRoot().setLayoutParams(params);
                binding.expandButton.setIconResource(R.drawable.icon_zoom_in_map);
            } else {
                binding.contentBox.setVisibility(GONE);
                DisplayUtil.setViewMargin(binding.expandButton, 0, 0, 0, 0);
                ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
                width = params.width;
                height = params.height;
                params.width = binding.expandButton.getWidth();
                params.height = binding.expandButton.getHeight();
                binding.getRoot().setLayoutParams(params);
                binding.expandButton.setIconResource(R.drawable.icon_zoom_out_map);

            }
            FloatWindow.updateLayoutParam(tag);
        });

        binding.recyclerView.setAdapter(adapter);

        adapter.setLogSave(Saver.getInstance().getLogSave(task.getId()));
        binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int index = adapter.searchLog(s.toString(), null);
                if (index > 0) binding.recyclerView.scrollToPosition(index);
                else binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                binding.preButton.setVisibility(index > 0 ? VISIBLE : GONE);
                binding.nextButton.setVisibility(index > 0 ? VISIBLE : GONE);
            }
        });

        binding.preButton.setOnClickListener(v -> searchLog(false));

        binding.nextButton.setOnClickListener(v -> searchLog(true));

        binding.clearButton.setOnClickListener(v -> {
            Saver.getInstance().clearLog(task.getId());
            adapter.setLogSave(Saver.getInstance().getLogSave(task.getId()));
        });
    }

    private void searchLog(boolean isNext) {
        String s = "";
        Editable text = binding.searchEdit.getText();
        if (text != null) s = text.toString();
        int index = adapter.searchLog(s, isNext);
        if (index > 0) binding.recyclerView.scrollToPosition(index);
        else binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (originWidth == 0 || originHeight == 0) {
            originWidth = binding.getRoot().getWidth();
            originHeight = binding.getRoot().getHeight();
            originWidth = Math.max(1, originWidth);
            originHeight = Math.max(1, originHeight);

            width = binding.expandButton.getWidth();
            height = binding.expandButton.getHeight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            canDrag = false;
            int[] location = new int[2];
            binding.recyclerView.getLocationOnScreen(location);
            if (new RectF(location[0], location[1], location[0] + binding.recyclerView.getWidth(), location[1] + binding.recyclerView.getHeight()).contains(x, y)) {
                FloatWindow.setDragAble(tag, false);
                return super.onInterceptTouchEvent(event);
            }

            location = new int[2];
            binding.dragImage.getLocationOnScreen(location);
            if (new RectF(location[0], location[1], location[0] + binding.dragImage.getWidth(), location[1] + binding.dragImage.getHeight()).contains(x, y)) {
                FloatWindow.setDragAble(tag, false);
                lastX = 0;
                lastY = 0;
                canDrag = true;
                return super.onInterceptTouchEvent(event);
            }
            FloatWindow.setDragAble(tag, true);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!canDrag) return false;
            if (lastX == 0 && lastY == 0) {
                lastX = x;
                lastY = y;
                return false;
            }
            dragging = true;
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE -> {
                float dx = x - lastX;
                float dy = y - lastY;
                ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
                if (params.width <= 0) params.width = originWidth;
                if (params.height <= 0) params.height = originHeight;
                params.width += (int) dx;
                params.height += (int) dy;
                params.width = Math.min(maxWidth, Math.max(minWidth, params.width));
                params.height = Math.min(maxHeight, Math.max(minHeight, params.height));
                binding.getRoot().setLayoutParams(params);
                FloatWindow.updateLayoutParam(tag);
                lastX = x;
                lastY = y;
                return true;
            }

            case MotionEvent.ACTION_UP -> {
                if (dragging) {
                    dragging = false;
                    FloatWindow.setDragAble(tag, true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void show() {
        Point size = DisplayUtil.getScreenSize(getContext());
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setSpecial(true)
                .setLocation(EAnchor.BOTTOM_CENTER, 0, -size.y / 4)
                .setExistEditText(true)
                .show();
        Saver.getInstance().addListener(this);
    }

    @Override
    public void dismiss() {
        Saver.getInstance().removeListener(this);
        FloatWindow.dismiss(tag);
    }

    @Override
    public void onNewLog(LogSave logSave, LogInfo log) {
        post(() -> {
            if (logSave.getKey().equals(task.getId())) {
                adapter.addLog(logSave, log);
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }
}
