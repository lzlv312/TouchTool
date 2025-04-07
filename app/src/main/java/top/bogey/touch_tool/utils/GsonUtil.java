package top.bogey.touch_tool.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_application.PinApplications;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;

public class GsonUtil {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new Task.TaskDeserialize())
            .registerTypeAdapter(Variable.class, new Variable.VariableDeserialize())
            .registerTypeAdapter(Action.class, new Action.ActionDeserializer())
            .registerTypeAdapter(Pin.class, new Pin.PinDeserialize())
            .registerTypeAdapter(PinBase.class, new PinBase.PinBaseDeserializer())

            .registerTypeAdapter(PinList.class, new PinList.PinListSerializer())
            .registerTypeAdapter(PinApplications.class, new PinList.PinListSerializer())
            .registerTypeAdapter(PinMap.class, new PinMap.PinMapSerializer())
            .create();

    public static <T> T copy(T src, Class<T> clazz) {
        return gson.fromJson(gson.toJson(src), clazz);
    }

    public static <T> T copy(T src, Type typeOfSrc) {
        return gson.fromJson(gson.toJson(src), typeOfSrc);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T getAsObject(JsonObject jsonObject, String key, Class<T> clazz, T defaultValue) {
        if (jsonObject.has(key)) {
            return gson.fromJson(jsonObject.get(key), clazz);
        }
        return defaultValue;
    }

    public static <T> T getAsObject(JsonObject jsonObject, String key, Type typeOfSrc, T defaultValue) {
        if (jsonObject.has(key)) {
            return gson.fromJson(jsonObject.get(key), typeOfSrc);
        }
        return defaultValue;
    }

    public static <T> T getAsObject(String json, Class<T> clazz, T defaultValue) {
        if (json != null) {
            return gson.fromJson(json, clazz);
        }
        return defaultValue;
    }

    public static <T> T getAsObject(String json, Type typeOfSrc, T defaultValue) {
        if (json != null) {
            return gson.fromJson(json, typeOfSrc);
        }
        return defaultValue;
    }

    public static int getAsInt(JsonObject jsonObject, String key, int defaultValue) {
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsInt();
        }
        return defaultValue;
    }

    public static float getAsFloat(JsonObject jsonObject, String key, float defaultValue) {
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsFloat();
        }
        return defaultValue;
    }

    public static long getAsLong(JsonObject jsonObject, String key, long defaultValue) {
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsLong();
        }
        return defaultValue;
    }

    public static double getAsDouble(JsonObject jsonObject, String key, double defaultValue) {
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsDouble();
        }
        return defaultValue;
    }

    public static boolean getAsBoolean(JsonObject jsonObject, String key, boolean defaultValue) {
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsBoolean();
        }
        return defaultValue;
    }


    public static String getAsString(JsonObject jsonObject, String key, String defaultValue) {
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsString();
        }
        return defaultValue;
    }


}
