package top.bogey.touch_tool.service.super_user.shizuku;

import android.content.Context;

import androidx.annotation.Keep;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import top.bogey.touch_tool.service.super_user.SuperUser;

public class ShizukuService extends IShizukuService.Stub {

    public ShizukuService() {

    }

    @Keep
    public ShizukuService(Context context) {

    }

    @Override
    public void destory() {
        System.exit(0);
    }

    @Override
    public String runCommond(String cmd) {
        Process process = null;
        boolean result = false;
        String info;

        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});

            String line;
            StringBuilder infoBuilder = new StringBuilder();
            BufferedReader infoReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = infoReader.readLine()) != null) {
                infoBuilder.append(line).append("\n");
            }
            infoReader.close();
            info = infoBuilder.toString().trim();

            process.waitFor();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            info = e.getMessage();
        } finally {
            if (process != null) process.destroy();
        }
        SuperUser.CmdResult cmdResult = new SuperUser.CmdResult(result, info);
        return new Gson().toJson(cmdResult);
    }
}
