package top.bogey.touch_tool.ui.blueprint.card;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.ui.blueprint.pin.PinLeftView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.ui.DragViewHolderHelper;
import top.bogey.touch_tool.utils.ui.IDragAbleRecycleViewAdapter;

// 列表、字典的输入项列表，按住针脚上的拖动柄可以调换顺序
public class CreateListActionAdapter extends RecyclerView.Adapter<CreateListActionAdapter.ViewHolder> implements IDragAbleRecycleViewAdapter {
    private final List<PinView> pinViews = new ArrayList<>();
    private final ActionCard card;
    private final ItemTouchHelper touchHelper;
    private RecyclerView recyclerView;

    public CreateListActionAdapter(ActionCard card) {
        this.card = card;
        // 拖动只由针脚上的拖动柄触发，关闭长按拖动
        DragViewHolderHelper helper = new DragViewHolderHelper(DragViewHolderHelper.VERTICAL, this) {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        };
        touchHelper = new ItemTouchHelper(helper);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setAdapter(this);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refresh(pinViews.get(position));
    }

    @Override
    public int getItemCount() {
        return pinViews.size();
    }

    public PinView addPin(Pin pin) {
        // 列表的输入项都是带拖动柄的左侧针脚
        PinView pinView = new PinLeftView(card.getContext(), card, pin, this::startDrag);
        pinViews.add(pinView);
        notifyItemInserted(pinViews.size() - 1);
        return pinView;
    }

    public void removePin(Pin pin) {
        for (int i = 0; i < pinViews.size(); i++) {
            PinView pinView = pinViews.get(i);
            if (pinView.getPin().equals(pin)) {
                pinViews.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    // 按住拖动柄时才开始拖动排序
    private void startDrag(PinView pinView) {
        if (recyclerView == null || pinViews.indexOf(pinView) < 0) return;
        // 针脚被放在 itemView 里，由 RecyclerView 沿父链反查它所在的 ViewHolder
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(pinView);
        if (holder == null) return;
        touchHelper.startDrag(holder);
    }

    @Override
    public void swap(int from, int to) {
        // 视图和数据一起换位，数据顺序就是列表的取值顺序
        Pin fromPin = pinViews.get(from).getPin();
        Pin toPin = pinViews.get(to).getPin();
        pinViews.add(to, pinViews.remove(from));

        List<Pin> pins = card.getAction().getPins();
        int i = pins.indexOf(fromPin);
        int j = pins.indexOf(toPin);
        pins.add(j, pins.remove(i));

        notifyItemMoved(from, to);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout layout;

        public ViewHolder(@NonNull FrameLayout itemView) {
            super(itemView);
            layout = itemView;
        }

        public void refresh(PinView pinView) {
            layout.removeAllViews();
            ViewGroup parent = (ViewGroup) pinView.getParent();
            if (parent != null) parent.removeView(pinView);
            layout.addView(pinView);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pinView.getLayoutParams();
            params.gravity = pinView.getPin().isOut() ? Gravity.END : Gravity.START;
            pinView.setLayoutParams(params);

            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.width = pinView.getPin().isVertical() ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
            layout.setLayoutParams(layoutParams);
        }
    }
}
