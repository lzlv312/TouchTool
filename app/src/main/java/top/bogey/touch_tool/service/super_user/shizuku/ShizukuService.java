package top.bogey.touch_tool.service.super_user.shizuku;

import android.content.Context;

import androidx.annotation.Keep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import top.bogey.touch_tool.service.super_user.CmdResult;

public class ShizukuService extends IShizukuService.Stub {
    private Process process = null;
    private BufferedWriter cmdWriter = null;
    private BufferedReader outputReader = null;
    private BufferedReader errorReader = null;

    public ShizukuService() {
        try {
            process = Runtime.getRuntime().exec("su");
            cmdWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Keep
    public ShizukuService(Context context) {
        this();
    }

    @Override
    public void destory() {
        try {
            if (cmdWriter != null) {
                cmdWriter.write("exit\n");
                cmdWriter.flush();
                cmdWriter.close();
            }
            if (outputReader != null) outputReader.close();
            if (errorReader != null) errorReader.close();
            if (process != null) process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public CmdResult runCommand(String cmd) {
        if (process == null) return new CmdResult(false, "");

        try {
            cmdWriter.write(cmd + "\n");
            cmdWriter.write("echo $?\n");
            cmdWriter.flush();

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = outputReader.readLine()) != null) {
                if (line.matches("^[0-9]+$")) {
                    int exitCode = Integer.parseInt(line);
                    return new CmdResult(exitCode == 0, output.toString().trim());
                }
                output.append(line).append("\n");
            }

            StringBuilder error = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }
            return new CmdResult(false, error.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return new CmdResult(false, e.getMessage());
        }
    }
}
