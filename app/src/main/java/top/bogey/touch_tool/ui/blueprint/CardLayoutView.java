package top.bogey.touch_tool.ui.blueprint;

import static top.bogey.touch_tool.ui.blueprint.CardLayoutHelper.CORNER_OFFSET_SCALE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.SyncAction;
import top.bogey.touch_tool.bean.action.task.CustomEndAction;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.task.TaskSaveListener;
import top.bogey.touch_tool.bean.save.variable.VariableSaveListener;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.history.HistoryManager;
import top.bogey.touch_tool.ui.blueprint.history.HistoryStep;
import top.bogey.touch_tool.ui.blueprint.history.IHistoryOwner;
import top.bogey.touch_tool.ui.blueprint.history.edit.CardAddHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.CardMoveHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.CardRemoveHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.CardUpdateHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.EditHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.PinAddHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.PinLinkHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.PinRemoveHistory;
import top.bogey.touch_tool.ui.blueprint.history.edit.PinUpdateHistory;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionByPinDialog;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;

public class CardLayoutView extends FrameLayout implements TaskSaveListener, VariableSaveListener, IHistoryOwner {
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_BACKGROUND = 1;
    private static final int TOUCH_CARD = 2;
    private static final int TOUCH_PIN = 3;

    private static final int TOUCH_SCALE = 4;
    private static final int TOUCH_SELECT_AREA = 5;

    private static final int TOUCH_DRAG_BACKGROUND = 6;
    private static final int TOUCH_DRAG_CARD = 7;
    private static final int TOUCH_DRAG_PIN = 8;
    private static final int TOUCH_DRAG_LINK = 9;

    private static final long LONG_TOUCH_TIME = 300L;

    private final Handler longTouchHandler;

    private final float gridSize;
    private final Paint gridPaint;
    private final Paint linkPaint;

    private final ScaleGestureDetector detector;

    private int touchState = TOUCH_NONE;
    final Set<ActionCard> selectedCards = new HashSet<>();
    private ActionCard touchedCard;

    private final Map<String, String> selectedLinks = new HashMap<>();
    private PinView touchedPin;

    private RectF selectArea = new RectF();
    private SelectActionDialog actionDialog = null;

    private float startX, startY, realStartX, realStartY;
    private float lastX, lastY, realLastX, realLastY;
    private boolean movingTouch = false;

    private float offsetX, offsetY;
    private float scale = 1f;

    private Task task;
    private HistoryManager history;
    private final Map<String, ActionCard> cards = new HashMap<>();

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setSaveEnabled(false);
        setSaveFromParentEnabled(false);

        longTouchHandler = new Handler();

