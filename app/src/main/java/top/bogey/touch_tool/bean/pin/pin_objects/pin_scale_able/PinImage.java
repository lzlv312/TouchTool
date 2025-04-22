package top.bogey.touch_tool.bean.pin.pin_objects.pin_scale_able;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import top.bogey.touch_tool.bean.pin.pin_objects.PinSubType;
import top.bogey.touch_tool.bean.pin.pin_objects.PinType;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinImage extends PinScaleAble<String> {
    private transient Bitmap image;

    public PinImage() {
        super(PinType.IMAGE);
    }

    public PinImage(PinSubType subType) {
        super(PinType.IMAGE, subType);
    }

    public PinImage(String value) {
        this();
        this.value = value;
    }

    public PinImage(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtil.getAsString(jsonObject, "value", null);
    }


    public Bitmap getImage() {
        if (image == null || image.isRecycled()) {
            if (value == null || value.isEmpty()) return null;
            try {
                byte[] bytes = Base64.decode(value, Base64.NO_WRAP);
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                float scale = getScale();
                if (scale != 1) {
                    image = Bitmap.createScaledBitmap(image, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), true);
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        if (image == null) return;

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

    @NonNull
    @Override
    public String toString() {
        Bitmap image = getImage();
        if (image == null) return super.toString();
        else return super.toString() + "[" + image.getWidth() + "x" + image.getHeight() + "]";
    }

    @Override
    public String getValue(EAnchor anchor) {
        return getValue();
    }

    @Override
    public void setValue(EAnchor anchor, String value) {
        setValue(value);
    }
}
