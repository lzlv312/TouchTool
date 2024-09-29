package top.bogey.touch_tool.service.ocr;

import android.graphics.Rect;

public class OCRResult {
    private final Rect area;
    private final String text;
    private final int similar;

    public OCRResult(Rect area, String text, int similar) {
        this.area = area;
        this.text = text;
        this.similar = similar;
    }

    public Rect getArea() {
        return area;
    }

    public int getSimilar() {
        return similar;
    }

    public String getText() {
        return text;
    }
}
