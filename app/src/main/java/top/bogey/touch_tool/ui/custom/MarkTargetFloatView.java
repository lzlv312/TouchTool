package top.bogey.touch_tool.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

public class MarkTargetFloatView extends AppCompatImageView implements FloatInterface {
    private final static int LINE_WIDTH = 10;
    private final static int PADDING = LINE_WIDTH * 2;

    private final String tag = UUID.randomUUID().toString();

    private final Paint paint;

    private Rect targetArea;

    public static void showTargetArea(Rect targetArea) {
        boolean showTargetArea = SettingSaver.getInstance().isShowNodeArea();
        if (!showTargetArea) return;

        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            MarkTargetFloatView floatView = new MarkTargetFloatView(keepView.getThemeContext());
            floatView.innerShowTargetArea(targetArea);
            floatView.show();
        });
    }

    public MarkTargetFloatView(@NonNull Context context) {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtil.getAttrColor(getContext(), R.attr.colorPrimaryLight));
        paint.setStrokeWidth(LINE_WIDTH);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void innerShowTargetArea(Rect targetArea) {
        this.targetArea = targetArea;
        int width = targetArea.width() + PADDING * 2;
        int height = targetArea.height() + PADDING * 2;
        if (width <= 0 || height <= 0) return;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(PADDING - targetArea.left, PADDING - targetArea.top);

        canvas.drawRoundRect(new RectF(targetArea), PADDING, PADDING, paint);
        setImageBitmap(bitmap);
        postDelayed(() -> animate().alpha(0).withEndAction(this::dismiss), 500);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = targetArea.width() + PADDING * 2;
        int height = targetArea.height() + PADDING * 2;
        setMeasuredDimension(width, height);
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setLocation(EAnchor.TOP_LEFT, targetArea.left - PADDING, targetArea.top - PADDING)
                .setDragAble(false)
                .setSpecial(true)
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(tag);
    }
}
