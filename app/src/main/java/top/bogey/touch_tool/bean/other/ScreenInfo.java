package top.bogey.touch_tool.bean.other;

import android.graphics.Bitmap;

import java.util.List;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;

public class ScreenInfo {
    private Bitmap screenShot;
    private final List<NodeInfo> rootNodes;

    public ScreenInfo(MainAccessibilityService service) {
        this(service, null);
    }

    public ScreenInfo(MainAccessibilityService service, BooleanResultCallback callback) {
        service.tryGetScreenShot(bitmap -> {
            screenShot = bitmap;
            if (callback != null) callback.onResult(true);
        });
        rootNodes = NodeInfo.getWindows();
    }

    public Bitmap getScreenShot() {
        return screenShot;
    }

    public List<NodeInfo> getRootNodes() {
        return rootNodes;
    }
}
