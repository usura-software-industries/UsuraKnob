package pro.usura.usuraknob;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import pro.usura.usuraknob.animation.KnobAnimator;
import pro.usura.usuraknob.animation.MinMaxTimeKnobAnimator;
import pro.usura.usuraknob.command.KnobCommand;
import pro.usura.usuraknob.command.ResetCommand;
import pro.usura.usuraknob.command.ToggleEnabledCommand;
import pro.usura.usuraknob.snap.NoSnapEngine;
import pro.usura.usuraknob.snap.SnapEngine;
import pro.usura.usuraknob.source.KnobDrawableSource;
import pro.usura.usuraknob.source.KnobImageSource;
import pro.usura.usuraknob.source.KnobState;

public class KnobView extends View implements GestureDetector.OnGestureListener {

	private final static int DOUBLE_TAP_INTERVAL_MS = 200;

	private GestureDetector gestureDetector;
	private Paint bitmapPaint = new Paint();
	private Drawable knobDrawable;

	private float minAngle = 0, maxAngle = 360, startingAngle = 0, currentAngle = 0, previousAngle = 0;
	private float previousX, previousY;
	private long lastTouchUp = 0;

	private KnobAnimator knobAnimator;
	private KnobImageSource source;
	private SnapEngine snapEngine;
	private KnobCommand doubleTapCommand;
	private KnobCommand longPressCommand;

	private KnobListener listener;
	boolean isKnobEnabled = true;

