package top.bogey.touch_tool.bean.save;

import com.tencent.mmkv.MMKV;

import top.bogey.touch_tool.utils.GsonUtil;
import top.bogey.touch_tool.utils.tree.ITreeNodeData;
import top.bogey.touch_tool.utils.tree.ITreeNodeDataLoader;

public class LogSave implements ITreeNodeDataLoader {
    private final static String COUNT = "count";

    private final MMKV mmkv;
    private final String key;

    private long time = System.currentTimeMillis();

    public LogSave(String key, String path) {
        this.key = key;
        mmkv = MMKV.mmkvWithID(key, MMKV.MULTI_PROCESS_MODE, null, path);
    }

    public LogInfo getLog(int index) {
        return GsonUtil.getAsObject(mmkv.decodeString(String.valueOf(index)), LogInfo.class, null);
    }

    public int getLogCount() {
        return mmkv.decodeInt(COUNT, 0);
    }

    public void addLog(LogInfo log) {
        int index = getLogCount();
        index++;
        mmkv.encode(COUNT, index);
        mmkv.encode(String.valueOf(index), GsonUtil.toJson(log));
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

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LogSave logSave = (LogSave) o;
        return getKey().equals(logSave.getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public ITreeNodeData loadData(Object flag) {
        return getLog((int) flag);
    }
}
