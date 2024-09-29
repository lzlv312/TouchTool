package top.bogey.touch_tool.service.super_user;

import androidx.annotation.Keep;

import java.lang.reflect.Constructor;

import top.bogey.touch_tool.service.super_user.root.RootSuperUser;
import top.bogey.touch_tool.service.super_user.shizuku.ShizukuSuperUser;
import top.bogey.touch_tool.ui.setting.SettingSaver;

public class SuperUser implements ISuperUser {
    private static ISuperUser instance;

    static ISuperUser getInstance(Class<? extends ISuperUser> clazz) {
        synchronized (SuperUser.class) {
            if (instance != null) {
                if (!clazz.equals(instance.getClass())) {
                    instance = null;
                }
            }

            if (instance == null) {
                try {
                    Constructor<? extends ISuperUser> constructor = clazz.getDeclaredConstructor();
                    instance = constructor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public static ISuperUser getInstance() {
        if (SettingSaver.getInstance().getSuperUser() == 1) {
            return getInstance(ShizukuSuperUser.class);
        } else if (SettingSaver.getInstance().getSuperUser() == 2) {
            return getInstance(RootSuperUser.class);
        } else {
            return getInstance(SuperUser.class);
        }
    }

    @Keep
    private SuperUser() {

    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public boolean tryInit() {
        return false;
    }

    @Override
    public void exit() {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public CmdResult runCommand(String cmd) {
        return null;
    }

    public record CmdResult(boolean result, String info) {
    }
}
