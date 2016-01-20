package im.ene.lab.support.widget;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

public class AlphaForegroundColorSpan extends ForegroundColorSpan {

  private float mAlpha;

  public AlphaForegroundColorSpan(int color) {
    super(color);
  }

  public AlphaForegroundColorSpan(Parcel src) {
    super(src);
    mAlpha = src.readFloat();
  }

  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeFloat(mAlpha);
  }

  @Override public void updateDrawState(TextPaint ds) {
    ds.setColor(getAlphaColor());
  }

  private int getAlphaColor() {
    int foregroundColor = getForegroundColor();
    return Color.argb((int) (mAlpha * 255), Color.red(foregroundColor),
        Color.green(foregroundColor), Color.blue(foregroundColor));
  }

  public float getAlpha() {
    return mAlpha;
  }

  public void setAlpha(float alpha) {
    this.mAlpha = alpha;
  }
}