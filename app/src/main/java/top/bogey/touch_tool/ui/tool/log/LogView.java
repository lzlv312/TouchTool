package top.bogey.touch_tool.ui.tool.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.save.LogInfo;
import top.bogey.touch_tool.bean.save.LogSave;
import top.bogey.touch_tool.bean.save.LogSaveListener;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatLogBinding;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class LogView extends FrameLayout implements FloatInterface, LogSaveListener {
    private final FloatLogBinding binding;
    private final LogViewAdapter adapter;
    private final Task task;

    @SuppressLint("ClickableViewAccessibility")
    public LogView(@NonNull Context context, Task task) {
        super(context);
        this.task = task;

        binding = FloatLogBinding.inflate(LayoutInflater.from(context), this, true);
        adapter = new LogViewAdapter();

        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.zoomButton.setOnClickListener(v -> {
            Saver.getInstance().clearLog(task.getId());
            adapter.setLogSave(Saver.getInstance().getLogSave(task.getId()));
        });

        binding.recyclerView.setAdapter(adapter);

        adapter.setLogSave(Saver.getInstance().getLogSave(task.getId()));
        binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int[] location = new int[2];
            binding.recyclerView.getLocationOnScreen(location);
            FloatWindow.setDragAble(LogView.class.getName(), !new RectF(location[0], location[1], location[0] + binding.recyclerView.getWidth(), location[1] + binding.recyclerView.getHeight()).contains(x, y));
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(LogView.class.getName())
                .setDragAble(false)
                .setSpecial(true)
                .setExistEditText(true)
                .show();
        Saver.getInstance().addListener(this);
    }

    @Override
    public void dismiss() {
        Saver.getInstance().removeListener(this);
        FloatWindow.dismiss(LogView.class.getName());
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
