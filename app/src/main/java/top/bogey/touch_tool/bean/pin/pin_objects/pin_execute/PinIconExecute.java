package top.bogey.touch_tool.bean.pin.pin_objects.pin_execute;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.bean.pin.pin_objects.PinBase;
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
    public void sync(PinBase value) {
        if (value instanceof PinIconExecute pinIcon) {
            this.value = pinIcon.value;
        }
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PinIconExecute that)) return false;
        if (!super.equals(object)) return false;

        return Objects.equals(value, that.value) && Objects.equals(getImage(), that.getImage());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(value);
        result = 31 * result + Objects.hashCode(getImage());
        return result;
    }
}
