package top.bogey.touch_tool.ui.tool.log;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.log.ActionLog;
import top.bogey.touch_tool.bean.save.log.Log;
import top.bogey.touch_tool.bean.save.log.LogInfo;
import top.bogey.touch_tool.bean.save.log.LogSave;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.FloatLogItemBinding;
import top.bogey.touch_tool.ui.blueprint.BlueprintView;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.tree.LazyTreeNode;
import top.bogey.touch_tool.utils.tree.TreeAdapter;
import top.bogey.touch_tool.utils.tree.TreeNode;

public class LogViewAdapter extends TreeAdapter {
    private Task task;
    private int searchIndex = -1;

    @NonNull
    @Override
    public TreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FloatLogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        private final FloatLogItemBinding binding;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull FloatLogItemBinding binding) {
            super(LogViewAdapter.this, binding.getRoot());
            this.binding = binding;

            binding.gotoButton.setOnClickListener(v -> {
                if (node == null) return;
                LogInfo logInfo = (LogInfo) node.getData();
                if (logInfo == null) return;
                Log log = logInfo.getLogObject();
                if (log instanceof ActionLog actionLog) {
                    BlueprintView.tryFocusAction(task.getAction(actionLog.getActionId()));
                    if (searchIndex != -1) notifyItemChanged(searchIndex);
                    searchIndex = getAdapterPosition();
                    notifyItemChanged(searchIndex);
                }
            });

            binding.copyButton.setOnClickListener(v -> {
                LogInfo logInfo = (LogInfo) node.getData();
                if (logInfo == null) return;
                Log log = logInfo.getLogObject();
                if (!(log instanceof ActionLog actionLog) || actionLog.getIndex() == -1) {
                    AppUtil.copyToClipboard(context, logInfo.getLog());
                } else {
                    switchNodeExpand(node);
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void refresh(TreeNode node) {
            super.refresh(node);
            this.node = null;
            binding.title.setText(null);
            binding.time.setText(null);
            binding.icon.setVisibility(View.GONE);
            binding.copyButton.setVisibility(View.INVISIBLE);
            binding.gotoButton.setVisibility(View.GONE);

            LogInfo logInfo = (LogInfo) node.getData();
            if (logInfo == null) return;
            Log log = logInfo.getLogObject();
            Action action = null;
            ActionLog actionLog = null;
            if (log instanceof ActionLog) {
                actionLog = (ActionLog) log;
                action = task.getAction(actionLog.getActionId());
            }
            this.node = node;

            binding.icon.setVisibility(View.VISIBLE);
            binding.gotoButton.setVisibility(View.VISIBLE);

            if (actionLog == null || actionLog.getIndex() == -1) {
                binding.copyButton.setIconResource(R.drawable.icon_content_copy);
                binding.copyButton.setVisibility(View.VISIBLE);
                binding.title.setText(":" + logInfo.getLog());
            } else {
                binding.copyButton.setIconResource(node.isExpanded() ? R.drawable.icon_keyboard_arrow_down : R.drawable.icon_keyboard_arrow_right);
                int size = logInfo.getChildrenFlags().size();
                binding.copyButton.setVisibility(size == 0 ? View.INVISIBLE : View.VISIBLE);
                binding.title.setText(logInfo.getLog());
            }

            binding.gotoButton.setVisibility(action == null ? View.GONE : View.VISIBLE);
            binding.time.setText(logInfo.getTime(context));
            binding.icon.setImageResource(actionLog != null && actionLog.isExecute() ? R.drawable.icon_shuffle : R.drawable.icon_equal);

            if (searchIndex == getAdapterPosition()) {
                binding.getRoot().setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorTertiaryContainer));
            } else {
                binding.getRoot().setCardBackgroundColor(DisplayUtil.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceContainerHighest));
            }
        }
    }
}
