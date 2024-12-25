package top.bogey.touch_tool.utils;

import android.annotation.SuppressLint;
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
import android.util.TypedValue;
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int resourceId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        if (resourceId == 0) return 0;
        return context.getColor(resourceId);
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

    public static boolean isPortrait(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getRotation() % 2 == Surface.ROTATION_0;
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

    public static native List<MatchResult> nativeMatchTemplate(Bitmap bitmap, Bitmap template, int similarity, int speed);

    public static synchronized List<Rect> matchTemplate(Bitmap bitmap, Bitmap template, Rect area, int similarity) {
        if (bitmap == null) return null;
        if (template == null) return null;
        if (area == null) area = new Rect();

        Bitmap tmp = null;
        if (!area.isEmpty()) {
            bitmap = safeClipBitmap(bitmap, area.left, area.top, area.width(), area.height());
            tmp = bitmap;
            if (bitmap == null) return null;
        }

        List<MatchResult> matchResults = nativeMatchTemplate(bitmap, template, similarity, 1);
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
