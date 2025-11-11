package top.bogey.touch_tool.utils.callback;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import top.bogey.touch_tool.bean.task.IDragTouchHelperAdapter;

// 通用拖拽回调，支持所有实现了ItemTouchHelperAdapter的适配器
public class CommonDragCallback extends ItemTouchHelper.Callback {
    private final IDragTouchHelperAdapter adapter;

    public CommonDragCallback(IDragTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // 支持上下左右拖拽（网格/列表通用）
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, 0); // 禁用滑动删除
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPos = viewHolder.getBindingAdapterPosition();
        int toPos = target.getBindingAdapterPosition();
        if (fromPos != RecyclerView.NO_POSITION && toPos != RecyclerView.NO_POSITION) {
            adapter.onItemMove(fromPos, toPos);
            return true;
        }
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // 禁用滑动删除
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // 拖拽时的视觉反馈（通用效果）
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
            ViewGroup parent = (ViewGroup) viewHolder.itemView.getParent();
            if (parent != null) {
                parent.bringChildToFront(viewHolder.itemView);
            }
            viewHolder.itemView.setScaleX(1.05f);
            viewHolder.itemView.setScaleY(1.05f);
            viewHolder.itemView.setAlpha(0.7f);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        // 拖拽结束后恢复样式
        viewHolder.itemView.setScaleX(1f);
        viewHolder.itemView.setScaleY(1f);
        viewHolder.itemView.setAlpha(1f);
        // 通知适配器保存排序
        adapter.onItemDragEnded();
    }
}
