package top.bogey.touch_tool.bean.save;

import android.annotation.SuppressLint;

import com.tencent.mmkv.MMKV;

import top.bogey.touch_tool.utils.GsonUtil;

@SuppressLint("DefaultLocale")
public class LogSave {
    private static final String DETAIL_LOG = "DETAIL_LOG";
    private static final String NORMAL_LOG = "NORMAL_LOG";

    private final MMKV mmkv;

    private long time = System.currentTimeMillis();

    public LogSave(String key, String path) {
        mmkv = MMKV.mmkvWithID(key, MMKV.MULTI_PROCESS_MODE, null, path);
    }

    public LogInfo getLog(int index) {
        return GsonUtil.getAsObject(mmkv.decodeString(String.format("%s_%d", NORMAL_LOG, index)), LogInfo.class, null);
    }

    public LogInfo getDetailLog(int index) {
        return GsonUtil.getAsObject(mmkv.decodeString(String.format("%s_%d", DETAIL_LOG, index)), LogInfo.class, null);
    }

    public int getLogCount() {
        return mmkv.decodeInt(NORMAL_LOG, 0);
    }

    public int getDetailLogCount() {
        return mmkv.decodeInt(DETAIL_LOG, 0);
    }

    public void addLog(LogInfo log) {
        int index;
        if (log.getIndex() == -1) {
            index = getLogCount();
            index++;
            mmkv.encode(NORMAL_LOG, index);
            mmkv.encode(String.format("%s_%d", NORMAL_LOG, index), GsonUtil.toJson(log));
        } else {
            index = getDetailLogCount();
            index++;
            mmkv.encode(DETAIL_LOG, index);
            mmkv.encode(String.format("%s_%d", DETAIL_LOG, index), GsonUtil.toJson(log));
        }
        time = System.currentTimeMillis();
    }

    public void clearLog() {
        mmkv.clearAll();
        time = 0;
    }

    public void destroy() {
        MMKV.removeStorage(mmkv.mmapID());
    }

    public boolean recycle() {
        long current = System.currentTimeMillis();
        if (current - time > 5 * 60 * 1000) {
            mmkv.close();
            return true;
        }
        return false;
    }
}
