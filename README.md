# UsuraKnob
Knob control that aims to replace standard android slider. With style.

<img src="https://github.com/usura-software-industries/UsuraKnob/blob/master/demos/usuraknob-demo.gif?" width=350>

### Gradle installation

#### Add jitpack to your root gradle file
```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

#### and to your app gradle
```gradle
dependencies {
  compile 'com.github.usura-software-industries:UsuraKnob:0.1.1'
}
```

### XML declaration
```xml
<pro.usura.usuraknob.KnobView
  android:id="@+id/knob"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  app:knobSrc="@drawable/knob_image"
  app:maxAngle="300"
  app:minAngle="60"
  app:startingAngle="180" />
```

It will always be square and does support weights.

### Code and customisation

#### Animating

Default animator calculates the minimal route and goes to target angle. Animation time will vary based on route length. You
can change this behavior by implementing your own knob aniamtor
```java
  float targetAngle = 300f;
  knobView.animateTo(targetAngle);
  
  knobView.setKnobAnimator(KnobAnimator knobAnimator);
```

#### Long press and double tap actions

Knob supports long press and double tap gestures. By defualt those will be togglling enabled/disabled state and reseting knob to starting value. Actions can be customised by implementing and setting KnobCommand or disabled by setting null.

```java
 KnobCommand customCommand = new KnobCommand() {
    @Override
    public void execute(boolean isEnabled) {
      //do your stuff here
    }
 };

knobView.setDoubleTapCommand(customCommand);
knobView.setLongPressCommand(null);
```

#### Snapping

Knob also supports snapping to predefined values and vibration while passing through a snap point to mimic "clicking" effect on some of the real life knobs. Default implementations are: NoSnapEngine, DivisionSnapEngine and VibratingSnapEngine. Custom snapping can be made making your own implementation of SnapEngine.

```java
noSnap = new NoSnapEngine();
  
int snapByDegrees = 20;
int fullCircle = 360;
int vibrationTimeMS = 20;
Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
  
vibratingSnap = new VibratingSnapEngine(vibrator, fullCircle / snapByDegrees, vibrationTimeMS);
```
