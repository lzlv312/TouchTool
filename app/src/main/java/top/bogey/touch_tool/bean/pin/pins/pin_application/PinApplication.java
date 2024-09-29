package top.bogey.touch_tool.bean.pin.pins.pin_application;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.pin.pins.PinObject;
import top.bogey.touch_tool.bean.pin.pins.PinSubType;
import top.bogey.touch_tool.bean.pin.pins.PinType;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinApplication extends PinObject {
    private String packageName;
    private List<String> activityClasses;

    public PinApplication() {
        super(PinType.APP, PinSubType.SINGLE_APP_WITH_ACTIVITY);
    }

    public PinApplication(String packageName) {
        this();
        this.packageName = packageName;
    }

    public PinApplication(PinSubType subType) {
        super(PinType.APP, subType);
    }

    public PinApplication(PinSubType subType, String packageName) {
        super(PinType.APP, subType);
        this.packageName = packageName;
    }

    public PinApplication(String packageName, String activityClass) {
        this();
        this.packageName = packageName;
        this.activityClasses = new ArrayList<>();
        this.activityClasses.add(activityClass);
    }

    public PinApplication(JsonObject jsonObject) {
        super(jsonObject);
        packageName = GsonUtil.getAsString(jsonObject, "packageName", null);
        activityClasses = GsonUtil.getAsObject(jsonObject, "activityClasses", TypeToken.getParameterized(ArrayList.class, String.class).getType(), null);
    }

    public String getFirstActivity() {
        return activityClasses == null || activityClasses.isEmpty() ? null : activityClasses.get(0);
    }

    @Override
    public void reset() {
        super.reset();
        packageName = null;
        activityClasses = null;
    }

    @NonNull
    @Override
    public String toString() {
        return packageName + activityClasses;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getActivityClasses() {
        return activityClasses;
    }

    public void setActivityClasses(List<String> activityClasses) {
        this.activityClasses = activityClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinApplication that = (PinApplication) o;

        if (getPackageName() != null ? !getPackageName().equals(that.getPackageName()) : that.getPackageName() != null) return false;
        return getActivityClasses() != null ? getActivityClasses().equals(that.getActivityClasses()) : that.getActivityClasses() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getPackageName() != null ? getPackageName().hashCode() : 0);
        result = 31 * result + (getActivityClasses() != null ? getActivityClasses().hashCode() : 0);
        return result;
    }
}
