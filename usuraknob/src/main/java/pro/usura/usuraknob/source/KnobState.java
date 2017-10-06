package pro.usura.usuraknob.source;

import android.os.Parcel;
import android.os.Parcelable;

import pro.usura.usuraknob.KnobView;

public class KnobState implements Parcelable {

	public final static String SUPER_KEY = "superStateKey";
	public final static String STATE_KEY = "stateKey";

	private float minAngle = 0, maxAngle = 0, startingAngle = 0, currentAngle = 0;

	public KnobState() {
	}

	public void save(KnobView knobView) {
		minAngle = knobView.getMinAngle();
		maxAngle = knobView.getMaxAngle();
		startingAngle = knobView.getStartingAngle();
		currentAngle = knobView.getCurrentAngle();
	}

	public void restore(KnobView knobView) {
		knobView.setMinAngle(minAngle);
		knobView.setMaxAngle(maxAngle);
		knobView.setStartingAngle(startingAngle);
		knobView.setRotorAngle(currentAngle);
	}

	protected KnobState(Parcel in) {
		minAngle = in.readFloat();
		maxAngle = in.readFloat();
		startingAngle = in.readFloat();
		currentAngle = in.readFloat();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(minAngle);
		dest.writeFloat(maxAngle);
		dest.writeFloat(startingAngle);
		dest.writeFloat(currentAngle);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<KnobState> CREATOR = new Parcelable.Creator<KnobState>() {
		@Override
		public KnobState createFromParcel(Parcel in) {
			return new KnobState(in);
		}

		@Override
		public KnobState[] newArray(int size) {
			return new KnobState[size];
		}
	};
}
