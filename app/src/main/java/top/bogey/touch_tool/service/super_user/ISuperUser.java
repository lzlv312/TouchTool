package top.bogey.touch_tool.service.super_user;

public interface ISuperUser {
    boolean init();

    boolean tryInit();

    void exit();

    boolean isValid();

    SuperUser.CmdResult runCommand(String cmd);
}
