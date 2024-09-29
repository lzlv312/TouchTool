package top.bogey.touch_tool.service.super_user.root;

import androidx.annotation.Keep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import top.bogey.touch_tool.service.super_user.ISuperUser;
import top.bogey.touch_tool.service.super_user.SuperUser;

public class RootSuperUser implements ISuperUser {
    private static boolean existRoot = false;

    @Keep
    public RootSuperUser() {

    }

    @Override
    public boolean init() {
        return existRoot();
    }

    @Override
    public boolean tryInit() {
        return existRoot();
    }

    @Override
    public void exit() {

    }

    @Override
    public boolean isValid() {
        return existRoot();
    }

    @Override
    public SuperUser.CmdResult runCommand(String cmd) {
        if (existRoot()) {
            Process process = null;
            boolean result = false;
            String info;

            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});

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
            return new SuperUser.CmdResult(result, info);
        }

        return null;
    }

    public static boolean existRoot() {
        if (existRoot) return true;

        Process process = null;
        OutputStreamWriter writer = null;
        try {
            process = Runtime.getRuntime().exec("su");
            writer = new OutputStreamWriter(process.getOutputStream());
            writer.write("exit\n");
            writer.flush();
            int value = process.waitFor();
            existRoot = value == 0;
            return existRoot;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) process.destroy();
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
