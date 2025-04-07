// IOcr.aidl
package top.bogey.ocr;

import android.graphics.Bitmap;
import top.bogey.touch_tool.service.ocr.OcrResult;

parcelable OcrResult;

interface IOcr {
    List<OcrResult> runOcr(in Bitmap bitmap);
}