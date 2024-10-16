package top.bogey.touch_tool.bean.pin.pin_objects.pin_execute;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinIconExecute extends PinExecute {
    private String value;
    private transient Bitmap image;

    public PinIconExecute() {
        super(PinSubType.WITH_ICON);
    }

    public PinIconExecute(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsString(jsonObject, "value", null);
    }

    public Bitmap getImage() {
        if (image == null || image.isRecycled()) {
            if (value == null || value.isEmpty()) return null;
            try {
                byte[] bytes = Base64.decode(value, Base64.NO_WRAP);
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public void setImage(Bitmap image) {
        if (image == null) {
            value = null;
            return;
        }

        float px = DisplayUtil.dp2px(MainApplication.getInstance(), 28);
        image = DisplayUtil.createScaledBitmap(image, (int) px, (int) px);
        this.image = image;

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            image.compress(Bitmap.CompressFormat.WEBP, 100, stream);
            byte[] bytes = stream.toByteArray();
            value = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        super.reset();
        value = null;
        image = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinIconExecute that = (PinIconExecute) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }
}
