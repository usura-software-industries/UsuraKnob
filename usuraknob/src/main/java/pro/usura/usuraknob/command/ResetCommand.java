package pro.usura.usuraknob.command;

import pro.usura.usuraknob.KnobView;

public class ResetCommand implements KnobCommand {

	private KnobView knobView;

	public ResetCommand(KnobView knobView) {
		this.knobView = knobView;
	}

	@Override
	public void execute(boolean isEnabled) {
		if (isEnabled) {
			knobView.animateTo(knobView.getStartingAngle());
		}
	}
}
