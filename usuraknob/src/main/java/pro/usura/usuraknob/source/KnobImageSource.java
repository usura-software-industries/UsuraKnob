package pro.usura.usuraknob.source;

import android.content.Context;
import android.graphics.Bitmap;

public abstract class KnobImageSource {

	protected Bitmap bitmap;

	public abstract Bitmap createBitmap(Context context, int width, int height);

	public boolean hasBitmap() {
		return bitmap != null;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
