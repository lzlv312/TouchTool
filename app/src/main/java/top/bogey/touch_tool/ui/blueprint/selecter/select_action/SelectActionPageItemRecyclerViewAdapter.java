package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.action.task.CustomStartAction;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.action.variable.GetVariableAction;
import top.bogey.touch_tool.bean.action.variable.SetVariableAction;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.bean.task.Variable;
import top.bogey.touch_tool.databinding.DialogSelectActionPageItemBinding;
import top.bogey.touch_tool.ui.blueprint.BlueprintView;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.custom.EditTaskDialog;
import top.bogey.touch_tool.ui.custom.EditVariableDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.listener.SpinnerSelectedListener;

public class SelectActionPageItemRecyclerViewAdapter extends RecyclerView.Adapter<SelectActionPageItemRecyclerViewAdapter.ViewHolder> {

    private final ResultCallback<Action> callback;

    private List<Object> data;

    public SelectActionPageItemRecyclerViewAdapter(ResultCallback<Action> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DialogSelectActionPageItemBinding binding = DialogSelectActionPageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        if (object instanceof Task task) return task.getTitle();
        if (object instanceof Variable var) return var.getTitle();
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

    public void setData(List<Object> data, boolean sort) {
        this.data = data;
        if (sort) AppUtil.chineseSort(data, SelectActionPageItemRecyclerViewAdapter::getObjectTitle);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final DialogSelectActionPageItemBinding binding;
        private final Context context;
        private boolean needDelete = false;

        public ViewHolder(@NonNull DialogSelectActionPageItemBinding binding) {
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
                            int index = getBindingAdapterPosition();
                            Variable var = (Variable) data.get(index);
                            var.setKeyPinInfo(pinInfo);
                            notifyItemChanged(index);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });

            binding.typeSpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getBindingAdapterPosition();
                    Variable var = (Variable) data.get(index);
                    if (var.setType(Variable.VariableType.values()[position])) notifyItemChanged(index);
                }
            });

            binding.valueSlot.setOnClickListener(v -> {
                SelectActionVariableTypeDialog dialog = new SelectActionVariableTypeDialog(context);
                new MaterialAlertDialogBuilder(context)
                        .setView(dialog)
                        .setPositiveButton(R.string.enter, (view, which) -> {
                            PinInfo pinInfo = dialog.getSelected();
                            binding.keySlot.setText(pinInfo.getTitle());
                            int index = getBindingAdapterPosition();
                            Variable var = (Variable) data.get(index);
                            var.setValuePinInfo(pinInfo);
                            notifyItemChanged(index);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            });

            binding.editButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Task editTask) {
                    EditTaskDialog dialog = new EditTaskDialog(context, editTask);
                    dialog.setTitle(R.string.task_update);
                    dialog.setCallback(result -> {
                        if (result) editTask.save();
                    });
                    dialog.show();
                }

                if (object instanceof Variable var) {
                    EditVariableDialog dialog = new EditVariableDialog(context, var);
                    dialog.setTitle(R.string.variable_update);
                    dialog.setCallback(result -> {
                        if (result) var.save();
                    });
                    dialog.show();
                }
            });

            binding.copyButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);
                if (object instanceof Task task) {
                    Task copy = task.newCopy();
                    copy.setTitle(context.getString(R.string.copy_title, task.getTitle()));
                    if (task.getParent() != null) task.getParent().addTask(copy);
                    copy.save();
                    data.add(index + 1, copy);
                    notifyItemInserted(index + 1);
                }

                if (object instanceof Variable var) {
                    Variable copy = var.newCopy();
                    copy.setTitle(context.getString(R.string.copy_title, var.getTitle()));
                    if (var.getOwner() != null) var.getOwner().addVar(copy);
                    copy.save();
                    data.add(index + 1, copy);
                    notifyItemInserted(index + 1);
                }
            });

            binding.settingButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Task task) {
                    BlueprintView.tryPushStack(task);
                }
            });

            binding.setVarValue.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Variable var) {
                    Action action = new SetVariableAction(var);
                    if (callback != null) callback.onResult(action);
                }
            });

            binding.getVarValue.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);

                if (object instanceof Variable var) {
                    Action action = new GetVariableAction(var);
                    if (callback != null) callback.onResult(action);
                }
            });

            binding.deleteButton.setOnClickListener(v -> {
                if (needDelete) {
                    int index = getBindingAdapterPosition();
                    Object object = data.get(index);

                    if (object instanceof Task task) {
                        Task parent = task.getParent();
                        if (parent == null) {
                            Saver.getInstance().removeTask(task.getId());
                        } else {
                            parent.removeTask(task.getId());
                        }
                    }

                    if (object instanceof Variable var) {
                        Task owner = var.getOwner();
                        if (owner == null) {
                            Saver.getInstance().removeVar(var.getId());
                        } else {
                            owner.removeVar(var.getId());
                        }
                    }

                    data.remove(index);
                    notifyItemRemoved(index);
                } else {
                    binding.deleteButton.setChecked(true);
                    needDelete = true;
                    binding.deleteButton.postDelayed(() -> {
                        binding.deleteButton.setChecked(false);
                        needDelete = false;
                    }, 1500);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
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

            String desc = getObjectDesc(object);
            if (desc != null && !desc.isEmpty()) {
                binding.taskDesc.setVisibility(ViewGroup.VISIBLE);
                binding.taskDesc.setText(desc);
            } else {
                binding.taskDesc.setVisibility(ViewGroup.GONE);
            }

            binding.editButton.setVisibility(ViewGroup.GONE);
            binding.copyButton.setVisibility(ViewGroup.GONE);
            binding.deleteButton.setVisibility(ViewGroup.GONE);

            binding.getRoot().setAlpha(1f);
            binding.getRoot().setEnabled(true);
            binding.settingButton.setVisibility(ViewGroup.GONE);
            if (object instanceof Task task) {
                binding.copyButton.setVisibility(ViewGroup.VISIBLE);
                binding.editButton.setVisibility(ViewGroup.VISIBLE);
                binding.deleteButton.setVisibility(ViewGroup.VISIBLE);

                binding.settingButton.setVisibility(ViewGroup.VISIBLE);

                if (task.getActions(CustomStartAction.class).isEmpty()) {
                    binding.getRoot().setEnabled(false);
                    binding.getRoot().setAlpha(0.5f);
                }
            }

            binding.getVarValue.setVisibility(ViewGroup.GONE);
            binding.setVarValue.setVisibility(ViewGroup.GONE);
            binding.varBox.setVisibility(ViewGroup.GONE);
            if (object instanceof Variable var) {
                binding.copyButton.setVisibility(ViewGroup.VISIBLE);
                binding.editButton.setVisibility(ViewGroup.VISIBLE);
                binding.deleteButton.setVisibility(ViewGroup.VISIBLE);

                binding.getVarValue.setVisibility(ViewGroup.VISIBLE);
                binding.setVarValue.setVisibility(ViewGroup.VISIBLE);

                binding.varBox.setVisibility(ViewGroup.VISIBLE);
                PinInfo pinInfo = var.getKeyPinInfo();
                if (pinInfo != null) {
                    binding.keySlot.setText(pinInfo.getTitle());
                }
                binding.typeSpinner.setSelection(var.getType().ordinal());
                binding.valueSlot.setVisibility(var.getType() == Variable.VariableType.MAP ? ViewGroup.VISIBLE : ViewGroup.GONE);
                pinInfo = var.getValuePinInfo();
                if (pinInfo != null) {
                    binding.valueSlot.setText(pinInfo.getTitle());
                }
            }
        }
    }
}
