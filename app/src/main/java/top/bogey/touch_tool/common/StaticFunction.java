package top.bogey.touch_tool.common;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

public class StaticFunction {

    public static int measureArrayAdapterContentWidth(Context context, ArrayAdapter<?> adapter) {
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        for (int i = 0; i < adapter.getCount(); i++) {
            int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, itemView, new FrameLayout(context));
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
        }

        return maxWidth;
    }


}
