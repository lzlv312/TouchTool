package top.bogey.touch_tool.service.super_user;

import android.content.Intent;

public interface ISuperUser {
    boolean init();

    void tryInit();

    void exit();

    boolean isValid();

    CmdResult runCommand(String cmd);
}
