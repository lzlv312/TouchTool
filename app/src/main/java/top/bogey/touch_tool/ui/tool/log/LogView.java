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
import top.bogey.touch_tool.bean.save.LogInfo;
import top.bogey.touch_tool.bean.save.LogSave;
import top.bogey.touch_tool.bean.save.LogSaveListener;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatLogBinding;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class LogView extends FrameLayout implements FloatInterface, LogSaveListener {
    private final FloatLogBinding binding;
    private final LogViewAdapter adapter;
    private final Task task;
    private final String tag = LogView.class.getName();

    private final int minWidth, minHeight;
    private final int maxWidth, maxHeight;

    private float lastX = 0, lastY = 0;
    private boolean dragging = false;

    @SuppressLint("ClickableViewAccessibility")
    public LogView(@NonNull Context context, Task task) {
        super(context);
        this.task = task;

        minWidth = (int) DisplayUtil.dp2px(context, 168);
        minHeight = (int) DisplayUtil.dp2px(context, 128);
        Point size = DisplayUtil.getScreenSize(context);
        maxWidth = size.x;
        maxHeight = (int) (size.y * 0.8f);

        binding = FloatLogBinding.inflate(LayoutInflater.from(context), this, true);
        adapter = new LogViewAdapter();

        binding.closeButton.setOnClickListener(v -> dismiss());

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
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
                return true;
            }
            FloatWindow.setDragAble(tag, true);
        }
        return super.onInterceptTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                lastX = 0;
                lastY = 0;
                int[] location = new int[2];
                binding.dragImage.getLocationOnScreen(location);
                if (new RectF(location[0], location[1], location[0] + binding.dragImage.getWidth(), location[1] + binding.dragImage.getHeight()).contains(x, y)) {
                    FloatWindow.setDragAble(tag, false);
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE -> {
                if (lastX == 0 && lastY == 0) {
                    lastX = x;
                    lastY = y;
                    return true;
                }
                dragging = true;
                float dx = x - lastX;
                float dy = y - lastY;
                ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
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
                } else {
                    Saver.getInstance().clearLog(task.getId());
                    adapter.setLogSave(Saver.getInstance().getLogSave(task.getId()));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setSpecial(true)
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
                adapter.addLog(log);
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }
}
