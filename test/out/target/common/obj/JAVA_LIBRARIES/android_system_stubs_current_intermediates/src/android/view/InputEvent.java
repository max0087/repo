package android.view;
public abstract class InputEvent
  implements android.os.Parcelable
{
InputEvent() { throw new RuntimeException("Stub!"); }
public abstract  int getDeviceId();
public final  android.view.InputDevice getDevice() { throw new RuntimeException("Stub!"); }
public abstract  int getSource();
public  boolean isFromSource(int source) { throw new RuntimeException("Stub!"); }
public abstract  long getEventTime();
public  int describeContents() { throw new RuntimeException("Stub!"); }
public static final android.os.Parcelable.Creator<android.view.InputEvent> CREATOR;
static { CREATOR = null; }
}
