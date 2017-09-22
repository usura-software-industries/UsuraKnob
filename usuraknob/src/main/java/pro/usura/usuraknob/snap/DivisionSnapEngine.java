package pro.usura.usuraknob.snap;

import java.security.InvalidParameterException;

import pro.usura.usuraknob.KnobView;

public class DivisionSnapEngine extends SnapEngine {

	private int division = 18;

	public DivisionSnapEngine(int division) {
		setDivision(division);
	}

	public void snapEnd(float currentAngle, KnobView knobView) {

		int currentDivisionStep = calculateDivisionStep(currentAngle);
		float targetAngle = 360f / division * currentDivisionStep;
		knobView.animateTo(targetAngle);
	}

	@Override
	public void snapOngoing(float currentAngle, KnobView knobView) {
		//do nth
	}

	protected int calculateDivisionStep(float currentAngle) {
		return Math.round(currentAngle / 360f * division);
	}

	public int getDivision() {
		return division;
	}

	public void setDivision(int division) {
		if (division == 0) {
			throw new InvalidParameterException("Congratulations. You played yourself. / division by 0.");
		}
		this.division = division;
	}
}
