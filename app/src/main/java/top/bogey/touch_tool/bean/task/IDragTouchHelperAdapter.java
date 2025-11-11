package top.bogey.touch_tool.bean.task;

//拖拽接口,目前主要用于标签和任务卡片的拖拽排序
public interface IDragTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDragEnded();
}