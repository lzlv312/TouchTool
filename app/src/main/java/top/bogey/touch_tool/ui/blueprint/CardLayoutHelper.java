package top.bogey.touch_tool.ui.blueprint;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_execute.PinExecute;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;

public class CardLayoutHelper {
    public static final int CORNER_OFFSET_SCALE = 2;

    public static Path calculateLinkPath(float gridSize, PointF start, PointF end, boolean vertical) {
        Path path = new Path();
        path.moveTo(start.x, start.y);

        float gridSizeOffset = gridSize / 8;
        float dx = Math.abs(start.x - end.x);
        float dy = Math.abs(start.y - end.y);
        if (Math.max(dx, dy) <= gridSize * CORNER_OFFSET_SCALE * 2 + gridSizeOffset) {
            path.lineTo(end.x, end.y);
            return path;
        }

        PointF startPoint = new PointF(start.x, start.y);
        PointF endPoint = new PointF(end.x, end.y);
        if (vertical) {
            startPoint.y += gridSize * CORNER_OFFSET_SCALE;
            endPoint.y -= gridSize * CORNER_OFFSET_SCALE;
        } else {
            startPoint.x += gridSize * CORNER_OFFSET_SCALE;
            endPoint.x -= gridSize * CORNER_OFFSET_SCALE;
        }

        boolean isXPositive = startPoint.x < endPoint.x;
        boolean isYPositive = startPoint.y < endPoint.y;
        int xScale = isXPositive ? 1 : -1;
        int yScale = isYPositive ? 1 : -1;

        dx = Math.abs(endPoint.x - startPoint.x);
        dy = Math.abs(endPoint.y - startPoint.y);
        boolean xLong = dx > dy;
        float halfLen = Math.abs(dx - dy) / 2;

        path.lineTo(startPoint.x, startPoint.y);
        /*
        vertical:
            xLong:
                isYPositive: ↓ ← ↓, ↓ → ↓
                ! isYPositive: ← ↖ ←, → ↗ →
            ! xLong:
                isYPositive: ↓ ↙ ↓, ↓ ↘ ↓
                ! isYPositive: ← ↑ ←, → ↑ →
            dx < gridSize / 2:
                isYPositive: ↓
                ! isYPositive: ← ↑ →
        ! vertical:
            xLong:
                isXPositive:  → ↗ →, → ↘ →
                ! isXPositive: ↑ ← ↑, ↓ ← ↓
            ! xLong:
                isXPositive:  → ↑ →, → ↓ →
                ! isXPositive: ↑ ↖ ↑, ↓ ↙ ↓
            dy < gridSize / 2:
                isXPositive:  →
                ! isXPositive: ↓ ← ↑
        */

        boolean flag = true;
        if (vertical) {
            if (!isYPositive) {
                if (dx < gridSize * (2 + CORNER_OFFSET_SCALE) - gridSizeOffset) { //← ↑ →
                    float x = Math.min(endPoint.x, startPoint.x) - gridSize * (CORNER_OFFSET_SCALE + 2);
                    path.lineTo(x, startPoint.y);
                    path.lineTo(x, endPoint.y);
                    flag = false;
                }
            }

            if (flag) {
                if (xLong) {
                    if (isYPositive) {  //↓ ← ↓, ↓ → ↓
                        path.lineTo(startPoint.x, startPoint.y + dy / 2);
                        path.lineTo(endPoint.x, endPoint.y - dy / 2);
                    } else {            //← ↖ ←, → ↗ →
                        path.lineTo(startPoint.x + halfLen * xScale, startPoint.y);
                        path.lineTo(endPoint.x - halfLen * xScale, endPoint.y);
                    }
                } else {
                    if (isYPositive) {  //↓ ↙ ↓, ↓ ↘ ↓
                        path.lineTo(startPoint.x, startPoint.y + halfLen);
                        path.lineTo(endPoint.x, endPoint.y - halfLen);
                    } else {            //← ↑ ←, → ↑ →
                        path.lineTo(startPoint.x + dx * xScale / 2, startPoint.y);
                        path.lineTo(endPoint.x - dx * xScale / 2, endPoint.y);
                    }
                }
            }
        } else {
            if (!isXPositive) {
                if (dy < gridSize * (2 + CORNER_OFFSET_SCALE) - gridSizeOffset) { //↓ ← ↑
                    float y = Math.max(endPoint.y, startPoint.y) + gridSize * (CORNER_OFFSET_SCALE + 4);
                    path.lineTo(startPoint.x, y);
                    path.lineTo(endPoint.x, y);
                    flag = false;
                }
            }

            if (flag) {
                if (xLong) {
                    if (isXPositive) {  //→ ↗ →, → ↘ →
                        path.lineTo(startPoint.x + halfLen, startPoint.y);
                        path.lineTo(endPoint.x - halfLen, endPoint.y);
                    } else {            //↑ ← ↑, ↓ ← ↓
                        path.lineTo(startPoint.x, startPoint.y + dy * yScale / 2);
                        path.lineTo(endPoint.x, endPoint.y - dy * yScale / 2);
                    }
                } else {
                    if (isXPositive) {  //→ ↑ →, → ↓ →
                        path.lineTo(startPoint.x + dx / 2, startPoint.y);
                        path.lineTo(endPoint.x - dx / 2, endPoint.y);
                    } else {            //↑ ↖ ↑, ↓ ↙ ↓
                        path.lineTo(startPoint.x, startPoint.y + halfLen * yScale);
                        path.lineTo(endPoint.x, endPoint.y - halfLen * yScale);
                    }
                }
            }
        }

        path.lineTo(endPoint.x, endPoint.y);
        path.lineTo(end.x, end.y);

        return path;
    }

