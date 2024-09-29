package top.bogey.touch_tool.bean.save;

import android.annotation.SuppressLint;

import top.bogey.touch_tool.utils.AppUtil;

public class LogInfo {
    private final long time;
    private final int index;
    private final String content;

    public LogInfo(int index, String content) {
        this.time = System.currentTimeMillis();
        this.index = index;
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public int getIndex() {
        return index;
    }

    public String getContent() {
        return content;
    }

    @SuppressLint("DefaultLocale")
    public String getLog() {
        return String.format("【%d】%s\n%s", index, AppUtil.formatTimeMillisecond(time), content);
    }
}
