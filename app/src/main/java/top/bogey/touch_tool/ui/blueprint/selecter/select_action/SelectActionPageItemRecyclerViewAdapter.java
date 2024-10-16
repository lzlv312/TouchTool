package top.bogey.touch_tool.ui.blueprint.selecter.select_action;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.ActionInfo;
import top.bogey.touch_tool.bean.action.ActionType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
import top.bogey.touch_tool.bean.pin.PinInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.PinList;
import top.bogey.touch_tool.bean.pin.pin_objects.PinMap;
import top.bogey.touch_tool.bean.pin.pin_objects.PinObject;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.DialogSelectActionPageItemBinding;
import top.bogey.touch_tool.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.custom.EditTaskDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.listener.SpinnerSelectedListener;

public class SelectActionPageItemRecyclerViewAdapter extends RecyclerView.Adapter<SelectActionPageItemRecyclerViewAdapter.ViewHolder> {
    private final static List<PinType> PIN_INFO_LIST = PinInfo.getValuePinTypes();

    private final CardLayoutView cardLayoutView;
    private final ResultCallback<ActionCard> callback;

    private List<Object> data;

    public SelectActionPageItemRecyclerViewAdapter(CardLayoutView cardLayoutView, ResultCallback<ActionCard> callback) {
        this.cardLayoutView = cardLayoutView;
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
        if (object instanceof VariableInfo var) return var.getName();
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

            ArrayAdapter<String> valueAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_select_item);
            binding.valueSpinner.setAdapter(valueAdapter);
            binding.valueSpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getBindingAdapterPosition();
                    VariableInfo var = (VariableInfo) data.get(index);
                    PinType valuePinType = PIN_INFO_LIST.get(position);
                    PinType pinType = var.getValue().getType();
                    if (pinType == PinType.MAP) {
                        PinMap pinMap = (PinMap) var.getValue();
                        PinType valueType = pinMap.getValueType();
                        if (valueType == valuePinType) return;

                        pinMap.setValueType(valuePinType);
                        var.getOwner().addVar(var.getName(), pinMap);
                    }
                }
            });

            ArrayAdapter<String> keyAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_select_item);
            binding.keySpinner.setAdapter(keyAdapter);
            binding.keySpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getBindingAdapterPosition();
                    VariableInfo var = (VariableInfo) data.get(index);
                    PinType pinType = var.getValue().getType();
                    List<PinSubType> subTypes = PinInfo.getValuePinSubTypes(pinType);
                    PinSubType subType = subTypes.get(position);
                    List<PinType> keyPinTypes = PinInfo.getKeyPinTypes(pinType, subType);
                    PinType keyPinType = keyPinTypes.get(position);

                    if (pinType == PinType.LIST) {
                        PinList pinList = (PinList) var.getValue();
                        PinType valueType = pinList.getValueType();
                        if (keyPinType == valueType) return;

                        pinList.setValueType(keyPinType);
                        var.getOwner().addVar(var.getName(), pinList);
                    }

                    if (pinType == PinType.MAP) {
                        PinMap pinMap = (PinMap) var.getValue();
                        PinType keyType = pinMap.getKeyType();
                        if (keyPinType == keyType) return;

                        pinMap.setKeyType(keyPinType);
                        var.getOwner().addVar(var.getName(), pinMap);
                    }
                }
            });

            ArrayAdapter<String> subTypeAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_select_item);
            binding.subTypeSpinner.setAdapter(subTypeAdapter);
            binding.subTypeSpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getBindingAdapterPosition();
                    VariableInfo var = (VariableInfo) data.get(index);
                    PinType pinType = var.getValue().getType();
                    List<PinSubType> subTypes = PinInfo.getValuePinSubTypes(pinType);
                    PinSubType subType = subTypes.get(position);
                    if (subType == var.getValue().getSubType()) return;

                    PinInfo pinInfo = PinInfo.getPinInfo(pinType, subType);
                    PinBase pinBase = pinInfo.newInstance();
                    if (pinBase instanceof PinObject pinObject) {
                        var.setValue(pinObject);
                    }

                    List<PinType> keyPinTypes = PinInfo.getKeyPinTypes(pinType, subType);
                    keyAdapter.clear();
                    for (PinType type : keyPinTypes) {
                        keyAdapter.add(PinInfo.getPinTypeTitle(type));
                    }
                }
            });

            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_select_item);
            for (PinType pinType : PIN_INFO_LIST) {
                typeAdapter.add(PinInfo.getPinTypeTitle(pinType));
            }
            binding.typeSpinner.setAdapter(typeAdapter);
            binding.typeSpinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getBindingAdapterPosition();
                    VariableInfo var = (VariableInfo) data.get(index);
                    PinType pinType = PIN_INFO_LIST.get(position);
                    if (pinType == var.getValue().getType()) return;

                    PinInfo pinInfo = PinInfo.getPinInfo(pinType);
                    PinBase pinBase = pinInfo.newInstance();
                    if (pinBase instanceof PinObject pinObject) {
                        var.setValue(pinObject);
                    }

                    List<PinSubType> subTypes = PinInfo.getValuePinSubTypes(pinType);
                    subTypeAdapter.clear();
                    for (PinSubType subType : subTypes) {
                        subTypeAdapter.add(PinInfo.getPinSubTypeTitle(subType));
                    }
                }
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

                if (object instanceof VariableInfo var) {
                    AppUtil.showEditDialog(context, R.string.task_update, var.getName(), result -> {
                        if (result != null && !result.isEmpty()) {
                            var.setName(result);
                        }
                    });
                }
            });

            binding.copyButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);
                if (object instanceof Task copyTask) {
                    Task parent = copyTask.getParent();
                    if (parent == null) return;
                    Task copy = copyTask.newCopy();
                    copy.setTitle(context.getString(R.string.task_copy_title, copyTask.getTitle()));
                    parent.addTask(copy);
                    parent.save();
                    data.add(index + 1, copy);
                    notifyItemInserted(index + 1);
                }

                if (object instanceof VariableInfo var) {
                    PinBase copy = var.getValue().newCopy();
                    VariableInfo info = new VariableInfo(var.getOwner(), context.getString(R.string.task_copy_title, var.getName()), (PinObject) copy);
                    data.add(index + 1, info);
                    notifyItemInserted(index + 1);
                }
            });

            binding.exchangeButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Object object = data.get(index);
                if (object instanceof Task exchangeTask) {
                    Task parent = exchangeTask.getParent();
                    if (parent == null) {

                    }
                }

            });

            binding.setVarValue.setOnClickListener(v -> {

            });

            binding.getVarValue.setOnClickListener(v -> {

            });

            binding.deleteButton.setOnClickListener(v -> {
                if (needDelete) {
                    int index = getBindingAdapterPosition();
                    Object object = data.get(index);
                    if (object instanceof Task task) {
                        Task parent = task.getParent();
                        if (parent == null) return;
                        parent.removeTask(task);
                    }

                    if (object instanceof VariableInfo var) {
                        var.getOwner().removeVar(var.getName());
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
                ActionCard card = null;
                if (object instanceof ActionType actionType) {
                    card = cardLayoutView.addNewCard(actionType);
                }
                if (callback != null) callback.onResult(card);
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

            if (object instanceof Task task) {
                // 主任务无法在这编辑或删除
                if (task.getParent() != null) {
                    binding.exchangeButton.setVisibility(ViewGroup.VISIBLE);
                    binding.copyButton.setVisibility(ViewGroup.VISIBLE);
                    binding.editButton.setVisibility(ViewGroup.VISIBLE);
                    binding.deleteButton.setVisibility(ViewGroup.VISIBLE);
                }
            }

            if (object instanceof VariableInfo var) {
                binding.editButton.setVisibility(ViewGroup.VISIBLE);
                binding.deleteButton.setVisibility(ViewGroup.VISIBLE);

                binding.typeBox.setVisibility(ViewGroup.VISIBLE);
                PinType type = var.getValue().getType();
                PinSubType subType = var.getValue().getSubType();

                binding.typeSpinner.setSelection(PIN_INFO_LIST.indexOf(type));
                List<PinSubType> subTypes = PinInfo.getValuePinSubTypes(type);
                if (subTypes.size() > 1) {
                    binding.typeSpinner.setBackgroundResource(R.drawable.shape_spinner_left);
                    binding.subTypeSpinner.setVisibility(ViewGroup.VISIBLE);
                    binding.subTypeSpinner.setSelection(subTypes.indexOf(subType));
                } else {
                    binding.typeSpinner.setBackgroundResource(R.drawable.shape_spinner);
                    binding.subTypeSpinner.setVisibility(ViewGroup.GONE);
                    binding.subTypeSpinner.setSelection(0);
                }

                binding.varBox.setVisibility(type == PinType.LIST || type == PinType.MAP ? ViewGroup.VISIBLE : ViewGroup.GONE);
                List<PinType> keyPinTypes = PinInfo.getKeyPinTypes(type, subType);
                if (type == PinType.LIST) {
                    PinList pinList = (PinList) var.getValue();
                    PinType valueType = pinList.getValueType();
                    binding.keySpinner.setSelection(keyPinTypes.indexOf(valueType));
                }

                if (type == PinType.MAP) {
                    PinMap pinMap = (PinMap) var.getValue();
                    PinType keyType = pinMap.getKeyType();
                    PinType valueType = pinMap.getValueType();
                    binding.keySpinner.setSelection(keyPinTypes.indexOf(keyType));
                    binding.valueSpinner.setSelection(PIN_INFO_LIST.indexOf(valueType));
                }

                binding.getVarValue.setVisibility(ViewGroup.VISIBLE);
                binding.setVarValue.setVisibility(ViewGroup.VISIBLE);
            }
        }
    }
}