    public static class ActionArea {
        private final static int SCALE = (CORNER_OFFSET_SCALE + 2) * 2;

        public Action action;
        public boolean execute;
        public RectF actionArea = new RectF();
        public RectF commonParamsArea;
        public RectF allArea = new RectF();

        public List<ActionArea> executes = new ArrayList<>();
        public List<ActionArea> params = new ArrayList<>();

        private float gridSize;

        public ActionArea(CardLayoutView cardLayoutView, List<Action> handledActions, Action action, RectF commonParamsArea, boolean execute) {
            this.action = action;
            this.execute = execute;
            this.commonParamsArea = commonParamsArea;

            gridSize = cardLayoutView.getGridSize();
            float scaleGridSize = gridSize * SCALE;

            ActionCard card = cardLayoutView.getActionCard(action);
            actionArea = new RectF(0, 0, formatToGridPx(card.getWidth()), formatToGridPx(card.getHeight()));

            Task task = cardLayoutView.getTask();
            List<Action> executes = new ArrayList<>();
            List<Action> params = new ArrayList<>();
            action.getPins().forEach(pin -> {
                if (!pin.isLinked()) return;
                boolean isExecute = pin.getValue() instanceof PinExecute;
                if (isExecute && !pin.isOut()) return;
                if (pin.isOut() && !(isExecute)) return;

                Pin linkedPin = pin.getLinkedPin(task);
                if (linkedPin == null) return;

                Action linkedAction = task.getAction(linkedPin.getOwnerId());
                if (linkedAction == null) return;

                if (handledActions.contains(linkedAction)) return;
                handledActions.add(linkedAction);

                if (isExecute) {
                    executes.add(linkedAction);
                } else {
                    params.add(linkedAction);
                }
            });

            RectF paramsArea = new RectF();
            for (Action paramAction : params) {
                ActionArea area = new ActionArea(cardLayoutView, handledActions, paramAction, new RectF(), false);
                this.params.add(area);
                paramsArea.right = Math.max(paramsArea.width(), area.allArea.width());
                paramsArea.bottom += area.allArea.height() + gridSize;
            }

            if (!paramsArea.isEmpty()) {
                float offset = paramsArea.width() + scaleGridSize;
                actionArea.offset(offset, 0);
                commonParamsArea.right = Math.max(commonParamsArea.right, offset);
            }

            allArea.union(paramsArea);
            allArea.union(actionArea);

            for (int i = 0, executesSize = executes.size(); i < executesSize; i++) {
                Action executeAction = executes.get(i);
                ActionArea area = new ActionArea(cardLayoutView, handledActions, executeAction, i == 0 ? commonParamsArea : new RectF(),  true);
                this.executes.add(area);
            }
        }

        public ActionArea(CardLayoutView cardLayoutView, List<Action> handledActions, List<Action> startActions) {
            gridSize = cardLayoutView.getGridSize();

            for (Action action : startActions) {
                if (handledActions.contains(action)) return;
                handledActions.add(action);

                ActionArea area = new ActionArea(cardLayoutView, handledActions, action, new RectF(), true);
                executes.add(area);
            }
            commonParamsArea = new RectF();
        }

        public int arrange(CardLayoutView cardLayoutView, Point start, RectF parentCommonParamsArea) {
            gridSize = cardLayoutView.getGridSize();
            float scaleGridSize = gridSize * SCALE;

            if (action != null) {
                int actionStartX;
                if (execute) {
                    actionStartX = (int) (start.x + commonParamsArea.width());
                } else {
                    actionStartX = (int) (start.x + parentCommonParamsArea.width() - scaleGridSize - actionArea.width());
                }
                int actionStartY = start.y;
                int x = formatToGrid(actionStartX);
                int y = formatToGrid(actionStartY);
                action.setPos(x, y);
            }

            int executeWidth = (int) (actionArea.width());

            int areaTotalExecuteWidth = 0;
            int executeStartX = start.x;
            int executeStartY = (int) (start.y + allArea.height() + (action == null ? 0 : scaleGridSize));
            for (int i = 0; i < executes.size(); i++) {
                ActionArea area = executes.get(i);
                int areaExecuteWidth = area.arrange(cardLayoutView, new Point(executeStartX, executeStartY), commonParamsArea);
                executeStartX += (int) (scaleGridSize + area.commonParamsArea.width() + areaExecuteWidth);
                if (i == 0) areaTotalExecuteWidth += areaExecuteWidth;
                else areaTotalExecuteWidth += (int) (scaleGridSize + area.commonParamsArea.width() + areaExecuteWidth);
            }

            int paramStartX = start.x;
            int paramStartY = start.y;
            for (ActionArea area : params) {
                area.arrange(cardLayoutView, new Point(paramStartX, paramStartY), commonParamsArea);
                paramStartY += (int) (gridSize + area.allArea.height());
            }

            return Math.max(executeWidth, areaTotalExecuteWidth);
        }

        private int formatToGridPx(float size) {
            return (int) (Math.ceil(size / gridSize) * gridSize);
        }

        private int formatToGrid(float size) {
            return (int) (size / gridSize);
        }
    }
}
