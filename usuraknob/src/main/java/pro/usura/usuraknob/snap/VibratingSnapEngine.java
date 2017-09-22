package pro.usura.usuraknob.snap;

import android.os.Vibrator;

import pro.usura.usuraknob.KnobView;

public class VibratingSnapEngine extends DivisionSnapEngine {

	private Vibrator vibrator;
	private int previousDivisionStep = -1;
	private int vibratingTimeMS;

	public VibratingSnapEngine(Vibrator vibrator, int division, int vibrationTimeMS) {
		super(division);
		this.vibrator = vibrator;
		this.vibratingTimeMS = vibrationTimeMS;
	}

	@Override
	public void snapEnd(float currentAngle, KnobView knobView) {
		super.snapEnd(currentAngle, knobView);
		previousDivisionStep = -1;
	}

	@Override
	public void snapOngoing(float currentAngle, KnobView knobView) {

		if (previousDivisionStep == -1) {
			previousDivisionStep = calculateDivisionStep(currentAngle);
			return;
		}

		int currentDivisionStep = calculateDivisionStep(currentAngle);

		if ((currentDivisionStep - previousDivisionStep != 0)) {
			previousDivisionStep = currentDivisionStep;
			vibrator.vibrate(vibratingTimeMS);
		}
	}

	public void setVibratingTimeMS(int vibratingTimeMS) {
		this.vibratingTimeMS = vibratingTimeMS;
	}

	public int getVibratingTimeMS() {
		return vibratingTimeMS;
	}
}