        gridSize = DisplayUtil.dp2px(context, 8);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryVariant));
        gridPaint.setAlpha(50);

        linkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linkPaint.setStyle(Paint.Style.STROKE);
        linkPaint.setStrokeCap(Paint.Cap.ROUND);
        linkPaint.setStrokeJoin(Paint.Join.ROUND);

        detector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float oldScale = scale;
                scale *= detector.getScaleFactor();
                scale = Math.max(0.3f, Math.min(scale, 2f));

                float v = 1 - scale / oldScale;
                float focusX = detector.getFocusX() - offsetX;
                float focusY = detector.getFocusY() - offsetY;

                offsetX += focusX * v;
                offsetY += focusY * v;

                updateCardsPos();
                invalidate();
                return true;
            }

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                touchState = TOUCH_SCALE;
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                touchState = TOUCH_NONE;
            }
        });
        detector.setQuickScaleEnabled(!AppUtil.isRelease(context));

        Saver.getInstance().addListener((TaskSaveListener) this);
        Saver.getInstance().addListener((VariableSaveListener) this);
    }

    public void setTask(Task task, HistoryManager history) {
        this.task = task;
        this.history = history;
        offsetX = 0;
        offsetY = 0;
        scale = 1;
        cleanSelectedCards();

        cards.values().forEach(this::removeView);
        cards.clear();

        task.getActions().forEach(action -> {
            if (action instanceof SyncAction syncAction) syncAction.sync(task);
            ActionCard card = newCard(action);
            cards.put(action.getId(), card);
            addView(card);
        });
        checkCards();
        invalidate();
        updateCardsPos();
    }

    public ActionCard newCard(Action action) {
        ActionInfo info = ActionInfo.getActionInfo(action.getType());
        try {
            assert info != null;
            Constructor<? extends ActionCard> constructor = info.getCardClass().getConstructor(Context.class, Task.class, Action.class);
            return constructor.newInstance(getContext(), task, action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 添加卡片
    public ActionCard addCard(Action action) {
        if (action instanceof CustomStartAction) {
            List<Action> actions = task.getActions(action.getClass());
            if (!actions.isEmpty()) {
                Toast.makeText(getContext(), R.string.custom_action_single_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (action instanceof SyncAction syncAction) {
            // 自定义结束动作需要多卡同步
            if (action instanceof CustomEndAction) {
                List<Action> actions = task.getActions(action.getClass());
                if (!actions.isEmpty()) {
                    Action first = actions.get(0);
                    syncAction = (SyncAction) first.newCopy();
                }
            }
            syncAction.sync(task);
        }

        task.addAction(action);
        ActionCard card = newCard(action);
        cards.put(action.getId(), card);
        addView(card);
        updateCardPos(card);
        return card;
    }

    public void removeCard(ActionCard card) {
        Action action = card.getAction();
        action.getPins().forEach(pin -> pin.clearLinks(task));
        task.removeAction(action.getId());
        cards.remove(action.getId());
        removeView(card);

        if (selectedCards.remove(card)) refreshEditView();
    }

    public void initCardPos(ActionCard card) {
        card.measure(0, 0);
        float width = card.getMeasuredWidth() * scale;
        float height = card.getMeasuredHeight() * scale;
        float x, y;
        if (lastX <= 0 && lastY <= 0) {
            x = (getWidth() - width) / 2f;
            y = (getHeight() - height) / 5f;
        } else {
            x = lastX - width / 2f;
            y = lastY - height / 2f;
        }
        card.getAction().setPos((int) ((x - offsetX) / getScaleGridSize()), (int) ((y - offsetY) / getScaleGridSize()));
        updateCardPos(card);
    }

    private void updateCardPos(ActionCard card) {
        card.setScaleX(scale);
        card.setScaleY(scale);
        Action action = card.getAction();
        Point pos = action.getPos();
        float x = pos.x * getScaleGridSize() + offsetX;
        float y = pos.y * getScaleGridSize() + offsetY;
        card.updateCardPos(x, y);

        RectF area = getCardArea(card);
        area.offset(offsetX, offsetY);

        RectF windowSize = new RectF(0, 0, getWidth(), getHeight());
        boolean intersects = RectF.intersects(windowSize, area);
        boolean contains = windowSize.contains(area);
        card.setVisibility(intersects || contains ? VISIBLE : INVISIBLE);
    }

    public void updateCardsPos() {
        cards.values().forEach(this::updateCardPos);
    }

    public ActionCard getActionCard(Action action) {
        return cards.get(action.getId());
    }

    private ActionCard getActionCard(float x, float y, boolean checkLock) {
        List<ActionCard> cards = new ArrayList<>(this.cards.values());
        cards.sort((o1, o2) -> indexOfChild(o2) - indexOfChild(o1));
        for (ActionCard card : cards) {
            if (checkLock && card.getAction().isLocked()) continue;
            RectF area = getCardArea(card);
            if (area.contains(x, y)) return card;
        }
        return null;
    }

    public void focusCard(String actionId) {
        focusCard(cards.get(actionId));
    }

    public void focusCard(ActionCard card) {
        if (card == null) return;

        cleanSelectedCards();
        addSelectedCard(card);
        Action action = card.getAction();
        Point pos = action.getPos();
        float x = -pos.x * getScaleGridSize() + (getWidth() - card.getWidth() * scale) / 2f;
        float y = -pos.y * getScaleGridSize() + (getHeight() - card.getHeight() * scale) / 3f;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            offsetX += (x - offsetX) * value;
            offsetY += (y - offsetY) * value;
            updateCardsPos();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.startFocusAnim();
            }
        });
        animator.start();
    }

    private void addSelectedCard(ActionCard card) {
        selectedCards.add(card);
        card.setSelected(true);
        refreshEditView();
    }

    private void cleanSelectedCards() {
        selectedCards.forEach(card -> card.setSelected(false));
        selectedCards.clear();
        refreshEditView();
    }

    private void refreshEditView() {
        BlueprintView.tryShowFloatingToolBar(selectedCards.size() > 1);
    }

    public void refreshPinView() {
        cards.forEach((id, card) -> card.getPinViews().forEach((pinId, pinView) -> pinView.refreshCopyButton()));
    }

    private RectF getCardArea(ActionCard card) {
        float gridSize = getScaleGridSize();
        Point cardPos = card.getAction().getPos();
        float cardX = cardPos.x * gridSize;
        float cardY = cardPos.y * gridSize;
        float cardWidth = card.getWidth() * scale;
        float cardHeight = card.getHeight() * scale;
        return new RectF(cardX, cardY, cardX + cardWidth, cardY + cardHeight);
    }

    private RectF calculateCardsArea(Collection<ActionCard> cards) {
        List<PointF> points = new ArrayList<>();
        cards.forEach(card -> {
            RectF cardArea = getCardArea(card);
            points.add(new PointF(cardArea.left, cardArea.top));
            points.add(new PointF(cardArea.right, cardArea.bottom));
        });
        return DisplayUtil.getPointFsArea(points);
    }

    public float getScaleGridSize() {
        return gridSize * scale;
    }

    public float getGridSize() {
        return gridSize;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float realX = x - offsetX;
        float realY = y - offsetY;

        ActionCard card = getActionCard(realX, realY, false);
        if (card != null) {
            if (card.isEmptyPosition(x - card.getX(), y - card.getY())) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (touchState == TOUCH_SCALE) {
            longTouchHandler.removeCallbacksAndMessages(null);
            return true;
        }

        float gridSize = getScaleGridSize();
        float x = event.getX();
        float y = event.getY();
        float realX = x - offsetX;
        float realY = y - offsetY;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN -> {
                startX = x;
                startY = y;
                realStartX = realX;
                realStartY = realY;

                lastX = x;
                lastY = y;
                realLastX = realX;
                realLastY = realY;

                touchState = TOUCH_BACKGROUND;
                touchedCard = null;
                touchedPin = null;
                selectedLinks.clear();
                movingTouch = false;

                longTouchHandler.removeCallbacksAndMessages(null);

                ActionCard card = getActionCard(realX, realY, true);
                if (card != null) {
                    touchState = TOUCH_CARD;
                    touchedCard = card;

                    card.bringToFront();
                    PinView pinView = card.getLinkAblePinView(x - card.getX(), y - card.getY());
                    if (pinView != null) {
                        touchState = TOUCH_PIN;
                        touchedPin = pinView;
                    }
                }

                switch (touchState) {
                    case TOUCH_BACKGROUND -> longTouchHandler.postDelayed(() -> {
                        touchState = TOUCH_SELECT_AREA;
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    }, LONG_TOUCH_TIME);

                    case TOUCH_PIN -> longTouchHandler.postDelayed(() -> {
                        Pin pin = touchedPin.getPin();
                        if (pin.isLinked()) {
                            touchState = TOUCH_DRAG_LINK;
                            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                            selectedLinks.putAll(pin.getLinks());
                            pin.clearLinks(task);
                        }

                        Pin linkedPin = pin.getLinkedPin(task);
                        if (linkedPin == null) return;
                        Action action = task.getAction(linkedPin.getOwnerId());
                        if (action == null) return;

                        touchState = TOUCH_NONE;
                        focusCard(action.getId());
                    }, LONG_TOUCH_TIME);
                }
            }
            case MotionEvent.ACTION_MOVE -> {
                if (Math.abs(x - startX) > getScaleGridSize() || Math.abs(y - startY) > getScaleGridSize()) movingTouch = true;

                if (movingTouch) {
                    longTouchHandler.removeCallbacksAndMessages(null);

                    switch (touchState) {
                        case TOUCH_BACKGROUND -> touchState = TOUCH_DRAG_BACKGROUND;
                        case TOUCH_CARD -> {
                            touchState = TOUCH_DRAG_CARD;
                            if (!selectedCards.contains(touchedCard)) {
                                cleanSelectedCards();
                                addSelectedCard(touchedCard);
                            }
                        }
                        case TOUCH_PIN -> {
                            touchState = TOUCH_DRAG_PIN;
                            cleanSelectedCards();
                        }
                    }
                }

                // 边界移动
                if (touchState != TOUCH_DRAG_BACKGROUND) sideMove(x, y);

                switch (touchState) {
                    case TOUCH_DRAG_BACKGROUND -> {
                        offsetX += x - lastX;
                        offsetY += y - lastY;
                        lastX = x;
                        lastY = y;
                        updateCardsPos();
                    }

                    case TOUCH_SELECT_AREA -> {
                        cleanSelectedCards();
                        selectArea = new RectF(realStartX, realStartY, x - offsetX, y - offsetY);
                        selectArea.sort();
                        cards.values().forEach(card -> {
                            RectF cardArea = getCardArea(card);
                            if (cardArea.intersect(selectArea)) addSelectedCard(card);
                        });
                    }

                    case TOUCH_DRAG_CARD -> {
                        int dx = (int) ((x - offsetX - realLastX) / gridSize);
                        int dy = (int) ((y - offsetY - realLastY) / gridSize);

                        selectedCards.forEach(card -> {
                            Action action = card.getAction();
                            Point pos = action.getPos();
                            if (dx != 0) pos.x += dx;
                            if (dy != 0) pos.y += dy;
                            updateCardPos(card);
                        });

                        realLastX += dx * gridSize;
                        realLastY += dy * gridSize;
                    }

                    case TOUCH_DRAG_PIN, TOUCH_DRAG_LINK -> {
                        lastX = x;
                        lastY = y;
                    }
                }
            }

            case MotionEvent.ACTION_UP -> {
                longTouchHandler.removeCallbacksAndMessages(null);

                switch (touchState) {
                    case TOUCH_BACKGROUND -> cleanSelectedCards();

                    case TOUCH_CARD -> {
                        boolean contains = selectedCards.contains(touchedCard);
                        cleanSelectedCards();
                        if (!contains) {
                            addSelectedCard(touchedCard);
                        }
                    }

                    case TOUCH_PIN -> {
                        Pin pin = touchedPin.getPin();
                        pin.clearLinks(task);
                    }

                    case TOUCH_DRAG_PIN, TOUCH_DRAG_LINK -> {
                        ActionCard card = getActionCard(realX, realY, false);
                        if (card != null) {
                            boolean flag = true;
                            PinView pinView = card.getLinkAblePinView(x - card.getX(), y - card.getY());
                            if (pinView != null) {
                                Pin pin = pinView.getPin();
                                if (touchState == TOUCH_DRAG_PIN) {
                                    if (pin.linkAble(touchedPin.getPin())) {
                                        pin.mutualAddLink(task, touchedPin.getPin());
                                        flag = false;
                                    }
                                } else {
                                    flag = !pin.addLinks(task, selectedLinks);
                                }
                            }

                            if (flag) tryLink(card.getAction());
                        } else {
                            showActionSelector();
                        }
                    }
                }
                touchState = TOUCH_NONE;
                lastX = 0;
                lastY = 0;
            }
        }
        invalidate();
        return true;
    }

    private void sideMove(float x, float y) {
        float triggerWidth = gridSize * 3;
        float sideMoveX = 0, sideMoveY = 0;
        if (x <= triggerWidth) {
            sideMoveX = 20;
        } else if (x >= getWidth() - triggerWidth) {
            sideMoveX = -20;
        }

        if (y <= triggerWidth) {
            sideMoveY = 20;
        } else if (y >= getHeight() - triggerWidth) {
            sideMoveY = -20;
        }

        offsetX += sideMoveX;
        offsetY += sideMoveY;
        updateCardsPos();
    }

    private void tryLink(Action action) {
        tryLink(touchState, action);
    }

    public void tryLink(int touchState, Action action) {
        if (touchedPin == null) return;
        Pin p = touchedPin.getPin();
        if (touchState == TOUCH_DRAG_PIN) {
            for (Pin pin : action.getPins()) {
                if (pin.linkAble(task) && pin.linkAble(p)) {
                    pin.mutualAddLink(task, p);
                    break;
                }
            }
        } else if (touchState == TOUCH_DRAG_LINK) {
            for (Pin pin : action.getPins()) {
                if (pin.linkAble(task) && pin.linkAble(p.getValue())) {
                    pin.addLinks(task, selectedLinks);
                }
            }
        }
    }

    private void showActionSelector() {
        int touchState = this.touchState;
        float lastX = this.lastX;
        float lastY = this.lastY;

        Pin pin = touchedPin.getPin();
        if (touchState == TOUCH_DRAG_PIN) {
            pin = new Pin(pin.getValue(), 0, pin.isOut());
        } else {
            pin = new Pin(pin.getValue(), 0, !pin.isOut());
        }

        actionDialog = new SelectActionByPinDialog(getContext(), task, pin, action -> {
            ActionCard card = addCard(action);
            if (card != null) {
                tryLink(touchState, action);
                action.setPos((int) ((lastX - offsetX) / getScaleGridSize()), (int) ((lastY - offsetY) / getScaleGridSize()));
                updateCardPos(card);
            }
            if (actionDialog != null) actionDialog.dismiss();
        });
        actionDialog.show();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            updateCardsPos();
        }
    }

    public Bitmap takeTaskCapture() {
        cleanSelectedCards();

        float tmpScale = scale;
        float tmpOffsetX = offsetX;
        float tmpOffsetY = offsetY;

        // 将所有卡片设置为未缩放大小
        scale = 1;
        offsetX = 0;
        offsetY = 0;
        updateCardsPos();
        cards.values().forEach(card -> card.setVisibility(VISIBLE));

        // 计算所有卡片的绘制区域
        RectF area = calculateCardsArea(cards.values());
        area.left -= gridSize * (CORNER_OFFSET_SCALE + 1);
        area.top -= gridSize * (CORNER_OFFSET_SCALE + 1);
        area.right += gridSize * (CORNER_OFFSET_SCALE + 1);
        area.bottom += gridSize * (CORNER_OFFSET_SCALE + 1);

        // 设置偏移
        offsetX = -area.left;
        offsetY = -area.top;
        updateCardsPos();
        cards.values().forEach(card -> card.setVisibility(VISIBLE));

        Bitmap bitmap = Bitmap.createBitmap((int) area.width(), (int) area.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurface));
        dispatchDraw(canvas);

        scale = tmpScale;
        offsetX = tmpOffsetX;
        offsetY = tmpOffsetY;
        updateCardsPos();
        return bitmap;
    }

    /**
     * 绘制背景网格
     *
     * @param canvas   画布
     * @param startX   绝对起始位置
     * @param startY   绝对起始位置
     * @param gridSize 网格大小
     */
    private void drawBackground(Canvas canvas, float startX, float startY, float gridSize) {
        canvas.save();
        float offsetX = startX % gridSize;
        float offsetY = startY % gridSize;
        canvas.translate(offsetX, offsetY);

        startX = startX - offsetX;
        startY = startY - offsetY;

        float row = canvas.getHeight() / gridSize;
        float col = canvas.getWidth() / gridSize;

        float bigGridSize = gridSize * 10;
        for (int i = 0; i < row; i++) {
            float y = i * gridSize;
            if (startY == y) {
                gridPaint.setStrokeWidth(5);
            } else {
                float offset = Math.abs((startY - y) % bigGridSize);
                boolean isBig = offset < 1 || offset > bigGridSize - 1;
                gridPaint.setStrokeWidth(isBig ? 2 : 0.5f);
            }
            canvas.drawLine(-gridSize, y, canvas.getWidth() + gridSize, y, gridPaint);
        }

        for (int i = 0; i < col; i++) {
            float x = i * gridSize;
            if (startX == x) {
                gridPaint.setStrokeWidth(5);
            } else {
                float offset = Math.abs((startX - x) % bigGridSize);
                boolean isBig = offset < 1 || offset > bigGridSize - 1;
                gridPaint.setStrokeWidth(isBig ? 2 : 0.5f);
            }
            canvas.drawLine(x, -gridSize, x, canvas.getHeight() + gridSize, gridPaint);
        }

        canvas.restore();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        float gridSize = getScaleGridSize();
        drawBackground(canvas, offsetX, offsetY, gridSize);

        CornerPathEffect cornerPathEffect = new CornerPathEffect(gridSize * CORNER_OFFSET_SCALE / 2);
        linkPaint.setPathEffect(cornerPathEffect);

        linkPaint.setStrokeWidth(gridSize / 4);
        // 所有卡片的连线，不包括选中的卡，因为选中的卡的连线要置顶
        cards.values().forEach(card -> {
            if (selectedCards.contains(card)) return;

            Action action = card.getAction();
            action.getPins().forEach(pin -> {
                // 输出针脚才需要画线
                if (!pin.isOut()) return;
                PinView cardPinView = card.getPinView(pin.getId());
                if (cardPinView == null) return;

                pin.getLinks().forEach((key, value) -> {
                    ActionCard actionCard = cards.get(value);

                    if (actionCard == null) return;

                    if (selectedCards.contains(actionCard)) return;

                    PinView pinView = actionCard.getPinView(key);
                    if (pinView == null) return;

                    if (pinView.getPin().isSameClass(pin)) {
                        linkPaint.setShader(null);
                        linkPaint.setColor(pinView.getPinColor());
                    } else {
                        PointF startPoint = cardPinView.getSlotPosInLayout(scale);
                        PointF endPoint = pinView.getSlotPosInLayout(scale);
                        linkPaint.setShader(new LinearGradient(startPoint.x, startPoint.y, endPoint.x, endPoint.y, cardPinView.getPinColor(), pinView.getPinColor(), Shader.TileMode.CLAMP));
                        linkPaint.setColor(Color.WHITE);
                    }

                    canvas.drawPath(calculateLinkPath(cardPinView, pinView), linkPaint);
                });
            });
        });

        linkPaint.setStrokeWidth(gridSize / 2);
        linkPaint.setColorFilter(new LightingColorFilter(getResources().getColor(R.color.SelectedPinMul, null), getResources().getColor(R.color.SelectedPinAdd, null)));
        // 选中的卡片
        selectedCards.forEach(card -> {
            Action action = card.getAction();
            action.getPins().forEach(pin -> {
                PinView cardPinView = card.getPinView(pin.getId());
                if (cardPinView == null) return;

                pin.getLinks().forEach((key, value) -> {
                    ActionCard actionCard = cards.get(value);

                    if (actionCard == null) return;

                    PinView pinView = actionCard.getPinView(key);
                    if (pinView == null) return;

                    linkPaint.setColor(pinView.getPinColor());

                    if (pinView.getPin().isSameClass(pin)) {
                        linkPaint.setShader(null);
                        linkPaint.setColor(pinView.getPinColor());
                    } else {
                        PointF startPoint = cardPinView.getSlotPosInLayout(scale);
                        PointF endPoint = pinView.getSlotPosInLayout(scale);
                        linkPaint.setShader(new LinearGradient(startPoint.x, startPoint.y, endPoint.x, endPoint.y, cardPinView.getPinColor(), pinView.getPinColor(), Shader.TileMode.CLAMP));
                        linkPaint.setColor(Color.WHITE);
                    }

                    if (pinView.getPin().isOut()) {
                        canvas.drawPath(calculateLinkPath(pinView, cardPinView), linkPaint);
                    } else {
                        canvas.drawPath(calculateLinkPath(cardPinView, pinView), linkPaint);
                    }
                });
            });
        });

        linkPaint.setShader(null);
        linkPaint.setColorFilter(null);
        linkPaint.setStrokeWidth(gridSize / 4);

        // 拖动针脚，要么连线，要么挪线
        if (touchState == TOUCH_DRAG_PIN || touchState == TOUCH_DRAG_LINK) {
            linkPaint.setColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryInverse));

            PinView currPosPinView = null;
            ActionCard card = getActionCard(lastX, lastY, false);
            if (card != null) {
                currPosPinView = card.getLinkAblePinView(lastX - card.getX(), lastY - card.getY());
            }

            if (touchState == TOUCH_DRAG_PIN) {
                if (currPosPinView != null) {
                    if (touchedPin.getPin().linkAble(currPosPinView.getPin())) {
                        linkPaint.setColor(touchedPin.getPinColor());
                    }
                }
                canvas.drawPath(calculateLinkPath(touchedPin), linkPaint);
            } else {
                if (currPosPinView != null && touchedPin.getPin().isSameClass(currPosPinView.getPin())) {
                    linkPaint.setColor(touchedPin.getPinColor());
                }

                for (Map.Entry<String, String> entry : selectedLinks.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    ActionCard actionCard = cards.get(value);
                    if (actionCard == null) continue;
                    PinView pinView = actionCard.getPinView(key);
                    if (pinView == null) continue;

                    canvas.drawPath(calculateLinkPath(pinView), linkPaint);
                }
            }
        }

        super.dispatchDraw(canvas);

        // 框选
        if (touchState == TOUCH_SELECT_AREA || selectedCards.size() > 1) {
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{gridSize, gridSize}, 0);
            linkPaint.setPathEffect(new ComposePathEffect(cornerPathEffect, dashPathEffect));
            linkPaint.setColor(DisplayUtil.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryVariant));
            RectF area = new RectF();
            if (touchState == TOUCH_SELECT_AREA) {
                area.set(selectArea);
            } else {
                area.set(calculateCardsArea(selectedCards));
                area.left -= gridSize;
                area.top -= gridSize;
                area.right += gridSize;
                area.bottom += gridSize;
            }
            area.offset(offsetX, offsetY);
            canvas.drawRect(area, linkPaint);
        }
    }

    private Path calculateLinkPath(PinView start, PinView end) {
        PointF startPoint = start.getSlotPosInLayout(scale);
        PointF endPoint = end.getSlotPosInLayout(scale);
        return CardLayoutHelper.calculateLinkPath(getScaleGridSize(), startPoint, endPoint, start.getPin().isVertical());
    }

    private Path calculateLinkPath(PinView pinView) {
        PointF pos = pinView.getSlotPosInLayout(scale);
        if (pinView.getPin().isOut()) {
            return CardLayoutHelper.calculateLinkPath(getScaleGridSize(), pos, new PointF(lastX, lastY), pinView.getPin().isVertical());
        } else {
            return CardLayoutHelper.calculateLinkPath(getScaleGridSize(), new PointF(lastX, lastY), pos, pinView.getPin().isVertical());
        }
    }

    public void checkCards() {
        int count = 0;
        for (ActionCard card : cards.values()) {
            boolean result = card.check();
            if (!result) count++;
        }
        if (count == 0) return;
        Toast.makeText(getContext(), getContext().getString(R.string.card_check_error_tips, count), Toast.LENGTH_SHORT).show();
    }

    public void syncCards() {
        for (ActionCard card : cards.values()) {
            Action action = card.getAction();
            if (action instanceof SyncAction syncAction) {
                syncAction.sync(task);
                card.refreshCardInfo();
            }
        }
    }

    public Task getTask() {
        return task;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public void onCreate(Task task) {

    }

    @Override
    public void onUpdate(Task task) {
        syncCards();
        checkCards();
    }

    @Override
    public void onRemove(Task task) {
        checkCards();
    }

    @Override
    public void onCreate(Variable var) {

    }

    @Override
    public void onUpdate(Variable var) {
        syncCards();
    }

    @Override
    public void onRemove(Variable var) {
        checkCards();
    }

    @Override
    public void back(HistoryStep step) {
        List<EditHistory> stepHistory = step.getHistory();
        for (int i = stepHistory.size() - 1; i >= 0; i--) {
            EditHistory editHistory = stepHistory.get(i);
            switch (editHistory.getType()) {
                case ADD_CARD -> {
                    CardAddHistory cardAddHistory = (CardAddHistory) editHistory;
                    Action action = cardAddHistory.getAction();
                    task.removeAction(action.getId());
                    ActionCard card = cards.remove(action.getId());
                    removeView(card);
                }

                case REMOVE_CARD -> {
                    CardRemoveHistory cardRemoveHistory = (CardRemoveHistory) editHistory;
                    Action action = cardRemoveHistory.getAction();
                    addCard(action);
                }

                case UPDATE_CARD -> {
                    CardUpdateHistory cardUpdateHistory = (CardUpdateHistory) editHistory;
                    String actionId = cardUpdateHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) continue;
                    card.setDescription(cardUpdateHistory.getFrom());
                }

                case MOVE_CARD -> {
                    CardMoveHistory cardMoveHistory = (CardMoveHistory) editHistory;
                    String actionId = cardMoveHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) continue;
                    card.getAction().setPos(cardMoveHistory.getStart());
                    updateCardPos(card);
                }

                case ADD_PIN -> {
                    PinAddHistory pinAddHistory = (PinAddHistory) editHistory;
                    String actionId = pinAddHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) continue;
                    card.removePin(pinAddHistory.getPin());
                }

                case REMOVE_PIN -> {
                    PinRemoveHistory pinRemoveHistory = (PinRemoveHistory) editHistory;
                    String actionId = pinRemoveHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    card.addPin(pinRemoveHistory.getPin());
                }

                case UPDATE_PIN -> {
                    PinUpdateHistory pinUpdateHistory = (PinUpdateHistory) editHistory;
                    String actionId = pinUpdateHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    PinView pinView = card.getPinView(pinUpdateHistory.getPinId());
                    if (pinView == null) return;
                    pinView.getPin().setValue(pinUpdateHistory.getFrom());
                    pinView.refreshPin();
                }

                case LINK_PIN -> {
                    PinLinkHistory pinLinkHistory = (PinLinkHistory) editHistory;
                    String actionId = pinLinkHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    PinView pinView = card.getPinView(pinLinkHistory.getPinId());
                    if (pinView == null) return;
                    pinView.getPin().getLinks().clear();
                    pinView.getPin().getLinks().putAll(pinLinkHistory.getFrom());
                    postInvalidate();
                }
            }
        }
    }

    @Override
    public void forward(HistoryStep step) {
        step.getHistory().forEach(editHistory -> {
            switch (editHistory.getType()) {
                case ADD_CARD -> {
                    CardAddHistory cardAddHistory = (CardAddHistory) editHistory;
                    Action action = cardAddHistory.getAction();
                    task.addAction(action);
                    addCard(action);
                }

                case REMOVE_CARD -> {
                    CardRemoveHistory cardRemoveHistory = (CardRemoveHistory) editHistory;
                    Action action = cardRemoveHistory.getAction();
                    task.removeAction(action.getId());
                    ActionCard card = cards.remove(action.getId());
                    removeView(card);
                }

                case UPDATE_CARD -> {
                    CardUpdateHistory cardUpdateHistory = (CardUpdateHistory) editHistory;
                    String actionId = cardUpdateHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    card.setDescription(cardUpdateHistory.getTo());
                }

                case MOVE_CARD -> {
                    CardMoveHistory cardMoveHistory = (CardMoveHistory) editHistory;
                    String actionId = cardMoveHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    card.getAction().setPos(cardMoveHistory.getEnd());
                }

                case ADD_PIN -> {
                    PinAddHistory pinAddHistory = (PinAddHistory) editHistory;
                    String actionId = pinAddHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    card.addPin(pinAddHistory.getPin());
                }

                case REMOVE_PIN -> {
                    PinRemoveHistory pinRemoveHistory = (PinRemoveHistory) editHistory;
                    String actionId = pinRemoveHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    card.removePin(pinRemoveHistory.getPin());
                }

                case UPDATE_PIN -> {
                    PinUpdateHistory pinUpdateHistory = (PinUpdateHistory) editHistory;
                    String actionId = pinUpdateHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    PinView pinView = card.getPinView(pinUpdateHistory.getPinId());
                    if (pinView == null) return;
                    pinView.getPin().setValue(pinUpdateHistory.getTo());
                    pinView.refreshPin();
                }

                case LINK_PIN -> {
                    PinLinkHistory pinLinkHistory = (PinLinkHistory) editHistory;
                    String actionId = pinLinkHistory.getActionId();
                    ActionCard card = cards.get(actionId);
                    if (card == null) return;
                    PinView pinView = card.getPinView(pinLinkHistory.getPinId());
                    if (pinView == null) return;
                    pinView.getPin().getLinks().clear();
                    pinView.getPin().getLinks().putAll(pinLinkHistory.getTo());
                }
            }
        });
    }

}
