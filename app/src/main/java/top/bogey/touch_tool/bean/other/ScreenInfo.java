package top.bogey.touch_tool.bean.other;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;

public class ScreenInfo {
    private Bitmap screenShot;
    private final List<NodeInfo> rootNodes = new ArrayList<>();

    public ScreenInfo(MainAccessibilityService service) {
        this(service, null);
    }

    public ScreenInfo(MainAccessibilityService service, BooleanResultCallback callback) {
        service.tryGetScreenShot(bitmap -> {
            screenShot = bitmap;
            if (callback != null) callback.onResult(true);
        });
        for (AccessibilityNodeInfo window : AppUtil.getWindows(service)) {
            rootNodes.add(new NodeInfo(window));
        }
    }

    public Bitmap getScreenShot() {
        return screenShot;
    }

    public List<NodeInfo> getRootNodes() {
        return rootNodes;
    }
}
