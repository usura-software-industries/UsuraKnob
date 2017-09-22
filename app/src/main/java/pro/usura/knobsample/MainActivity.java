package pro.usura.knobsample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pro.usura.usuraknob.KnobListener;
import pro.usura.usuraknob.KnobView;
import pro.usura.usuraknob.snap.NoSnapEngine;
import pro.usura.usuraknob.snap.SnapEngine;
import pro.usura.usuraknob.snap.VibratingSnapEngine;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.knob)
	protected KnobView knobView;
	@BindView(R.id.knob_percentage_tv)
	protected TextView percentageTV;
	@BindView(R.id.toggle_snap_mode)
	protected TextView snapToggle;

	private boolean shouldSnap = false;

	private SnapEngine noSnap;
	private SnapEngine vibratingSnap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setPercentageText();

		knobView.setListener(new KnobListener() {
			@Override
			public void onAngleChanged(float angle) {
				setPercentageText();
			}
		});

		noSnap = new NoSnapEngine();

		int snapByDegrees = 20;
		int fullCircle = 360;
		int vibrationTimeMS = 20;
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibratingSnap = new VibratingSnapEngine(vibrator, fullCircle / snapByDegrees, vibrationTimeMS);

//		KnobCommand customCommand = new KnobCommand() {
//
//			@Override
//			public void execute(boolean isEnabled) {
//				//do your stuff here
//			}
//		};
//
//		knobView.setDoubleTapCommand(customCommand);
//
//		knobView.setLongPressCommand(null);
	}

	@OnClick(R.id.animate_min)
	protected void animateToMin() {
		knobView.animateTo(knobView.getMinAngle());
	}

	@OnClick(R.id.animate_max)
	protected void animateToMax() {
		knobView.animateTo(knobView.getMaxAngle());
	}

	@OnClick(R.id.toggle_snap_mode)
	protected void toggleSnapClick() {
		shouldSnap = !shouldSnap;
		setSnapping(shouldSnap);
	}

	@OnClick(R.id.contact_us_btn)
	protected void contactUsClick() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.usura.pro")));
	}

	private void setPercentageText() {
		float normalized = knobView.getNormalizedValue();
		int percentage = (int) (normalized * 100f);
		percentageTV.setText(percentage + "%");
	}

	private void setSnapping(boolean shouldSnap) {
		knobView.setSnapEngine(shouldSnap ? vibratingSnap : noSnap);
		snapToggle.setText(shouldSnap ? R.string.snap_mode_snap_and_vibrate : R.string.snap_mode_no_snap);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
