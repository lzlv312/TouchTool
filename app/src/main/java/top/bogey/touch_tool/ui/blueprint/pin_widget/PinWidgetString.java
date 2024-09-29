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
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.DynamicPinsAction;
import top.bogey.touch_tool.bean.action.number.MathExpressionAction;
import top.bogey.touch_tool.bean.pin.Pin;
import top.bogey.touch_tool.bean.pin.pins.pin_number.PinFloat;
import top.bogey.touch_tool.bean.pin.pins.pin_string.PinString;
import top.bogey.touch_tool.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool.ui.InstantActivity;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.pin.PinView;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetString extends PinWidget<PinString> {
    private final PinWidgetInputBinding binding;

    public PinWidgetString(@NonNull Context context, ActionCard card, PinView pinView, PinString pinBase, boolean custom) {
        super(context, card, pinView, pinBase, custom);
        binding = PinWidgetInputBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    protected void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);

        binding.editText.setEnabled(false);
        binding.pickButton.setVisibility(VISIBLE);
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
                                pinBase.setValue(null);
                            } else {
                                pinBase.setValue(uri.toString());
                            }
                            binding.editText.setText(getRingtoneName(pinBase.getValue()));
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
                        pinBase.setValue(s.toString());
                    }
                });
            }
            case MULTI_LINE -> {
                binding.editText.setEnabled(true);
                binding.pickButton.setVisibility(GONE);
                binding.editText.setText(pinBase.getValue());
                binding.editText.setSingleLine(false);
                binding.editText.setMaxLines(10);
                binding.editText.setInputType(binding.editText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
                binding.editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        pinBase.setValue(s.toString());
                        resetDynamicPin(s.toString());
                    }
                });
            }
            case NODE_PATH -> {
                binding.editText.setText(pinBase.getValue());
                binding.editText.setSingleLine(false);
                binding.editText.setMaxLines(10);
                binding.pickButton.setOnClickListener(v -> {

                });
            }
            case TASK_ID -> {

            }
            default -> {
                binding.editText.setEnabled(true);
                binding.pickButton.setVisibility(GONE);
            }
        }

        binding.editText.setText(pinBase.getValue());
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                pinBase.setValue(s.toString());
            }
        });
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
                Pattern pattern = Pattern.compile("\\b([a-zA-Z])\\b");
                matcher = pattern.matcher(value);
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
                Pin pin = new Pin(new PinFloat(), 0, false, true, false);
                pin.setTitle(key);
                card.addPin(pin);
            }
        }
    }
}
