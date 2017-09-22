package pro.usura.usuraknob.source;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class KnobDrawableSource extends KnobImageSource {

	private Drawable drawable;

	public KnobDrawableSource(Drawable drawable) {
		this.drawable = drawable;
	}

	@Override
	public Bitmap createBitmap(Context context, int width, int height) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}

		Bitmap unscaled = drawableToBitmap(drawable);
		bitmap = Bitmap.createScaledBitmap(unscaled, width, height, true);

		return bitmap;
	}

	private Bitmap drawableToBitmap(Drawable drawable) {

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
}
