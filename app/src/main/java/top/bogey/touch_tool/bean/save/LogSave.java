package top.bogey.touch_tool.bean.save;

import com.tencent.mmkv.MMKV;

import top.bogey.touch_tool.utils.GsonUtil;

public class LogSave {
    private static final String INDEX = "index";

    private final MMKV mmkv;

    private long time = System.currentTimeMillis();

    public LogSave(String key, String path) {
        mmkv = MMKV.mmkvWithID(key, MMKV.MULTI_PROCESS_MODE, null, path);
    }

    public String getLog() {
        StringBuilder sb = new StringBuilder();
        int index = mmkv.decodeInt(INDEX, 0);
        for (int i = index; i > 0; i--) {
            LogInfo logInfo = GsonUtil.getAsObject(mmkv.decodeString(String.valueOf(i)), LogInfo.class, null);
            if (logInfo != null) {
                sb.append(logInfo.getLog()).append("\n\n");
            }
        }
        time = System.currentTimeMillis();
        return sb.toString().trim();
    }

    public void addLog(String log) {
        int index = mmkv.decodeInt(INDEX, 0);
        index++;
        LogInfo logInfo = new LogInfo(index, log);
        mmkv.encode(INDEX, index);
        mmkv.encode(String.valueOf(index), GsonUtil.toJson(logInfo));
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
