// IOcrCallback.aidl
package top.bogey.ocr;

import top.bogey.touch_tool.service.OcrResult;

parcelable OcrResult;

interface IOcrCallback {
    void onResult(in List<OcrResult> result);
}