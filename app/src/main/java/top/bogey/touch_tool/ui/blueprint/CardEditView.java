package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.ShapeAppearanceModel;

import top.bogey.touch_tool.databinding.ViewCardEditBinding;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.utils.DisplayUtil;

@SuppressLint("ViewConstructor")
public class CardEditView extends MaterialCardView {
    private final ViewCardEditBinding binding;
    private final CardLayoutView cardLayout;
    private boolean needDelete = false;

    public CardEditView(@NonNull Context context, CardLayoutView cardLayout) {
        super(context);
        this.cardLayout = cardLayout;

        setElevation(DisplayUtil.dp2px(context, 3));
        setShapeAppearanceModel(ShapeAppearanceModel.builder()
                .setAllCornerSizes(DisplayUtil.dp2px(context, 16))
                .build());
        setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant));
        setStrokeWidth(0);

        setVisibility(INVISIBLE);
        setPivotX(0);
        setPivotY(0);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        binding = ViewCardEditBinding.inflate(LayoutInflater.from(context), this, true);

        binding.copyButton.setOnClickListener(v -> {

        });

        binding.deleteButton.setOnClickListener(v -> {
            if (needDelete) {
                for (ActionCard card : cardLayout.selectedCards) {
                    cardLayout.removeCard(card);
                }
                cardLayout.selectedCards.clear();
                setVisibility(INVISIBLE);
            } else {
                binding.deleteButton.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    binding.deleteButton.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });
    }
}
