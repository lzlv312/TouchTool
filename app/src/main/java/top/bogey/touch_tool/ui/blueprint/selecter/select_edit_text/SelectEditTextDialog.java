package top.bogey.touch_tool.ui.blueprint.selecter.select_edit_text;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import top.bogey.touch_tool.databinding.DialogFullScreenEditBinding;
import top.bogey.touch_tool.utils.callback.StringResultCallback;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class SelectEditTextDialog extends BottomSheetDialog {

    public SelectEditTextDialog(@NonNull Context context, TextInputEditText from, StringResultCallback callback) {
        super(context);
        DialogFullScreenEditBinding binding = DialogFullScreenEditBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        binding.editText.setText(from.getText());
        binding.editText.setEnabled(from.isEnabled());
        binding.editText.setMaxLines(from.getMaxLines());
        binding.editText.setMinLines(from.getMinLines());
        binding.editText.setInputType(from.getInputType());

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                callback.onResult(s.toString());
            }
        });

        binding.editText.requestFocus();
    }
}
