package top.bogey.touch_tool.service.super_user;

public interface ISuperUser {
    boolean init();

    boolean tryInit();

    void exit();

    boolean isValid();

    CmdResult runCommand(String cmd);
}
