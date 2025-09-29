package top.bogey.touch_tool.ui.tool.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.log.ActionLog;
import top.bogey.touch_tool.bean.save.log.DateTimeLog;
import top.bogey.touch_tool.bean.save.log.Log;
import top.bogey.touch_tool.bean.save.log.LogInfo;
import top.bogey.touch_tool.bean.save.log.LogSave;
import top.bogey.touch_tool.bean.save.log.NormalLog;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatLogActionItemBinding;
import top.bogey.touch_tool.databinding.FloatLogDateTimeItemBinding;
import top.bogey.touch_tool.databinding.FloatLogNormalItemBinding;
import top.bogey.touch_tool.ui.blueprint.BlueprintView;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.tree.LazyTreeNode;
import top.bogey.touch_tool.utils.tree.TreeAdapter;
import top.bogey.touch_tool.utils.tree.TreeNode;

public class LogFloatViewAdapter extends TreeAdapter {
    private Task task;
    private int searchIndex = -1;

    @NonNull
    @Override
    public TreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return switch (viewType) {
            case 1 -> new ViewHolder(FloatLogActionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case 2 -> new ViewHolder(FloatLogDateTimeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default -> new ViewHolder(FloatLogNormalItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        };
    }

    @Override
    public int getItemViewType(int position) {
        TreeNode treeNode = treeNodes.get(position);
        LogInfo logInfo = (LogInfo) treeNode.getData();
        if (logInfo == null) return 0;
        if (logInfo.getLogObject() instanceof ActionLog) return 1;
        if (logInfo.getLogObject() instanceof DateTimeLog) return 2;
        return 0;
    }

    public void setLogSave(LogSave logSave) {
        task = Saver.getInstance().getTask(logSave.getKey());

        List<TreeNode> nodeList = new ArrayList<>();
        for (int i = 1; i < logSave.getLogCount() + 1; i++) {
            TreeNode node = new LazyTreeNode(logSave, i);
            nodeList.add(node);
        }
        setTreeNodes(nodeList);
    }

    public List<LogInfo> getLogs() {
        return getLogs(treeNodes);
    }

    private List<LogInfo> getLogs(List<TreeNode> treeNodes) {
        List<LogInfo> logs = new ArrayList<>();
        for (TreeNode treeNode : treeNodes) {
            LazyTreeNode node = (LazyTreeNode) treeNode;
            logs.add((LogInfo) node.getData());
            logs.addAll(getLogs(node.getChildren()));
        }
        return logs;
    }

    public void addLog(LogSave logSave, LogInfo log) {
        TreeNode node = new LazyTreeNode(logSave, log.getUid());
        addTreeNode(node);
    }

    public int searchLog(String text, Boolean isNext) {
        if (text.isEmpty()) {
            searchIndex = -1;
            return searchIndex;
        }
        if (isNext == null) {
            searchIndex = -1;
            isNext = false;
        }
        List<TreeNode> subList;
        if (searchIndex < 1 || searchIndex >= treeNodes.size()) {
            subList = treeNodes;
        } else {
            if (isNext) {
                subList = treeNodes.subList(searchIndex + 1, treeNodes.size());
            } else {
                subList = treeNodes.subList(0, searchIndex - 1);
            }
        }

        if (searchIndex >= 0 && searchIndex < treeNodes.size() - 1) {
            int index = searchIndex;
            searchIndex = -1;
            notifyItemChanged(index);
        }

        TreeNode treeNode = findTreeNode(subList, text, AppUtil.getPattern(text), isNext);
        if (treeNode != null) {
            expandNode(treeNode);
            searchIndex = treeNodes.indexOf(treeNode);
            notifyItemChanged(searchIndex);
        }
        return searchIndex;
    }

    private TreeNode findTreeNode(List<TreeNode> treeNodes, String text, Pattern pattern, boolean isNext) {
        if (isNext) {
            for (int i = 0; i < treeNodes.size(); i++) {
                TreeNode treeNode = treeNodes.get(i);

                LogInfo logInfo = (LogInfo) treeNode.getData();
                if (logInfo == null) continue;
                if (pattern == null) {
                    if (logInfo.getLog().contains(text)) return treeNode;
                } else {
                    if (pattern.matcher(logInfo.getLog()).find()) return treeNode;
                }
                Log log = logInfo.getLogObject();
                if (log instanceof ActionLog actionLog) {
                    Action action = task.getAction(actionLog.getActionId());
                    if (action == null) continue;
                    if (pattern == null) {
                        if (action.getFullDescription().contains(text)) return treeNode;
                    } else {
                        if (pattern.matcher(action.getFullDescription()).find()) return treeNode;
                    }
                }

                if (!treeNode.isExpanded()) {
                    TreeNode tree = findTreeNode(treeNode.getChildren(), text, pattern, true);
                    if (tree != null) return tree;
                }
            }
        } else {
            for (int i = treeNodes.size() - 1; i >= 0; i--) {
                TreeNode treeNode = treeNodes.get(i);

                if (!treeNode.isExpanded()) {
                    TreeNode tree = findTreeNode(treeNode.getChildren(), text, pattern, false);
                    if (tree != null) return tree;
                }

                LogInfo logInfo = (LogInfo) treeNode.getData();
                if (logInfo == null) continue;
                if (pattern == null) {
                    if (logInfo.getLog().contains(text)) return treeNode;
                } else {
                    if (pattern.matcher(logInfo.getLog()).find()) return treeNode;
                }
                Log log = logInfo.getLogObject();
                if (log instanceof ActionLog actionLog) {
                    Action action = task.getAction(actionLog.getActionId());
                    if (action == null) continue;
                    if (pattern == null) {
                        if (action.getFullDescription().contains(text)) return treeNode;
                    } else {
                        if (pattern.matcher(action.getFullDescription()).find()) return treeNode;
                    }
                }
            }
        }

        return null;
    }

    public class ViewHolder extends TreeAdapter.ViewHolder {
        private final Context context;
        private FloatLogNormalItemBinding normalBinding;
        private FloatLogActionItemBinding actionBinding;
        private FloatLogDateTimeItemBinding dateTimeBinding;

        public ViewHolder(@NonNull FloatLogNormalItemBinding binding) {
            super(LogFloatViewAdapter.this, binding.getRoot());
            context = binding.getRoot().getContext();
            this.normalBinding = binding;

            binding.copyButton.setOnClickListener(v -> {
                LogInfo logInfo = (LogInfo) node.getData();
                if (logInfo == null) return;
                AppUtil.copyToClipboard(context, logInfo.getLog());
            });
        }

        public ViewHolder(@NonNull FloatLogActionItemBinding binding) {
            super(LogFloatViewAdapter.this, binding.getRoot());
            context = binding.getRoot().getContext();
            this.actionBinding = binding;

            binding.gotoButton.setOnClickListener(v -> {
                if (node == null) return;
                LogInfo logInfo = (LogInfo) node.getData();
                if (logInfo == null) return;
                Log log = logInfo.getLogObject();
                if (log instanceof ActionLog actionLog) {
                    Action action = null;
                    Task currTask = Saver.getInstance().getTask(task, actionLog.getTaskId());
                    if (currTask != null) {
                        action = currTask.getAction(actionLog.getActionId());
                    }
                    BlueprintView.tryFocusAction(currTask, action);
                    if (searchIndex != -1) notifyItemChanged(searchIndex);
                    searchIndex = getAdapterPosition();
                    notifyItemChanged(searchIndex);
                }
            });

            binding.expandButton.setOnClickListener(v -> switchNodeExpand(node));
        }

        public ViewHolder(@NonNull FloatLogDateTimeItemBinding binding) {
            super(LogFloatViewAdapter.this, binding.getRoot());
            context = binding.getRoot().getContext();
            this.dateTimeBinding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void refresh(TreeNode node) {
            super.refresh(node);

            LogInfo logInfo = (LogInfo) node.getData();
            if (logInfo == null) return;

            Log log = logInfo.getLogObject();
            this.node = node;

            if (log instanceof NormalLog normalLog && normalBinding != null) {
                normalBinding.title.setText(normalLog.getLog());
                normalBinding.time.setText(logInfo.getTime(context));
            }

            if (log instanceof ActionLog actionLog && actionBinding != null) {
                Action action = null;
                Task currTask = Saver.getInstance().getTask(task, actionLog.getTaskId());
                if (currTask != null) {
                    action = currTask.getAction(actionLog.getActionId());
                }
                actionBinding.gotoButton.setVisibility(action != null ? View.VISIBLE : View.GONE);
                int size = logInfo.getChildrenFlags().size();
                actionBinding.expandButton.setVisibility(size > 0 ? View.VISIBLE : View.INVISIBLE);
                actionBinding.expandButton.setIconResource(node.isExpanded() ? R.drawable.icon_keyboard_arrow_down : R.drawable.icon_keyboard_arrow_right);
                actionBinding.title.setText(actionLog.getLog());
                actionBinding.time.setText(logInfo.getTime(context));
                actionBinding.icon.setImageResource(actionLog.isExecute() ? R.drawable.icon_shuffle : R.drawable.icon_equal);
            }

            if (log instanceof DateTimeLog dateTimeLog && dateTimeBinding != null) {
                dateTimeBinding.title.setText(dateTimeLog.getLog());
            }

            if (itemView instanceof MaterialCardView cardView) {
                if (searchIndex == getAdapterPosition()) {
                    cardView.setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorTertiaryContainer));
                } else {
                    cardView.setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceContainerHighest));
                }
            }
        }
    }
}
