package android.app;
public class TimePickerDialog
  extends android.app.AlertDialog
  implements android.content.DialogInterface.OnClickListener, android.widget.TimePicker.OnTimeChangedListener
{
public static interface OnTimeSetListener
{
public abstract  void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute);
}
public  TimePickerDialog(android.content.Context context, android.app.TimePickerDialog.OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) { super((android.content.Context)null,0); throw new RuntimeException("Stub!"); }
public  TimePickerDialog(android.content.Context context, int themeResId, android.app.TimePickerDialog.OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) { super((android.content.Context)null,0); throw new RuntimeException("Stub!"); }
public  void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) { throw new RuntimeException("Stub!"); }
public  void onClick(android.content.DialogInterface dialog, int which) { throw new RuntimeException("Stub!"); }
public  void updateTime(int hourOfDay, int minuteOfHour) { throw new RuntimeException("Stub!"); }
public  android.os.Bundle onSaveInstanceState() { throw new RuntimeException("Stub!"); }
public  void onRestoreInstanceState(android.os.Bundle savedInstanceState) { throw new RuntimeException("Stub!"); }
}
