package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
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
import top.bogey.touch_tool.bean.save.LogInfo;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatPlayItemBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.ITaskListener;
import top.bogey.touch_tool.service.TaskRunnable;

@SuppressLint("ViewConstructor")
public class PlayFloatItemView extends FrameLayout implements ITaskListener {
    protected final FloatPlayItemBinding binding;

    protected Task task;
    protected StartAction startAction;

    protected boolean playing = false;
    protected TaskRunnable runnable;
    protected boolean remove = false;

    public PlayFloatItemView(@NonNull Context context, Task task, StartAction action) {
        super(context);
        binding = FloatPlayItemBinding.inflate(LayoutInflater.from(context), this, true);
        this.task = task;
        this.startAction = action;

        binding.title.setText(getTitle(task, action));

        binding.playButton.setOnClickListener(v -> {
            if (playing) {
                pause();
            } else {
                if (!resume()) {
                    MainAccessibilityService service = MainApplication.getInstance().getService();
                    if (service == null || !service.isEnabled()) return;
                    runnable = service.runTask(task, startAction, this);
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

    public void tryRemoveFromParent() {
        if (playing) remove = true;
        else {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    private void pause() {
        if (runnable == null) return;
        runnable.pause();
        playing = false;

        binding.playButton.setIndeterminate(false);
        binding.icon.setIconResource(R.drawable.icon_play);
    }

    private void setPlaying() {
        playing = true;

        binding.playButton.setIndeterminate(true);
        binding.icon.setIconResource(R.drawable.icon_pause);
    }

    private boolean resume() {
        if (runnable == null) return false;
        runnable.resume();
        setPlaying();
        return true;
    }

    protected void stop() {
        if (runnable == null) return;
        runnable.stop();
        runnable = null;
        playing = false;

        binding.playButton.setIndeterminate(false);
        binding.icon.setIconResource(0);
        binding.title.setText(getTitle(task, startAction));
    }

    private String getTitle(Task task, StartAction action) {
        String description = action.getDescription();
        if (description == null || description.isEmpty()) description = task.getTitle();
        if (description == null || description.isEmpty()) return "?";
        Pattern pattern = Pattern.compile("[\"|“](.*)[\"|”]");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return description.substring(0, 1);
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        this.runnable = runnable;
        post(this::setPlaying);
    }

    @Override
    public void onExecute(TaskRunnable runnable, Action action, int progress) {
        post(() -> {
            if (playing) binding.title.setText(String.valueOf(progress));
        });
    }

    @Override
    public void onCalculate(TaskRunnable runnable, Action action) {
    }

    @Override
    public void onFinish(TaskRunnable runnable) {
        post(() -> {
            stop();
            if (remove) ((ViewGroup) getParent()).removeView(this);
        });
    }
}
