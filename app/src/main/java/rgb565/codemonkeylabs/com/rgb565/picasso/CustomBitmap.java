package rgb565.codemonkeylabs.com.rgb565.picasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * the built in Bitmap class does not contain a way to specify a config (RGB565,RGB8888) when
 * creating a scaled bitmap from source.
 *
 * i copied the source of Bitmap.createScaledBitmap() and it's depending functions
 * to enable the config to be specified.  the methods copied contain boilerplate
 * dimension calculation only
 *
 */

public final class CustomBitmap
{


    private static volatile Matrix sScaleMatrix;

    /**
     * Creates a new bitmap, scaled from an existing bitmap, when possible. If the
     * specified width and height are the same as the current width and height of
     * the source bitmap, the source bitmap is returned and no new bitmap is
     * created.
     *
     * @param src       The source bitmap.
     * @param dstWidth  The new bitmap's desired width.
     * @param dstHeight The new bitmap's desired height.
     * @param filter    true if the source should be filtered.
     * @return The new scaled bitmap or the source bitmap if no scaling is required.
     * @throws IllegalArgumentException if width is <= 0, or height is <= 0
     */
    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight,
                                            boolean filter, Bitmap.Config newConfig)
    {
        Matrix matrix;
        synchronized (Bitmap.class)
        {
            // small pool of just 1 matrix
            matrix = sScaleMatrix;
            sScaleMatrix = null;
        }

        if (matrix == null)
        {
            matrix = new Matrix();
        }

        final int width = src.getWidth();
        final int height = src.getHeight();
        final float sx = dstWidth / (float) width;
        final float sy = dstHeight / (float) height;
        matrix.setScale(sx, sy);

        Bitmap bitmap = createBitmap(src, 0, 0, width, height, matrix, filter, newConfig);

        synchronized (Bitmap.class)
        {
            // do we need to check for null? why not just assign everytime?
            if (sScaleMatrix == null)
            {
                sScaleMatrix = matrix;
            }
        }

        return bitmap;
    }


    /**
     * Returns an immutable bitmap from subset of the source bitmap,
     * transformed by the optional matrix. The new bitmap may be the
     * same object as source, or a copy may have been made. It is
     * initialized with the same density as the original bitmap.
     * <p/>
     * If the source bitmap is immutable and the requested subset is the
     * same as the source bitmap itself, then the source bitmap is
     * returned and no new bitmap is created.
     *
     * @param source       The bitmap we are subsetting
     * @param x_coordinate The x coordinate of the first pixel in source
     * @param y_coordinate The y coordinate of the first pixel in source
     * @param width        The number of pixels in each row
     * @param height       The number of rows
     * @param matrix       Optional matrix to be applied to the pixels
     * @param filter       true if the source should be filtered.
     *                     Only applies if the matrix contains more than just
     *                     translation.
     * @return A bitmap that represents the specified subset of source
     * @throws IllegalArgumentException if the x, y, width, height values are
     *                                  outside of the dimensions of the source bitmap, or width is <= 0,
     *                                  or height is <= 0
     */
    public static Bitmap createBitmap(Bitmap source, int x_coordinate, int y_coordinate, int width, int height,
                                      Matrix matrix, boolean filter, Bitmap.Config newConfig)
    {

        checkXYSign(x_coordinate, y_coordinate);
        checkWidthHeight(width, height);
        if (x_coordinate + width > source.getWidth())
        {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (y_coordinate + height > source.getHeight())
        {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }

        // check if we can just return our argument unchanged
        if (!source.isMutable() && x_coordinate == 0 && y_coordinate == 0 && width == source.getWidth() &&
                height == source.getHeight() && (matrix == null || matrix.isIdentity()))
        {
            return source;
        }

        int neww = width;
        int newh = height;
        Canvas canvas = new Canvas();
        Bitmap bitmap;
        Paint paint;

        Rect srcR = new Rect(x_coordinate, y_coordinate, x_coordinate + width, y_coordinate + height);
        RectF dstR = new RectF(0, 0, width, height);

        if (matrix == null || matrix.isIdentity())
        {
            bitmap = Bitmap.createBitmap(neww, newh, newConfig);
            paint = null;   // not needed
        } else
        {
            final boolean transformed = !matrix.rectStaysRect();

            RectF deviceR = new RectF();
            matrix.mapRect(deviceR, dstR);

            neww = Math.round(deviceR.width());
            newh = Math.round(deviceR.height());

            bitmap = Bitmap.createBitmap(neww, newh, newConfig);

            canvas.translate(-deviceR.left, -deviceR.top);
            canvas.concat(matrix);

            paint = new Paint();
            paint.setFilterBitmap(filter);
            if (transformed)
            {
                paint.setAntiAlias(true);
            }
        }

        // The new bitmap was created from a known bitmap source so assume that
        // they use the same density
        bitmap.setDensity(source.getDensity());
        bitmap.setHasAlpha(source.hasAlpha());

        canvas.setBitmap(bitmap);
        canvas.drawBitmap(source, srcR, dstR, paint);
        canvas.setBitmap(null);

        return bitmap;
    }


    /**
     * Common code for checking that x and y are >= 0
     *
     * @param x_coordinate x coordinate to ensure is >= 0
     * @param y_coordinate y coordinate to ensure is >= 0
     */
    private static void checkXYSign(int x_coordinate, int y_coordinate)
    {
        if (x_coordinate < 0)
        {
            throw new IllegalArgumentException("x must be >= 0");
        }
        if (y_coordinate < 0)
        {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    /**
     * Common code for checking that width and height are > 0
     *
     * @param width  width to ensure is > 0
     * @param height height to ensure is > 0
     */
    private static void checkWidthHeight(int width, int height)
    {
        if (width <= 0)
        {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (height <= 0)
        {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

}

