package top.bogey.touch_tool.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.annotation.ColorInt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DisplayUtil {
    private static int statusHeight = -1;

    @ColorInt
    public static int getAttrColor(Context context, int id) {
        int[] attrs = {id};
        try (TypedArray typedArray = context.obtainStyledAttributes(attrs)) {
            int resourceId = typedArray.getResourceId(0, 0);
            return context.getColor(resourceId);
        } catch (Exception e) {
            return 0;
        }
    }

    @ColorInt
    public static int blendColor(int color1, int color2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        final float inverseRatio = 1f - ratio;
        int a = (int) (Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio);
        int r = (int) (Color.red(color1) * inverseRatio + Color.red(color2) * ratio);
        int g = (int) (Color.green(color1) * inverseRatio + Color.green(color2) * ratio);
        int b = (int) (Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio);
        return Color.argb(a, r, g, b);
    }

    @ColorInt
    public static int getTextColor(@ColorInt int backgroundColor) {
        return Color.luminance(backgroundColor) > 0.5 ? Color.BLACK : Color.WHITE;
    }

    public static float dp2px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float sp2px(Context context, float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static boolean isPortrait(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getRotation() % 2 == Surface.ROTATION_0;
    }

    public static boolean isInFreeFormMode(Activity activity) {
        if (activity.isInMultiWindowMode()) return true;
        Point screenSize = getScreenSize(activity);

        View decorView = activity.getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getDrawingRect(rect);
        return screenSize.x != rect.width() || screenSize.y != rect.height();
    }

    public static Rect getScreenArea(Context context) {
        Point size = getScreenSize(context);
        return new Rect(0, 0, size.x, size.y);
    }

    public static Point getScreenSize(Context context) {
        Point point = new Point();
        // 获取屏幕宽高
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealSize(point);
        return point;
    }

    public static int getScreenWidth(Context context) {
        Point size = getScreenSize(context);
        return Math.min(size.x, size.y);
    }

    @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    public static int getStatusBarHeight(View view, WindowManager.LayoutParams params) {
        if (statusHeight >= 0) return statusHeight;
        if (view == null) return 0;
        if (params == null) return getStatusBarHeight(view.getContext());

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        // 绝对坐标与相对坐标一致，代表状态栏高度为0
        statusHeight = location[0] + location[1] - params.x - params.y;
        return statusHeight;
    }

    public static void resetStatusBarHeight() {
        statusHeight = -1;
    }

    // 不用getLocationOnScreen，因为这种方法没有考虑缩放
    public static PointF getLocationRelativeToView(View view, View relativeView) {
        PointF pointF = new PointF(view.getX(), view.getY());
        ViewParent viewParent = view.getParent();
        while (viewParent != null && viewParent != relativeView) {
            View parentView = (View) viewParent;
            pointF.offset(parentView.getX(), parentView.getY());
            viewParent = viewParent.getParent();
        }
        return pointF;
    }

    public static void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        else params.width = width;
        view.setLayoutParams(params);
    }

    public static void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        else params.height = height;
        view.setLayoutParams(params);
    }

    public static void setViewMargin(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (params == null) return;
        params.setMargins(left, top, right, bottom);
        view.setLayoutParams(params);
    }

    public static Rect getPointsArea(List<Point> points) {
        Rect rect = new Rect();
        boolean init = false;
        for (Point point : points) {
            if (init) {
                rect.left = Math.min(rect.left, point.x);
                rect.top = Math.min(rect.top, point.y);
                rect.right = Math.max(rect.right, point.x);
                rect.bottom = Math.max(rect.bottom, point.y);
            } else {
                rect.set(point.x, point.y, point.x, point.y);
                init = true;
            }
        }
        return rect;
    }

    public static RectF getPointFsArea(List<PointF> points) {
        RectF rect = new RectF();
        boolean init = false;
        for (PointF point : points) {
            if (init) {
                rect.left = Math.min(rect.left, point.x);
                rect.top = Math.min(rect.top, point.y);
                rect.right = Math.max(rect.right, point.x);
                rect.bottom = Math.max(rect.bottom, point.y);
            } else {
                rect.set(point.x, point.y, point.x, point.y);
                init = true;
            }
        }
        return rect;
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int width, int height) {
        final int srcWidth = bitmap.getWidth();
        final int srcHeight = bitmap.getHeight();
        if (srcWidth == width && srcHeight == height) return bitmap;

        final float scaleX = width / (float) srcWidth;
        final float scaleY = height / (float) srcHeight;
        final float scale = Math.min(scaleX, scaleY);
        final Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);
    }

    public static Bitmap safeClipBitmap(Bitmap bitmap, int x, int y, int width, int height) {
        Rect area = safeClipBitmapArea(bitmap, x, y, width, height);
        if (area == null) return null;
        return Bitmap.createBitmap(bitmap, area.left, area.top, area.width(), area.height());
    }

    public static Rect safeClipBitmapArea(Bitmap bitmap, int x, int y, int width, int height) {
        if (bitmap == null) return null;
        if (x < 0) {
            width += x;
            x = 0;
        }
        if (y < 0) {
            height += y;
            y = 0;
        }
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (x > bitmapWidth) return null;
        if (y > bitmapHeight) return null;
        if (x + width > bitmapWidth) width = bitmapWidth - x;
        if (y + height > bitmapHeight) height = bitmapHeight - y;
        return new Rect(x, y, x + width, y + height);
    }


    public static Bitmap createTextBitmap(Context context, String text, int textColor, int textSizeSp, int maxWidth, int lineSpacing, int padding) {
        // 1. 初始化Paint
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(sp2px(context, textSizeSp));
        paint.setAntiAlias(true);

        // 2. 分割文本为多行并记录每行实际宽度
        String[] paragraphs = text.split("\n", -1); // -1保留空行

        List<String> lines = new ArrayList<>();
        List<Float> lineWidths = new ArrayList<>();
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add(" ");
                lineWidths.add(0f);
                continue;
            }

            int start = 0;
            while (start < paragraph.length()) {
                // 测量能显示多少个字符
                int count = paint.breakText(paragraph, start, paragraph.length(), true, maxWidth - 2 * padding, null);
                String line = paragraph.substring(start, start + count);
                lines.add(line);

                // 记录每行实际宽度
                float lineWidth = paint.measureText(line);
                lineWidths.add(lineWidth);

                start += count;
            }
        }

        // 3. 计算实际需要的宽度（取最长行宽度）
        float maxLineWidth = 0;
        for (float width : lineWidths) {
            if (width > maxLineWidth) {
                maxLineWidth = width;
            }
        }
        int totalWidth = (int) (maxLineWidth + 2 * padding);

        // 4. 测量总高度
        Paint.FontMetrics fm = paint.getFontMetrics();
        float lineHeight = fm.descent - fm.ascent;
        int totalHeight = (int) (lines.size() * lineHeight + (lines.size() - 1) * lineSpacing + 2 * padding);

        // 5. 创建Bitmap（使用实际需要的宽度而非最大宽度）
        Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 6. 逐行绘制文本
        float y = padding - fm.ascent; // 初始y位置
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            canvas.drawText(line, padding, y, paint);
            y += lineHeight + lineSpacing;
        }

        return bitmap;
    }

    public static native MatchResult nativeMatchTemplate(Bitmap bitmap, Bitmap template, int speed);

    public static Rect matchTemplate(Bitmap bitmap, Bitmap template, Rect area, int similarity) {
        return matchTemplate(bitmap, template, area, similarity, 2);
    }

    public static Rect matchTemplate(Bitmap bitmap, Bitmap template, Rect area, int similarity, int speed) {
        if (bitmap == null) return null;
        if (template == null) return null;
        // 如果图片尺寸小于模板尺寸，则不匹配
        if (bitmap.getWidth() < template.getWidth() || bitmap.getHeight() < template.getHeight()) return null;

        if (area == null) area = new Rect();

        Bitmap tmp = null;
        if (!area.isEmpty()) {
            bitmap = safeClipBitmap(bitmap, area.left, area.top, area.width(), area.height());
            tmp = bitmap;
            if (bitmap == null) return null;
        }

        MatchResult matchResult = nativeMatchTemplate(bitmap, template, (int) Math.pow(2, speed));
        if (tmp != null) tmp.recycle();
        if (matchResult == null) return null;
        if (matchResult.value * 100 < similarity) return null;
        matchResult.area.offset(area.left, area.top);
        return matchResult.area;
    }

    public static native List<MatchResult> nativeMatchAllTemplate(Bitmap bitmap, Bitmap template, int similarity, int speed);

    public static synchronized List<Rect> matchAllTemplate(Bitmap bitmap, Bitmap template, Rect area, int similarity, int speed) {
        if (bitmap == null) return null;
        if (template == null) return null;
        // 如果图片尺寸小于模板尺寸，则不匹配
        if (bitmap.getWidth() < template.getWidth() || bitmap.getHeight() < template.getHeight()) return null;

        if (area == null) area = new Rect();

        Bitmap tmp = null;
        if (!area.isEmpty()) {
            bitmap = safeClipBitmap(bitmap, area.left, area.top, area.width(), area.height());
            tmp = bitmap;
            if (bitmap == null) return null;
        }

        List<MatchResult> matchResults = nativeMatchAllTemplate(bitmap, template, similarity, (int) Math.pow(2, speed));
        if (tmp != null) tmp.recycle();

        if (matchResults == null || matchResults.isEmpty()) return null;
        matchResults.sort(Comparator.comparingDouble(result -> result.value));
        List<Rect> rectList = new ArrayList<>();
        for (int i = matchResults.size() - 1; i >= 0; i--) {
            MatchResult result = matchResults.get(i);
            result.area.offset(area.left, area.top);
            rectList.add(result.area);
        }
        return rectList;
    }

    public static native List<MatchResult> nativeMatchColor(Bitmap bitmap, int[] color, int similarity);

    public static synchronized List<Rect> matchColor(Bitmap bitmap, int color, Rect area, int similarity) {
        if (bitmap == null) return null;
        if (area == null) area = new Rect();

        Bitmap tmp = null;
        if (!area.isEmpty()) {
            bitmap = safeClipBitmap(bitmap, area.left, area.top, area.width(), area.height());
            tmp = bitmap;
            if (bitmap == null) return null;
        }

        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        List<MatchResult> matchResults = nativeMatchColor(bitmap, rgb, similarity);
        if (tmp != null) tmp.recycle();

        if (matchResults == null || matchResults.isEmpty()) return null;
        matchResults.sort(Comparator.comparingDouble(result -> result.value));
        List<Rect> rectList = new ArrayList<>();
        for (int i = matchResults.size() - 1; i >= 0; i--) {
            MatchResult result = matchResults.get(i);
            result.area.offset(area.left, area.top);
            rectList.add(result.area);
        }
        return rectList;
    }
}
