// IShizukuService.aidl
package top.bogey.touch_tool.service.super_user.shizuku;

interface IShizukuService {
    void destory() = 16777114;
    String runCommond(String cmd) = 1;
}