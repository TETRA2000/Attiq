package im.ene.lab.attiq.widgets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by eneim on 4/6/15.
 */
public class RoundedTransformation implements Transformation {
  private int mBorderSize;
  private int mCornerRadius = 0;
  private int mColor;

  private int halfBorder;

  public RoundedTransformation(int borderSize, int color) {
    this.mBorderSize = borderSize;
    this.mColor = color;
    this.halfBorder = mBorderSize / 2;
  }

  public RoundedTransformation(int borderSize, int color, int cornerRadius) {
    this.mBorderSize = borderSize;
    this.mColor = color;
    this.mCornerRadius = cornerRadius;
    this.halfBorder = mBorderSize / 2;
    if (halfBorder < 1) {
      halfBorder = 1;
    }
  }

  @Override
  public Bitmap transform(Bitmap source) {
    int width = source.getWidth();
    int height = source.getHeight();

    Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(image);
    canvas.drawARGB(0, 0, 0, 0);

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect rect = new Rect(0, 0, width, height);

    if (this.mCornerRadius == 0) {
      canvas.drawRect(rect, paint);
    } else {
      canvas.drawRoundRect(new RectF(rect),
          this.mCornerRadius, this.mCornerRadius, paint);
    }

    paint.setXfermode(new PorterDuffXfermode((PorterDuff.Mode.SRC_IN)));
    canvas.drawBitmap(source, rect, rect, paint);

    Bitmap output;

    if (this.mBorderSize == 0) {
      output = image;
    } else {
      width = width + this.mBorderSize * 2;
      height = height + this.mBorderSize * 2;

      output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      canvas.setBitmap(output);
      canvas.drawARGB(0, 0, 0, 0);

      rect = new Rect(halfBorder, halfBorder, width - halfBorder, height - halfBorder);

      paint.setXfermode(null);
      paint.setColor(this.mColor);
      paint.setStyle(Paint.Style.STROKE);
      paint.setStrokeWidth(mBorderSize);

      canvas.drawRoundRect(new RectF(rect),
          this.mCornerRadius + halfBorder,
          this.mCornerRadius + halfBorder, paint);

      paint.setColor(Color.WHITE);
      paint.setStyle(Paint.Style.FILL);

      canvas.drawRoundRect(new RectF(halfBorder, halfBorder,
              width - halfBorder, height - halfBorder),
          this.mCornerRadius + halfBorder, this.mCornerRadius + halfBorder, paint);

      canvas.drawBitmap(image, this.mBorderSize, this.mBorderSize, null);
    }

    if (source != output) source.recycle();

    return output;
  }

  @Override
  public String key() {
    return toString();
  }

  @Override public String toString() {
    return "RoundedTransformation{" +
        "mBorderSize=" + mBorderSize +
        ", mCornerRadius=" + mCornerRadius +
        ", mColor=" + mColor +
        '}';
  }
}
