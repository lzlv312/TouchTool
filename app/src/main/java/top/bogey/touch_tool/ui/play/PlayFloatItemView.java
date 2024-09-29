package top.bogey.touch_tool.ui.play;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_execute.PinIconExecute;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.TaskListener;
import top.bogey.touch_tool.bean.task.TaskRunnable;
import top.bogey.touch_tool.databinding.FloatPlayItemBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.DisplayUtil;

public class PlayFloatItemView extends FrameLayout implements TaskListener {
    private final FloatPlayItemBinding binding;

    private Task task;
    private StartAction startAction;

    private boolean playing = false;
    private TaskRunnable runnable;
    private boolean remove = false;

    public PlayFloatItemView(@NonNull Context context) {
        super(context);
        binding = FloatPlayItemBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public PlayFloatItemView(@NonNull Context context, Task task, StartAction action) {
        this(context);
        this.task = task;
        this.startAction = action;

        binding.icon.setImageBitmap(getIcon(task, action));

        binding.playButton.setOnClickListener(v -> {
            if (playing) {
                pause();
            } else {
                if (!resume()) {
                    if (task.checkCapturePermission()) {
                        play(task, action);
                    } else {
                        MainAccessibilityService service = MainApplication.getInstance().getService();
                        if (service != null && service.isEnabled()) {
                            if (service.isCaptureEnabled()) {
                                play(task, action);
                            } else {
                                service.startCapture(null);
                            }
                        }
                    }
                }
            }
        });

        binding.playButton.setOnLongClickListener(v -> {
            stop();
            return true;
        });
    }

    public boolean check(Task task, StartAction startAction) {
        return this.task.getId().equals(task.getId()) && this.startAction.getId().equals(startAction.getId());
    }

    public void tryRemoveFromParent(boolean remove) {
        if (playing) this.remove = remove;
        else {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    private void play(Task task, StartAction startAction) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isEnabled()) return;
        runnable = service.runTask(task, startAction, this);
    }

    private void pause() {
        if (runnable == null) return;
        runnable.pause();
        playing = false;

        binding.playButton.setIndeterminate(false);
        binding.icon.setImageResource(R.drawable.icon_stop);
        binding.icon.setAlpha(1f);
        binding.percent.setVisibility(GONE);
    }

    private boolean resume() {
        if (runnable == null) return false;
        runnable.resume();
        playing = true;

        binding.playButton.setIndeterminate(true);
        binding.icon.setImageBitmap(getIcon(runnable.getTask(), runnable.getStartAction()));
        binding.icon.setAlpha(.2f);
        binding.percent.setVisibility(VISIBLE);
        return true;
    }

    private void stop() {
        if (runnable == null) return;
        runnable.stop();
        runnable = null;

        binding.playButton.setIndeterminate(false);
        binding.icon.setAlpha(1f);
        binding.percent.setVisibility(GONE);
    }

    private Bitmap getIcon(Task task, StartAction action) {
        Pin executePin = action.getExecutePin();
        if (executePin.getValue() instanceof PinIconExecute iconExecute) {
            return iconExecute.getImage();
        }

        String description = action.getDescription();
        if (description == null || description.isEmpty()) description = task.getTitle();
        if (description == null || description.isEmpty())
            return DisplayUtil.textToBitmap(getContext(), "?", 13);
        Pattern pattern = Pattern.compile("[\"|“](.*)[\"|”]");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return DisplayUtil.textToBitmap(getContext(), matcher.group(1), 13);
        }
        return DisplayUtil.textToBitmap(getContext(), description.substring(0, 1), 13);
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        playing = true;
        this.runnable = runnable;
    }

    @Override
    public void onExecute(TaskRunnable runnable, Action action, int progress) {
        post(() -> binding.percent.setText(String.valueOf(progress)));
    }

    @Override
    public void onCalculate(TaskRunnable runnable, Action action) {
    }

    @Override
    public void onFinish(TaskRunnable runnable) {
        playing = false;
        stop();
        if (remove) post(() -> ((ViewGroup) getParent()).removeView(this));
    }
}
