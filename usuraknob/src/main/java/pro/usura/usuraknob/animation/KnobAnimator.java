package pro.usura.usuraknob.animation;

import pro.usura.usuraknob.KnobView;

public abstract class KnobAnimator {

	protected boolean isAnimating = false;

	public abstract void animateTo(KnobView knobView, float targetAngle);

	public abstract void stopAnimation();

	public boolean isAnimating() {
		return isAnimating;
	}
}
