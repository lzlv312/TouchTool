package top.bogey.touch_tool.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.number.MathExpressionAction;
import top.bogey.touch_tool.bean.action.task.ExecuteTaskAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_number.PinDouble;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinAutoPinString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinSingleLineString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinRingtoneString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinShortcutString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinTaskString;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinUrlString;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool.ui.InstantActivity;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.picker.NodePickerPreview;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionByCustomActionDialog;
import top.bogey.touch_tool.ui.blueprint.selecter.select_edit_text.SelectEditTextDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetString extends PinWidget<PinString> {
    private final PinWidgetInputBinding binding;

    private final ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_edit_text, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.fullScreen) {
                mode.finish();
                new SelectEditTextDialog(getContext(), binding.editText, binding.editText::setText).show();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinString pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetInputBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinUrlString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinShortcutString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinRingtoneString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinAutoPinString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinSingleLineString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinNodePathString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinTaskString pinBase, boolean custom) {
        this(context, card, pinView, (PinString) pinBase, custom);
    }

    @Override
    protected void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);

        binding.editText.setEnabled(false);
        binding.pickButton.setVisibility(VISIBLE);

        binding.editText.setCustomSelectionActionModeCallback(callback);
        binding.editText.setCustomInsertionActionModeCallback(callback);

        switch (pinBase.getSubType()) {
            case URL -> {
                String url = "ttp://do_action?" + InstantActivity.TASK_ID + "=" + card.getTask().getId() + "&" + InstantActivity.ACTION_ID + "=" + card.getAction().getId();
                binding.editText.setText(url);
                binding.pickButton.setIconResource(R.drawable.icon_copy);
                binding.pickButton.setOnClickListener(v -> {
                    ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(ClipData.newPlainText(getContext().getString(R.string.copy_ttp_url), url));
                    Toast.makeText(getContext(), R.string.copy_tips, Toast.LENGTH_SHORT).show();
                });
            }
            case SHORTCUT -> {
                binding.editText.setVisibility(GONE);
                binding.pickButton.setIconResource(R.drawable.icon_shortcut);
                binding.pickButton.setOnClickListener(v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ShortcutManager manager = (ShortcutManager) getContext().getSystemService(Context.SHORTCUT_SERVICE);
                        if (manager.isRequestPinShortcutSupported()) {
                            Intent intent = new Intent(getContext(), InstantActivity.class);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.putExtra(InstantActivity.INTENT_KEY_DO_ACTION, true);
                            intent.putExtra(InstantActivity.TASK_ID, card.getTask().getId());
                            intent.putExtra(InstantActivity.ACTION_ID, card.getAction().getId());
                            ShortcutInfo info = new ShortcutInfo.Builder(getContext(), card.getAction().getId())
                                    .setShortLabel(card.getTask().getTitle())
                                    .setIcon(Icon.createWithResource(getContext(), R.drawable.icon_shortcut))
                                    .setIntent(intent)
                                    .build();
                            manager.requestPinShortcut(info, null);
                            return;
                        }
                    }
                    Toast.makeText(getContext(), R.string.device_not_support_shortcut, Toast.LENGTH_SHORT).show();
                });
            }
            case RINGTONE -> {
                String value = pinBase.getValue();
                binding.editText.setText(getRingtoneName(value));
                binding.pickButton.setIconResource(R.drawable.icon_notification);
                binding.pickButton.setOnClickListener(v -> {
                    MainActivity activity = MainApplication.getInstance().getActivity();
                    activity.launcherRingtone(value, (code, intent) -> {
                        if (code == Activity.RESULT_OK) {
                            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                            if (uri == null) {
                                binding.editText.setText("");
                                pinBase.setValue(null);
                                pinView.getPin().notifyValueUpdated();
                            } else {
                                binding.editText.setText(getRingtoneName(uri.toString()));
                                pinBase.setValue(uri.toString());
                                pinView.getPin().notifyValueUpdated();
                            }
                        }
                    });
                });
            }
            case AUTO_PIN -> {
                binding.editText.setEnabled(true);
                binding.pickButton.setVisibility(GONE);
                binding.editText.setText(pinBase.getValue());
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (Objects.equals(s.toString(), pinBase.getValue())) return;
                        pinBase.setValue(s.toString());
                        pinView.getPin().notifyValueUpdated();
                        resetDynamicPin(s.toString());
                    }
                });
            }
            case SINGLE_LINE -> {
                binding.pickButton.setVisibility(GONE);
                binding.editText.setEnabled(true);
                binding.editText.setText(pinBase.getValue());
                binding.editText.setSingleLine(true);
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        pinBase.setValue(s.toString());
                        pinView.getPin().notifyValueUpdated();
                    }
                });
            }
            case NODE_PATH -> {
                PinNodePathString nodePath = (PinNodePathString) pinBase;
                binding.editText.setText(String.valueOf(nodePath.getNodeInfo()));
                binding.pickButton.setIconResource(R.drawable.icon_widget);
                binding.pickButton.setOnClickListener(v -> new NodePickerPreview(getContext(), result -> {
                    nodePath.setValue(result);
                    pinView.getPin().notifyValueUpdated();
                    binding.editText.setText(String.valueOf(nodePath.getNodeInfo()));
                }, nodePath.getNodeInfo()).show());
            }
            case TASK_ID -> {
                Task task = Saver.getInstance().getTask(card.getTask(), pinBase.getValue());
                if (task != null) binding.editText.setText(task.getTitle());
                binding.pickButton.setIconResource(R.drawable.icon_task);
                binding.pickButton.setOnClickListener(v -> new SelectActionByCustomActionDialog(getContext(), card.getTask(), action -> {
                    ExecuteTaskAction executeTaskAction = (ExecuteTaskAction) action;
                    Task executeTask = executeTaskAction.getTask(card.getTask());
                    pinBase.setValue(executeTask.getId());
                    pinView.getPin().notifyValueUpdated();
                    binding.editText.setText(executeTask.getTitle());
                    ExecuteTaskAction cardAction = (ExecuteTaskAction) card.getAction();
                    cardAction.sync(card.getTask(), executeTask);
                }).show());
            }
            default -> {
                binding.editText.setEnabled(true);
                binding.editText.setText(pinBase.getValue());
                binding.editText.setSingleLine(false);
                binding.editText.setMaxLines(10);
                binding.editText.setInputType(binding.editText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        pinBase.setValue(s.toString());
                        pinView.getPin().notifyValueUpdated();
                    }
                });
                binding.pickButton.setVisibility(GONE);
            }
        }
    }

    @Override
    protected void initCustom() {

    }

    private String getRingtoneName(String path) {
        if (path == null) return null;
        Uri uri = Uri.parse(path);
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
        if (ringtone == null) return path;
        return ringtone.getTitle(getContext());
    }

    private void resetDynamicPin(String value) {
        if (card.getAction() instanceof DynamicPinsAction dynamicPinsAction) {
            Matcher matcher = null;
            if (dynamicPinsAction instanceof MathExpressionAction) {
                Pattern pattern = AppUtil.getPattern("\\b([a-zA-Z])\\b");
                if (pattern != null) matcher = pattern.matcher(value);
            }

            List<String> keys = new ArrayList<>();
            if (matcher != null) {
                while (matcher.find()) {
                    keys.add(matcher.group(1));
                }
            }

            List<Pin> removePins = new ArrayList<>();
            for (Pin dynamicPin : dynamicPinsAction.getDynamicPins()) {
                String title = dynamicPin.getTitle();
                if (keys.isEmpty()) removePins.add(dynamicPin);
                else if (!keys.remove(title)) removePins.add(dynamicPin);
            }
            removePins.forEach(card::removePin);

            for (String key : keys) {
                Pin pin = null;
                if (dynamicPinsAction instanceof MathExpressionAction) {
                    pin = new Pin(new PinDouble(), 0, false, true, false);
                }
                if (pin == null) break;
                pin.setTitle(key);
                card.addPin(pin);
            }
        }
    }
}
