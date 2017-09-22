package pro.usura.usuraknob.snap;

import pro.usura.usuraknob.KnobView;

public abstract class SnapEngine {

	public abstract void snapEnd(float currentAngle, KnobView knobView);

	public abstract void snapOngoing(float currentAngle, KnobView knobView);
}
