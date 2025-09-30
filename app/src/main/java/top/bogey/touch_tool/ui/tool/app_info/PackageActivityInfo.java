package top.bogey.touch_tool.ui.tool.app_info;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.tree.ITreeNodeData;
import top.bogey.touch_tool.utils.tree.ObjectTreeNodeData;

public class PackageActivityInfo implements ITreeNodeData {
    private final String packageName;
    private final String activityName;

    private String name;
    private String versionName;
    private int versionCode;

    private final ArrayList<ITreeNodeData> list = new ArrayList<>();


    public PackageActivityInfo(TaskInfoSummary.PackageActivity packageActivity) {
        packageName = packageActivity.packageName();
        activityName = packageActivity.activityName();
        list.add(new ObjectTreeNodeData(packageName));
        list.add(new ObjectTreeNodeData(activityName));

        PackageInfo packageInfo = TaskInfoSummary.getInstance().getAppInfo(packageName);
        PackageManager manager = MainApplication.getInstance().getPackageManager();
        if (packageInfo != null && packageInfo.applicationInfo != null) {
            name = packageInfo.applicationInfo.loadLabel(manager).toString();
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;

            list.add(new ObjectTreeNodeData(name));
//            list.add(new ObjectTreeNodeData(versionName));
//            list.add(new ObjectTreeNodeData(versionCode));
        }
    }

    @Override
    public List<ITreeNodeData> getChildrenData() {
        return list;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getName() {
        return name;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }
}