	public KnobView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public KnobView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		readAttrs(attrs, context);
		init(context);
	}

	public KnobView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		readAttrs(attrs, context);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public KnobView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		readAttrs(attrs, context);
		init(context);
	}

	private void init(Context context) {
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setFilterBitmap(true);
		setWillNotDraw(false);

		source = createSource();
		knobAnimator = createAnimator();
		snapEngine = createSnapEngine();

		doubleTapCommand = new ResetCommand(this);
		longPressCommand = new ToggleEnabledCommand(this);

		gestureDetector = new GestureDetector(context, this);

		currentAngle = startingAngle;
	}

	private void readAttrs(AttributeSet attrs, Context context) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KnobView, 0, 0);
		try {
			minAngle = a.getInteger(R.styleable.KnobView_minAngle, 0);
			maxAngle = a.getInteger(R.styleable.KnobView_maxAngle, 360);
			startingAngle = a.getInteger(R.styleable.KnobView_startingAngle, (int) minAngle);
			knobDrawable = a.getDrawable(R.styleable.KnobView_knobSrc);
		} finally {
			a.recycle();
		}
	}

	public void setRotorAngle(float targetAngle) {
		if (targetAngle % 360 > maxAngle) {
			targetAngle = maxAngle;
			if (isAnimating()) {
				knobAnimator.stopAnimation();
			}
		}

		if (targetAngle % 360 < minAngle) {
			targetAngle = minAngle;
			if (isAnimating()) {
				knobAnimator.stopAnimation();
			}
		}

		currentAngle = targetAngle;

		invalidate();
		notifyListener();
	}

	public boolean isAnimating() {
		return knobAnimator != null && knobAnimator.isAnimating();
	}

	public void animateTo(float angle) {
		knobAnimator.stopAnimation();
		knobAnimator.animateTo(this, angle);
	}

	public KnobImageSource createSource() {
		return new KnobDrawableSource(knobDrawable);
	}

	public KnobAnimator createAnimator() {
		return new MinMaxTimeKnobAnimator();
	}

	public SnapEngine createSnapEngine() {
		return new NoSnapEngine();
	}

	private float pointAngle(float x1, float y1, float x2, float y2) {
		x1 -= 0.5f;
		x2 -= 0.5f;
		y1 -= 0.5f;
		y2 -= 0.5f;

		float angle = (float) Math.toDegrees(Math.atan2(y2, x2) - Math.atan2(y1, x1));

		//adjust for quarter of circle switch which causes 1 - angle
		if (angle < -180) {
			angle += 360;
		}

		if (angle > 180) {
			angle -= 360;
		}

		return angle;
	}

	public void setKnobEnabled(boolean enabled) {
		if (enabled) {
			bitmapPaint.setAlpha(255);
		} else {
			bitmapPaint.setAlpha((int) (0.4f * 255));
		}
		isKnobEnabled = enabled;
		invalidate();
	}

	public float getNormalizedValue() {
		return (currentAngle - getMinAngle()) / (getMaxAngle() - getMinAngle());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!source.hasBitmap()) {
			source.createBitmap(getContext(), getWidth(), getHeight());
		}

		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.rotate(currentAngle, canvas.getWidth() / 2, canvas.getHeight() / 2);
		canvas.drawBitmap(source.getBitmap(), 0, 0, bitmapPaint);
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isAnimating()) {
			return true;
		}

		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
			case (MotionEvent.ACTION_DOWN):
				break;
			case (MotionEvent.ACTION_MOVE):
				break;
			case (MotionEvent.ACTION_UP):
				long currentTimeMS = System.currentTimeMillis();
				if (currentTimeMS - lastTouchUp <= DOUBLE_TAP_INTERVAL_MS) {
					onDoubleTap();
				} else {
					if (Math.abs(currentAngle - previousAngle) > 0) {
						snapEngine.snapEnd(getCurrentAngle(), this);
					}
				}
				lastTouchUp = currentTimeMS;

				notifyListener();
				break;
		}

		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (!isKnobEnabled) {
			return false;
		}

		getParent().requestDisallowInterceptTouchEvent(true);
		float x = e.getX() / ((float) getWidth());
		float y = e.getY() / ((float) getHeight());
		previousX = x;
		previousY = y;
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (!isKnobEnabled) {
			return false;
		}

		getParent().requestDisallowInterceptTouchEvent(true);
		float x = e2.getX() / ((float) getWidth());
		float y = e2.getY() / ((float) getHeight());

		float angleExp = pointAngle(previousX, previousY, x, y);

		previousX = x;
		previousY = y;

		previousAngle = currentAngle;
		currentAngle += angleExp;

		snapEngine.snapOngoing(currentAngle, this);

		setRotorAngle(currentAngle);

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (longPressCommand != null) {
			longPressCommand.execute(isKnobEnabled);
		}
	}

	public void onDoubleTap() {
		if (doubleTapCommand != null) {
			doubleTapCommand.execute(isKnobEnabled);
		}
	}

	private void notifyListener() {
		if (listener != null) {
			listener.onAngleChanged(currentAngle);
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int size;
		if (widthMode == MeasureSpec.EXACTLY && widthSize > 0) {
			size = widthSize;
		} else if (heightMode == MeasureSpec.EXACTLY && heightSize > 0) {
			size = heightSize;
		} else {
			size = widthSize < heightSize ? widthSize : heightSize;
		}

		int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
		super.onMeasure(finalMeasureSpec, finalMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		source.createBitmap(getContext(), w, h);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (isAnimating()) {
			knobAnimator.stopAnimation();
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		KnobState knobState = new KnobState(superState);
		knobState.save(this);
		return knobState;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		KnobState knobState = (KnobState) state;
		super.onRestoreInstanceState(knobState.getSuperState());
		knobState.restore(this);
	}
	public KnobListener getListener() {
		return listener;
	}

	public void setListener(KnobListener listener) {
		this.listener = listener;
	}

	public float getMinAngle() {
		return minAngle;
	}

	public void setMinAngle(float minAngle) {
		this.minAngle = minAngle;
	}

	public float getMaxAngle() {
		return maxAngle;
	}

	public void setMaxAngle(float maxAngle) {
		this.maxAngle = maxAngle;
	}

	public float getStartingAngle() {
		return startingAngle;
	}

	public void setStartingAngle(float startingAngle) {
		this.startingAngle = startingAngle;
	}

	public float getCurrentAngle() {
		return currentAngle;
	}

	public void setCurrentAngle(float currentAngle) {
		this.currentAngle = currentAngle;
	}

	public boolean isKnobEnabled() {
		return isKnobEnabled;
	}

	public KnobAnimator getKnobAnimator() {
		return knobAnimator;
	}

	public void setKnobAnimator(KnobAnimator knobAnimator) {
		this.knobAnimator = knobAnimator;
	}

	public SnapEngine getSnapEngine() {
		return snapEngine;
	}

	public void setSnapEngine(SnapEngine snapEngine) {
		this.snapEngine = snapEngine;
	}

	public KnobCommand getDoubleTapCommand() {
		return doubleTapCommand;
	}

	public void setDoubleTapCommand(KnobCommand doubleTapCommand) {
		this.doubleTapCommand = doubleTapCommand;
	}

	public KnobCommand getLongPressCommand() {
		return longPressCommand;
	}

	public void setLongPressCommand(KnobCommand longPressCommand) {
		this.longPressCommand = longPressCommand;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		//do nth
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//do nth
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//do nth
		return false;
	}
}
