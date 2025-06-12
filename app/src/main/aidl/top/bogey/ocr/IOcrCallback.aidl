// IOcrCallback.aidl
package top.bogey.ocr;

import top.bogey.touch_tool.service.OcrResult;
import java.util.List;

interface IOcrCallback {
    void onResult(in List<OcrResult> result);
}