package top.bogey.touch_tool.bean.pin;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.pin.pin_objects.PinAdd;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBoolean;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinNode;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleSelect;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinValueArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinIconExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinStringExecute;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinDate;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinFloat;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinInteger;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinLong;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinPeriodic;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinTime;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinArea;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinColor;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinImage;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinPoint;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able.PinTouchPath;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinAutoPinString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinLogString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinMultiLineString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinRingtoneString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinShortcutString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinUrlString;
import top.bogey.touch_tool.ui.blueprint.pin_slot.ExecutePinSlotView;
import top.bogey.touch_tool.ui.blueprint.pin_slot.ListPinSlotView;
import top.bogey.touch_tool.ui.blueprint.pin_slot.MapPinSlotView;
import top.bogey.touch_tool.ui.blueprint.pin_slot.NormalPinSlotView;
import top.bogey.touch_tool.ui.blueprint.pin_slot.PinSlotView;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidget;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetAdd;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetApp;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetApps;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetArea;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetBoolean;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetColor;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetExecute;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetImage;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetNumber;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetPoint;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetSelect;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetString;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetTouch;
import top.bogey.touch_tool.ui.blueprint.pin_widget.PinWidgetValueArea;
import top.bogey.touch_tool.utils.DisplayUtil;

public class PinInfo {
    private final static PinInfo EXECUTE_INFO = new PinInfo(PinType.EXECUTE, PinSubType.NORMAL, PinExecute.class, ExecutePinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimary), 0, null, false);
    private final static PinInfo ICON_EXECUTE_INFO = new PinInfo(PinType.EXECUTE, PinSubType.WITH_ICON, PinIconExecute.class, ExecutePinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimary), 0, PinWidgetExecute.class, false);
    private final static PinInfo STRING_EXECUTE_INFO = new PinInfo(PinType.EXECUTE, PinSubType.WITH_STRING, PinStringExecute.class, ExecutePinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimary), 0, PinWidgetExecute.class, false);

    private final static PinInfo ADD_INFO = new PinInfo(PinType.ADD, PinSubType.NORMAL, PinAdd.class, NormalPinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorSurfaceVariant), 0, PinWidgetAdd.class, false);

    private final static PinInfo OBJECT_INFO = new PinInfo(PinType.OBJECT, PinSubType.NORMAL, PinObject.class, NormalPinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimaryInverse), 0, null, true);

    private final static PinInfo LIST_INFO = new PinInfo(PinType.LIST, PinSubType.NORMAL, PinList.class, ListPinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimaryInverse), 0, null, true);
    private final static PinInfo APPS_INFO = new PinInfo(PinType.LIST, PinSubType.MULTI_APP, PinApplications.class, ListPinSlotView.class, getColor(R.color.AppPinColor), 0, PinWidgetApps.class, true);

    private final static PinInfo MAP_INFO = new PinInfo(PinType.MAP, PinSubType.NORMAL, PinMap.class, MapPinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimaryInverse), 0, null, true);

    private final static PinInfo STRING_INFO = new PinInfo(PinType.STRING, PinSubType.NORMAL, PinString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo URL_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.URL, PinUrlString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo SHORTCUT_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.SHORTCUT, PinShortcutString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo RINGTONE_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.RINGTONE, PinRingtoneString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo AUTO_PIN_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.AUTO_PIN, PinAutoPinString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo MULTI_LINE_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.MULTI_LINE, PinMultiLineString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo NODE_PATH_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.NODE_PATH, PinNodePathString.class, NormalPinSlotView.class, getColor(R.color.StringPinColor), 0, PinWidgetString.class, true);
    private final static PinInfo LOG_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.LOG, PinLogString.class, NormalPinSlotView.class, getAttrColor(com.google.android.material.R.attr.colorPrimaryInverse), 0, PinWidgetString.class, true);
    private final static PinInfo SELECT_STRING_INFO = new PinInfo(PinType.STRING, PinSubType.SINGLE_SELECT, PinSingleSelect.class, NormalPinSlotView.class, getColor(R.color.SelectPinColor), 0, PinWidgetSelect.class, true);

    private final static PinInfo INTEGER_INFO = new PinInfo(PinType.NUMBER, PinSubType.INTEGER, PinInteger.class, NormalPinSlotView.class, getColor(R.color.IntegerPinColor), 0, PinWidgetNumber.class, true);
    private final static PinInfo FLOAT_INFO = new PinInfo(PinType.NUMBER, PinSubType.FLOAT, PinFloat.class, NormalPinSlotView.class, getColor(R.color.FloatPinColor), 0, PinWidgetNumber.class, true);
    private final static PinInfo LONG_INFO = new PinInfo(PinType.NUMBER, PinSubType.LONG, PinLong.class, NormalPinSlotView.class, getColor(R.color.LongPinColor), 0, PinWidgetNumber.class, true);
    private final static PinInfo DATE_INFO = new PinInfo(PinType.NUMBER, PinSubType.DATE, PinDate.class, NormalPinSlotView.class, getColor(R.color.LongPinColor), 0, PinWidgetNumber.class, true);
    private final static PinInfo TIME_INFO = new PinInfo(PinType.NUMBER, PinSubType.TIME, PinTime.class, NormalPinSlotView.class, getColor(R.color.LongPinColor), 0, PinWidgetNumber.class, true);
    private final static PinInfo PERIODIC_INFO = new PinInfo(PinType.NUMBER, PinSubType.PERIODIC, PinPeriodic.class, NormalPinSlotView.class, getColor(R.color.LongPinColor), 0, PinWidgetNumber.class, true);

    private final static PinInfo BOOLEAN_INFO = new PinInfo(PinType.BOOLEAN, PinSubType.NORMAL, PinBoolean.class, NormalPinSlotView.class, getColor(R.color.BooleanPinColor), 0, PinWidgetBoolean.class, true);
    private final static PinInfo VALUE_AREA_INFO = new PinInfo(PinType.VALUE_AREA, PinSubType.NORMAL, PinValueArea.class, NormalPinSlotView.class, getColor(R.color.ValueAreaPinColor), 0, PinWidgetValueArea.class, true);
    private final static PinInfo POINT_INFO = new PinInfo(PinType.POINT, PinSubType.NORMAL, PinPoint.class, NormalPinSlotView.class, getColor(R.color.PointPinColor), 0, PinWidgetPoint.class, true);
    private final static PinInfo AREA_INFO = new PinInfo(PinType.AREA, PinSubType.NORMAL, PinArea.class, NormalPinSlotView.class, getColor(R.color.AreaPinColor), 0, PinWidgetArea.class, true);
    private final static PinInfo TOUCH_INFO = new PinInfo(PinType.TOUCH, PinSubType.NORMAL, PinTouchPath.class, NormalPinSlotView.class, getColor(R.color.TouchPinColor), 0, PinWidgetTouch.class, true);
    private final static PinInfo NODE_INFO = new PinInfo(PinType.NODE, PinSubType.NORMAL, PinNode.class, NormalPinSlotView.class, getColor(R.color.NodePinColor), 0, null, true);
    private final static PinInfo IMAGE_INFO = new PinInfo(PinType.IMAGE, PinSubType.NORMAL, PinImage.class, NormalPinSlotView.class, getColor(R.color.ImagePinColor), 0, PinWidgetImage.class, true);
    private final static PinInfo COLOR_INFO = new PinInfo(PinType.COLOR, PinSubType.NORMAL, PinColor.class, NormalPinSlotView.class, getColor(R.color.ColorPinColor), 0, PinWidgetColor.class, true);
    private final static PinInfo APP_INFO = new PinInfo(PinType.APP, PinSubType.NORMAL, PinApplication.class, NormalPinSlotView.class, getColor(R.color.AppPinColor), 0, PinWidgetApp.class, true);

    private static @ColorInt int getColor(@ColorRes int color) {
        return MainApplication.getInstance().getActivity().getColor(color);
    }

    private static @ColorInt int getAttrColor(@AttrRes int attr) {
        return DisplayUtil.getAttrColor(MainApplication.getInstance().getActivity(), attr);
    }

    public static PinInfo getPinInfo(PinBase pin) {
        return getPinInfo(pin.getType(), pin.getSubType());
    }

    public static PinInfo getPinInfo(PinType type) {
        return getPinInfo(type, PinSubType.NORMAL);
    }

    public static PinInfo getPinInfo(PinType type, PinSubType subType) {
        PinInfo info = null;
        switch (type) {
            case EXECUTE -> {
                switch (subType) {
                    case NORMAL -> info = EXECUTE_INFO;
                    case WITH_ICON -> info = ICON_EXECUTE_INFO;
                    case WITH_STRING -> info = STRING_EXECUTE_INFO;
                }
            }
            case ADD -> info = ADD_INFO;
            case OBJECT -> info = OBJECT_INFO;
            case LIST -> {
                switch (subType) {
                    case NORMAL -> info = LIST_INFO;
                    case MULTI_APP, MULTI_APP_WITH_ACTIVITY, MULTI_APP_WITH_EXPORT_ACTIVITY -> info = APPS_INFO;
                }
            }
            case MAP -> info = MAP_INFO;
            case STRING -> {
                switch (subType) {
                    case NORMAL -> info = STRING_INFO;
                    case URL -> info = URL_STRING_INFO;
                    case SHORTCUT -> info = SHORTCUT_STRING_INFO;
                    case RINGTONE -> info = RINGTONE_STRING_INFO;
                    case AUTO_PIN -> info = AUTO_PIN_STRING_INFO;
                    case MULTI_LINE -> info = MULTI_LINE_STRING_INFO;
                    case NODE_PATH -> info = NODE_PATH_STRING_INFO;
                    case LOG -> info = LOG_STRING_INFO;
                    case SINGLE_SELECT -> info = SELECT_STRING_INFO;
                }
            }
            case NUMBER -> {
                switch (subType) {
                    case NORMAL, INTEGER -> info = INTEGER_INFO;
                    case FLOAT -> info = FLOAT_INFO;
                    case LONG -> info = LONG_INFO;
                    case DATE -> info = DATE_INFO;
                    case TIME -> info = TIME_INFO;
                    case PERIODIC -> info = PERIODIC_INFO;
                }
            }
            case BOOLEAN -> info = BOOLEAN_INFO;
            case VALUE_AREA -> info = VALUE_AREA_INFO;
            case POINT -> info = POINT_INFO;
            case AREA -> info = AREA_INFO;
            case TOUCH -> info = TOUCH_INFO;
            case NODE -> info = NODE_INFO;
            case IMAGE -> info = IMAGE_INFO;
            case COLOR -> info = COLOR_INFO;
            case APP -> info = APP_INFO;
        }
        return info;
    }

    public static List<PinType> getKeyPinTypes(PinType type, PinSubType subType) {
        List<PinType> list = getValuePinTypes();
        if (type == PinType.LIST && subType != PinSubType.NORMAL) {
            list = List.of(PinType.APP);
        }
        return list;
    }

    public static List<PinType> getValuePinTypes() {
        List<PinType> list = new ArrayList<>();
        for (PinType pinType : PinType.values()) {
            PinInfo pinInfo = getPinInfo(pinType);
            if (pinInfo != null && pinInfo.isValuePin()) {
                list.add(pinType);
            }
        }
        return list;
    }

    public static List<PinSubType> getValuePinSubTypes(PinType type) {
        List<PinSubType> list = null;
        switch (type) {
            case STRING -> list = List.of(PinSubType.RINGTONE, PinSubType.MULTI_LINE, PinSubType.NODE_PATH);
            case NUMBER -> list = List.of(PinSubType.INTEGER, PinSubType.FLOAT);
            case APP -> list = List.of(PinSubType.SINGLE_APP, PinSubType.SINGLE_ACTIVITY, PinSubType.SINGLE_APP_WITH_ACTIVITY, PinSubType.SINGLE_APP_WITH_EXPORT_ACTIVITY);
            case LIST -> list = List.of(PinSubType.MULTI_APP, PinSubType.MULTI_APP_WITH_ACTIVITY, PinSubType.MULTI_APP_WITH_EXPORT_ACTIVITY);
        }
        if (list != null) list.add(0, PinSubType.NORMAL);
        else list = List.of(PinSubType.NORMAL);
        return list;
    }

    public static String getPinTypeTitle(PinType type) {
        String[] strings = MainApplication.getInstance().getResources().getStringArray(R.array.pin_type);
        if (type.ordinal() < strings.length) return strings[type.ordinal()];
        return "";
    }

    public static String getPinSubTypeTitle(PinSubType subType) {
        String[] strings = MainApplication.getInstance().getResources().getStringArray(R.array.pin_sub_type);
        if (subType.ordinal() < strings.length) return strings[subType.ordinal()];
        return "";
    }

    //-------------------------------------------------------------------------------------------------

    private final PinType type;
    private final PinSubType subType;

    private final Class<? extends PinBase> clazz;

    private final Class<? extends PinSlotView> slot;
    @ColorInt
    private final int color;

    @StringRes
    private final int title;

    private final Class<? extends PinWidget<? extends PinBase>> widget;

    private final boolean valuePin;

    public PinInfo(PinType type, PinSubType subType, Class<? extends PinBase> clazz, Class<? extends PinSlotView> slot, @ColorInt int color, @StringRes int title, Class<? extends PinWidget<? extends PinBase>> widget, boolean valuePin) {
        this.type = type;
        this.subType = subType;
        this.clazz = clazz;
        this.slot = slot;
        this.color = color;
        this.title = title;
        this.widget = widget;
        this.valuePin = valuePin;
    }

    public PinBase newInstance() {
        try {
            Constructor<? extends PinBase> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PinType getType() {
        return type;
    }

    public PinSubType getSubType() {
        return subType;
    }

    public Class<? extends PinBase> getClazz() {
        return clazz;
    }

    public Class<? extends PinSlotView> getSlot() {
        return slot;
    }

    public @ColorInt int getColor() {
        if (color == 0) return DisplayUtil.getAttrColor(MainApplication.getInstance(), com.google.android.material.R.attr.colorPrimaryInverse);
        return color;
    }

    public String getTitle() {
        if (title == 0) return "";
        return MainApplication.getInstance().getString(title);
    }

    public Class<? extends PinWidget<? extends PinBase>> getWidget() {
        return widget;
    }

    public boolean isValuePin() {
        return valuePin;
    }

    @NonNull
    @Override
    public String toString() {
        return "PinInfo{" +
                "subType=" + subType +
                ", type=" + type +
                ", clazz=" + clazz +
                '}';
    }
}
