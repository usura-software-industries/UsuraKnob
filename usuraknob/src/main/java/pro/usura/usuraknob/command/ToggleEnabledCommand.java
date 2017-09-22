package pro.usura.usuraknob.command;

import pro.usura.usuraknob.KnobView;

public class ToggleEnabledCommand implements KnobCommand {

	private KnobView knobView;

	public ToggleEnabledCommand(KnobView knobView) {
		this.knobView = knobView;
	}

	@Override
	public void execute(boolean isEnabled) {
		knobView.setKnobEnabled(!knobView.isKnobEnabled());
	}
}
