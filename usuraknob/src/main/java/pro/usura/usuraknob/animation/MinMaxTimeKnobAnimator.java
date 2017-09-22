package pro.usura.usuraknob.animation;

import android.os.Handler;

import pro.usura.usuraknob.KnobView;

public class MinMaxTimeKnobAnimator extends KnobAnimator {

	//~60fps
	private final static int UPDATE_INTERVAL_MS = 16;
	private int maxAnimateTimeMS = 1024;
	private int minAnimateTimeMS = 128;

	private int repeatCount = 0;
	private float step;
	protected KnobView knobView;
	private Handler handler = new Handler();
	private float targetAngle;

	public void animateTo(KnobView knobView, float targetAngle) {
		this.knobView = knobView;
		this.targetAngle = targetAngle;

		float currentAngle = knobView.getCurrentAngle();
		float route = (targetAngle - currentAngle) % 360;
		int routeAnimateTime = Math.round((Math.abs(route) / 360f) * (float) maxAnimateTimeMS);
		routeAnimateTime = Math.max(minAnimateTimeMS, routeAnimateTime);

		repeatCount = routeAnimateTime / UPDATE_INTERVAL_MS;
		step = route / repeatCount;

		handler.postDelayed(runAnim(), UPDATE_INTERVAL_MS);
		isAnimating = true;
	}

	public void stopAnimation() {
		handler.removeCallbacksAndMessages(null);
		isAnimating = false;
	}

	public boolean isAnimating() {
		return isAnimating;
	}

	private Runnable runAnim() {
		return new Runnable() {
			@Override
			public void run() {
				if (repeatCount > 0) {
					knobView.setRotorAngle(knobView.getCurrentAngle() + step);
					repeatCount--;
					handler.postDelayed(runAnim(), UPDATE_INTERVAL_MS);
				} else {
					knobView.setRotorAngle(targetAngle);
					isAnimating = false;
					knobView = null;
				}
			}
		};
	}

	public int getMaxAnimateTimeMS() {
		return maxAnimateTimeMS;
	}

	public void setMaxAnimateTimeMS(int maxAnimateTimeMS) {
		this.maxAnimateTimeMS = maxAnimateTimeMS;
	}

	public int getMinAnimateTimeMS() {
		return minAnimateTimeMS;
	}

	public void setMinAnimateTimeMS(int minAnimateTimeMS) {
		this.minAnimateTimeMS = minAnimateTimeMS;
	}
}
