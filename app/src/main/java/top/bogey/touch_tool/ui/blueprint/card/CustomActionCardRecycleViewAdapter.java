package top.bogey.touch_tool.ui.blueprint.card;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.ui.blueprint.pin.PinLeftCustomView;
import top.bogey.touch_tool.ui.blueprint.pin.PinRightCustomView;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;

public class CustomActionCardRecycleViewAdapter extends RecyclerView.Adapter<CustomActionCardRecycleViewAdapter.ViewHolder> {
    private final List<PinView> pinViews = new ArrayList<>();
    private final ActionCard card;

    public CustomActionCardRecycleViewAdapter(ActionCard card) {
        this.card = card;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
        PinView pinView;
        if (pin.isOut()) {
            pinView = new PinRightCustomView(card.getContext(), card, pin);
        } else {
            pinView = new PinLeftCustomView(card.getContext(), card, pin);
        }
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

    public void swapPin(int from, int to) {
        Pin fromPin = pinViews.get(from).getPin();
        Pin toPin = pinViews.get(to).getPin();

        Collections.swap(pinViews, from, to);

        List<Pin> pins = card.getAction().getPins();
        int i = pins.indexOf(fromPin);
        int j = pins.indexOf(toPin);
        Collections.swap(pins, i, j);

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
        }
    }

    public static class DragViewHolderHelper extends ItemTouchHelper.SimpleCallback {
        private final CustomActionCardRecycleViewAdapter adapter;

        public DragViewHolderHelper(CustomActionCardRecycleViewAdapter adapter) {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
            adapter.swapPin(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }
}
