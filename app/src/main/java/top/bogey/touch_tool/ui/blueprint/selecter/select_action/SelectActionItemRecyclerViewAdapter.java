package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.action.variable.GetVariableAction;
import top.bogey.touch_tool.bean.action.variable.SetVariableAction;
import top.bogey.touch_tool.bean.other.Usage;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.databinding.DialogSelectActionItemBinding;
import top.bogey.touch_tool.ui.blueprint.BlueprintView;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.custom.EditTaskDialog;
import top.bogey.touch_tool.ui.custom.EditVariableDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.callback.BooleanResultCallback;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.listener.SpinnerSelectedListener;

public class SelectActionItemRecyclerViewAdapter extends RecyclerView.Adapter<SelectActionItemRecyclerViewAdapter.ViewHolder> {

    private final SelectActionDialog dialog;
    private final ResultCallback<Action> callback;

    private List<Object> data = new ArrayList<>();

    public SelectActionItemRecyclerViewAdapter(SelectActionDialog dialog, ResultCallback<Action> callback) {
        this.dialog = dialog;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DialogSelectActionItemBinding binding = DialogSelectActionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refresh(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static String getObjectTitle(Object object) {
        if (object instanceof Task task) {
            return task.getTitle();
        }
        if (object instanceof Variable var) {
            return var.getTitle();
        }
        if (object instanceof ActionType actionType) {
            ActionInfo info = ActionInfo.getActionInfo(actionType);
            if (info != null) {
                return info.getTitle();
            }
        }
        if (object instanceof ActionCard card) return card.getAction().getTitle();
        return "";
    }

    public static String getObjectDesc(Object object) {
        if (object instanceof Task task) return task.getDescription();
        if (object instanceof Variable var) return var.getDescription();
        if (object instanceof ActionType actionType) {
            ActionInfo info = ActionInfo.getActionInfo(actionType);
            if (info != null) {
                return info.getDescription();
            }
        }
        if (object instanceof ActionCard card) return card.getAction().getDescription();
        return "";
    }

    @DrawableRes
    public static int getObjectIcon(Object object) {
        if (object instanceof Task) return R.drawable.icon_assignment;
        if (object instanceof Variable) return R.drawable.icon_note_stack;
        if (object instanceof ActionType actionType) {
            ActionInfo info = ActionInfo.getActionInfo(actionType);
            if (info != null) {
                return info.getIcon();
            }
        }
        if (object instanceof ActionCard card) return getObjectIcon(card.getAction().getType());
        return 0;
    }

    public static String getUsageTitlePath(Usage usage) {
        StringBuilder builder = new StringBuilder();
        Task task = usage.task();
        while (task != null) {
            builder.insert(0, task.getTitle()).insert(0, " > ");
            task = task.getParent();
        }
        builder.append(" (").append(usage.pos().x).append(",").append(usage.pos().y).append(")");
        return builder.toString().substring(1);
    }

    public static LinearLayout getTipsLinearLayout(Context context, List<Usage> usages, @StringRes int tipString) {
        LinearLayout linearLayout = new LinearLayout(context);

        MaterialTextView tips = new MaterialTextView(context);
        tips.setText(context.getString(tipString, usages.size()));
        linearLayout.addView(tips);
        MaterialDivider divider = new MaterialDivider(context);
        DisplayUtil.setViewWidth(divider, ViewGroup.LayoutParams.MATCH_PARENT);
        DisplayUtil.setViewHeight(divider, (int) DisplayUtil.dp2px(context, 1));
        linearLayout.addView(divider);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        usages.forEach(usage -> {
            MaterialTextView title = new MaterialTextView(context);
            title.setText(usage.task().getTitle());
            linearLayout.addView(title);

            MaterialTextView path = new MaterialTextView(context);
            path.setTextSize(11);
            path.setText(getUsageTitlePath(usage));
            linearLayout.addView(path);

            MaterialDivider div = new MaterialDivider(context);
            DisplayUtil.setViewWidth(div, ViewGroup.LayoutParams.MATCH_PARENT);
            DisplayUtil.setViewHeight(div, (int) DisplayUtil.dp2px(context, 1));
            linearLayout.addView(div);
        });
        return linearLayout;
    }

    public void setData(List<Object> data, boolean sort) {
        this.data = data;
        if (sort) AppUtil.chineseSort(data, SelectActionItemRecyclerViewAdapter::getObjectTitle);
        notifyDataSetChanged();
    }

    public void addData(Object object) {
        data.add(object);
        notifyItemInserted(data.size() - 1);
    }

    private void deleteSameObject(Object object) {
        dialog.deleteSameObject(object);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final DialogSelectActionItemBinding binding;
        private final Context context;
        private boolean needDelete = false;

        public ViewHolder(@NonNull DialogSelectActionItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.keySlot.setOnClickListener(v -> {
                SelectActionVariableTypeDialog dialog = new SelectActionVariableTypeDialog(context);
                new MaterialAlertDialogBuilder(context)
                        .setView(dialog)
                        .setPositiveButton(R.string.enter, (view, which) -> {
                            PinInfo pinInfo = dialog.getSelected();
                            binding.keySlot.setText(pinInfo.getTitle());
                            int index = getAdapterPosition();
                            Variable var = (Variable) data.get(index);
                            var.setKeyPinInfo(pinInfo);
                            var.save();
                            notifyItemChanged(index);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });

            binding.typeSpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getAdapterPosition();
                    Variable var = (Variable) data.get(index);
                    if (var.setType(Variable.VariableType.values()[position])) {
                        var.save();
                        notifyItemChanged(index);
                    }
                }
            });

            binding.valueSlot.setOnClickListener(v -> {
                SelectActionVariableTypeDialog dialog = new SelectActionVariableTypeDialog(context);
                new MaterialAlertDialogBuilder(context)
                        .setView(dialog)
                        .setPositiveButton(R.string.enter, (view, which) -> {
                            PinInfo pinInfo = dialog.getSelected();
                            binding.keySlot.setText(pinInfo.getTitle());
                            int index = getAdapterPosition();
                            Variable var = (Variable) data.get(index);
                            var.setValuePinInfo(pinInfo);
                            var.save();
                            notifyItemChanged(index);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });

            binding.editButton.setOnClickListener(v -> {
                int index = getAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Task editTask) {
                    EditTaskDialog dialog = new EditTaskDialog(context, editTask);
                    dialog.setTitle(R.string.task_update);
                    dialog.setCallback(result -> {
                        if (result) {
                            editTask.save();
                            notifyItemChanged(index);
                        }
                    });
                    dialog.show();
                } else if (object instanceof Variable var) {
                    EditVariableDialog dialog = new EditVariableDialog(context, var);
                    dialog.setTitle(R.string.variable_update);
                    dialog.setCallback(result -> {
                        if (result) {
                            var.save();
                            notifyItemChanged(index);
                        }
                    });
                    dialog.show();
                }
            });

            binding.copyButton.setOnClickListener(v -> {
                int index = getAdapterPosition();
                Object object = data.get(index);
                if (object instanceof Task task) {
                    Task copy = task.newCopy();
                    copy.setTitle(context.getString(R.string.copy_title, task.getTitle()));
                    dialog.setCopyObject(copy);
                }

                if (object instanceof Variable var) {
                    Variable copy = var.newCopy();
                    copy.setTitle(context.getString(R.string.copy_title, var.getTitle()));
                    dialog.setCopyObject(copy);
                }
            });

            binding.settingButton.setOnClickListener(v -> {
                int index = getAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Task task) {
                    BlueprintView.tryPushStack(task);
                    dialog.dismiss();
                }
            });

            binding.deleteButton.setOnClickListener(v -> {
                if (needDelete) {
                    int index = getAdapterPosition();
                    Object object = data.get(index);

                    if (object instanceof Task task) {
                        deleteTask(task, result -> {
                            Task parent = task.getParent();
                            if (parent == null) {
                                Saver.getInstance().removeTask(task.getId());
                            } else {
                                parent.removeTask(task.getId());
                            }
                            data.remove(index);
                            notifyItemRemoved(index);
                            deleteSameObject(object);
                        });
                    }

                    if (object instanceof Variable var) {
                        deleteVariable(var, result -> {
                            if (result) {
                                Task parent = var.getParent();
                                if (parent == null) {
                                    Saver.getInstance().removeVar(var.getId());
                                } else {
                                    parent.removeVariable(var.getId());
                                }
                                data.remove(index);
                                notifyItemRemoved(index);
                                deleteSameObject(object);
                            }
                        });
                    }
                } else {
                    binding.deleteButton.setChecked(true);
                    needDelete = true;
                    binding.deleteButton.postDelayed(() -> {
                        binding.deleteButton.setChecked(false);
                        needDelete = false;
                    }, 1500);
                }
            });

            binding.helpButton.setOnClickListener(v -> {
                int index = getAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Variable var) {
                    Action action = new SetVariableAction(var);
                    if (callback != null) callback.onResult(action);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                int index = getAdapterPosition();
                Object object = data.get(index);
                Action action = null;
                if (object instanceof ActionType actionType) {
                    ActionInfo info = ActionInfo.getActionInfo(actionType);
                    if (info != null) action = info.newInstance();
                }

                if (object instanceof Task task) {
                    ExecuteTaskAction executeTaskAction = new ExecuteTaskAction();
                    executeTaskAction.setTask(task);
                    action = executeTaskAction;
                }

                if (object instanceof Variable var) {
                    action = new GetVariableAction(var);
                }

                if (callback != null) callback.onResult(action);
            });
        }

        public void refresh(Object object) {
            binding.taskName.setText(getObjectTitle(object));
            binding.icon.setImageResource(getObjectIcon(object));
            binding.helpButton.setVisibility(View.GONE);

            String desc = getObjectDesc(object);
            if (desc != null && !desc.isEmpty()) {
                binding.taskDesc.setVisibility(View.VISIBLE);
                binding.taskDesc.setText(desc);
            } else {
                binding.taskDesc.setVisibility(View.GONE);
            }

            binding.editButton.setVisibility(View.GONE);
            binding.copyButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.GONE);

            binding.getRoot().setAlpha(1f);
            binding.getRoot().setEnabled(true);
            binding.settingButton.setVisibility(View.GONE);
            if (object instanceof Task task) {

                binding.copyButton.setVisibility(View.VISIBLE);
                binding.editButton.setVisibility(View.VISIBLE);
                Task childTask = dialog.task.findChildTask(task.getId());
                if (task.equals(dialog.task) || childTask == null) {
                    binding.deleteButton.setVisibility(View.GONE);
                } else {
                    binding.deleteButton.setVisibility(View.VISIBLE);
                }

                binding.settingButton.setVisibility(View.VISIBLE);

                if (task.getParent() == null) {
                    binding.icon.setImageResource(R.drawable.icon_globe);
                }

                if (task.getActions(CustomStartAction.class).isEmpty()) {
                    binding.getRoot().setEnabled(false);
                    binding.getRoot().setAlpha(0.5f);
                }
            }

            binding.helpButton.setIconResource(R.drawable.icon_help);
            binding.varBox.setVisibility(View.GONE);
            if (object instanceof Variable var) {
                binding.copyButton.setVisibility(View.VISIBLE);
                binding.editButton.setVisibility(View.VISIBLE);
                binding.deleteButton.setVisibility(View.VISIBLE);
                binding.helpButton.setVisibility(View.VISIBLE);

                binding.helpButton.setIconResource(R.drawable.icon_download);

                if (var.getParent() == null) {
                    binding.icon.setImageResource(R.drawable.icon_globe);
                }

                binding.varBox.setVisibility(View.VISIBLE);
                PinInfo pinInfo = var.getKeyPinInfo();
                if (pinInfo != null) {
                    binding.keySlot.setText(pinInfo.getTitle());
                }
                binding.typeSpinner.setSelection(var.getType().ordinal());
                binding.valueSlot.setVisibility(var.getType() == Variable.VariableType.MAP ? View.VISIBLE : View.GONE);
                pinInfo = var.getValuePinInfo();
                if (pinInfo != null) {
                    binding.valueSlot.setText(pinInfo.getTitle());
                }
            }
        }

        private void deleteTask(Task task, BooleanResultCallback callback) {
            List<Usage> usages;
            Task parent = task.getParent();
            if (parent == null) {
                usages = Saver.getInstance().getTaskUses(task.getId());
            } else {
                usages = parent.getTaskUses(task.getId());
            }

            if (!usages.isEmpty()) {
                LinearLayout linearLayout = getTipsLinearLayout(context, usages, R.string.task_delete_tips);
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.remove_task)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.enter, null)
                        .setNegativeButton(R.string.force_delete, (dialog, which) -> callback.onResult(true))
                        .show();

                int px = (int) DisplayUtil.dp2px(context, 32);
                DisplayUtil.setViewMargin(linearLayout, px, px / 2, px, px / 2);
            } else {
                callback.onResult(true);
            }
        }

        private void deleteVariable(Variable var, BooleanResultCallback callback) {
            List<Usage> usages;
            Task parent = var.getParent();
            if (parent == null) {
                usages = Saver.getInstance().getVarUses(var.getId());
            } else {
                usages = parent.getVariableUses(var.getId());
            }

            if (!usages.isEmpty()) {
                LinearLayout linearLayout = getTipsLinearLayout(context, usages, R.string.variable_delete_tips);
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.remove_task)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.enter, null)
//                        .setNegativeButton(R.string.force_delete, (dialog, which) -> callback.onResult(true))
                        .show();

                int px = (int) DisplayUtil.dp2px(context, 32);
                DisplayUtil.setViewMargin(linearLayout, px, px / 2, px, px / 2);
            } else {
                callback.onResult(true);
            }
        }
    }
}
