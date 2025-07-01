package top.bogey.touch_tool.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatPlayItemBinding;
import top.bogey.touch_tool.service.ITaskListener;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class PlayFloatItemView extends FrameLayout implements ITaskListener {
    protected final FloatPlayItemBinding binding;
    protected int size;

    protected Task task;
    protected StartAction startAction;

    protected boolean playing = false;
    protected TaskRunnable runnable;
    protected boolean remove = false;

    public PlayFloatItemView(@NonNull Context context, Task task, StartAction action) {
        super(context);
        binding = FloatPlayItemBinding.inflate(LayoutInflater.from(context), this, true);
        size = SettingSaver.getInstance().getManualPlayViewExpandSize();
        int pauseType = SettingSaver.getInstance().getManualPlayPauseType();

        this.task = task;
        this.startAction = action;

        binding.title.setText(getTitle(task, action));

        binding.getRoot().setOnClickListener(v -> {
            if (playing) {
                if (pauseType == 0) pause();
                else stop();
            } else {
                if (!resume()) {
                    MainAccessibilityService service = MainApplication.getInstance().getService();
                    if (service == null || !service.isEnabled()) return;
                    runnable = service.runTask(task, startAction, this);
                }
            }
        });

        binding.getRoot().setOnLongClickListener(v -> {
            if (pauseType == 0) stop();
            else pause();
            return true;
        });

        binding.circleProgress.setVisibility(View.GONE);
        binding.lineProgress.setVisibility(View.GONE);
        DisplayUtil.setViewWidth(binding.lineProgress, (int) DisplayUtil.dp2px(context, 8 + 8 * size));
        DisplayUtil.setViewWidth(binding.cardLayout, (int) DisplayUtil.dp2px(context, 20 + 8 * size));
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

        binding.circleProgress.setIndeterminate(false);
        binding.lineProgress.setIndeterminate(false);
        binding.circleProgress.setVisibility(View.GONE);
        binding.lineProgress.setVisibility(View.GONE);
        binding.icon.setIconResource(R.drawable.icon_play);
    }

    private void setPlaying() {
        playing = true;

        binding.circleProgress.setIndeterminate(true);
        binding.lineProgress.setIndeterminate(true);
        binding.circleProgress.setVisibility(size == 1 ? View.VISIBLE : View.GONE);
        binding.lineProgress.setVisibility(size == 1 ? View.GONE : View.VISIBLE);
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

        binding.circleProgress.setIndeterminate(false);
        binding.lineProgress.setIndeterminate(false);
        binding.circleProgress.setVisibility(View.GONE);
        binding.lineProgress.setVisibility(View.GONE);
        binding.icon.setIconResource(0);
        binding.title.setText(getTitle(task, startAction));
    }

    private String getTitle(Task task, StartAction action) {
        String description = action.getDescription();
        if (description == null || description.isEmpty()) description = task.getTitle();
        if (description == null || description.isEmpty()) return "?";
        Pattern pattern = Pattern.compile("[\"|“](.+)[\"|”]");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) description = matcher.group(1);
        if (description == null || description.isEmpty()) return "?";
        description = description.substring(0, Math.min(size, description.length()));
        return description;
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
