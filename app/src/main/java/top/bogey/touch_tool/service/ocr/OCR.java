package top.bogey.touch_tool.service.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import top.bogey.touch_tool.MainApplication;

public class OCR {
    private final static String KEYS = "keys.txt";
    private final static String DET = "det.nb";
    private final static String CLS = "cls.nb";
    private final static String REC = "rec.nb";
    private final static Map<String, Long> modules = new HashMap<>();
    private final static Map<String, List<String>> labels = new HashMap<>();

    private static boolean loadModule(String name) {
        Context context = MainApplication.getInstance();
        String modulePath = context.getFilesDir() + File.separator + "ocr" + File.separator + name + File.separator;
        long module = initModule(modulePath + DET, modulePath + CLS, modulePath + REC);
        modules.put(name, module);

        ArrayList<String> list = new ArrayList<>();
        list.add("black");
        try (InputStream inputStream = new FileInputStream(modulePath + KEYS)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        list.add(" ");
        labels.put(name, list);

        return module != 0;
    }

    public static boolean importModule(Uri uri) {
        Context context = MainApplication.getInstance();

        String moduleName = null;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(context.getContentResolver().openInputStream(uri));
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if ("VERSION".equals(name)) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
                    moduleName = bufferedReader.readLine();
                    bufferedReader.close();
                    break;
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (moduleName == null) return false;

        String modulePath = context.getFilesDir() + File.separator + "ocr" + File.separator + moduleName;
        File dirFile = new File(modulePath);
        if (!dirFile.exists()) if (!dirFile.mkdirs()) return false;

        ArrayList<String> fileNames = new ArrayList<>(Arrays.asList(KEYS, DET, CLS, REC));
        try {
            ZipInputStream zipInputStream = new ZipInputStream(context.getContentResolver().openInputStream(uri));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String name = zipEntry.getName();
                if (!fileNames.remove(name)) return false;

                File file = new File(modulePath, name);
                if (file.isDirectory()) continue;

                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = zipInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
                outputStream.close();
            }
            zipInputStream.close();
            return fileNames.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<OCRResult> runOcr(String moduleName, Bitmap image) {
        List<OCRResult> results = new ArrayList<>();
        Long module = modules.get(moduleName);
        List<String> list = labels.get(moduleName);
        if (module == null) {
            if (loadModule(moduleName)) {
                module = modules.get(moduleName);
                list = labels.get(moduleName);
            } else {
                return results;
            }
        }
        if (module == null || list == null) return results;
        if (image == null) return results;

        float[] ocrData = forward(module, image, Math.max(image.getWidth(), image.getHeight()), 0, 0, 0);
        int begin = 0;
        while (begin < ocrData.length) {
            int pointNum = Math.round(ocrData[begin]);
            int wordNum = Math.round(ocrData[begin + 1]);
            int similar = Math.round(ocrData[begin + 2] * 100);

            int current = begin + 3;
            Rect rect = new Rect();
            boolean init = false;
            for (int i = 0; i < pointNum; i++) {
                int x = Math.round(ocrData[current + i * 2]);
                int y = Math.round(ocrData[current + i * 2 + 1]);
                if (init) {
                    rect.left = Math.min(x, rect.left);
                    rect.right = Math.max(x, rect.right);
                    rect.top = Math.min(y, rect.top);
                    rect.bottom = Math.max(y, rect.bottom);
                } else {
                    rect.set(x, y, x, y);
                    init = true;
                }
            }

            StringBuilder builder = new StringBuilder();
            current += (pointNum * 2);
            for (int i = 0; i < wordNum; i++) {
                int index = Math.round(ocrData[current + i]);
                builder.append(list.get(index));
            }

            results.add(new OCRResult(rect, builder.toString(), similar));

            begin += (3 + pointNum * 2 + wordNum + 2);
        }

        results.sort((o1, o2) -> {
            int topOffset = -(o1.getArea().top - o2.getArea().top);
            if (Math.abs(topOffset) <= 10) {
                return -(o1.getArea().left - o2.getArea().left);
            } else {
                return topOffset;
            }
        });

        return results;
    }

    public static boolean releaseModule(String moduleName) {
        Long module = modules.remove(moduleName);
        if (module != null) {
            releaseModule(module);
            labels.remove(moduleName);
            return true;
        }
        return false;
    }

    private static native long initModule(String det, String cls, String rec);

    private static native float[] forward(long module, Bitmap image, int size, int det, int cls, int rec);

    private static native void releaseModule(long module);
}
