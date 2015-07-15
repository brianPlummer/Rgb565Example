package rgb565.codemonkeylabs.com.rgb565.picasso;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by brianplummer on 7/15/15.
 */
public class BitmapTransform implements Transformation
{

    int maxWidth;
    int maxHeight;
    String key;
    Bitmap.Config config;

    public BitmapTransform(int maxWidth, int maxHeight, String key, Bitmap.Config config) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.key = key;
        this.config = config;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int targetWidth, targetHeight;
        double aspectRatio;

        if (source.getWidth() > source.getHeight()) {
            targetWidth = maxWidth;
            aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            targetHeight = (int) (targetWidth * aspectRatio);
        } else {
            targetHeight = maxHeight;
            aspectRatio = (double) source.getWidth() / (double) source.getHeight();
            targetWidth = (int) (targetHeight * aspectRatio);
        }

        Bitmap result = CustomBitmap.createScaledBitmap(source, targetWidth,
                targetHeight, false, config);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return key + "_" + maxWidth + ":" + maxHeight + config.name();
    }

}
