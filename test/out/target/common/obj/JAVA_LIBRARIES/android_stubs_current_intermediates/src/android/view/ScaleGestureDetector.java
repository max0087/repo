package android.view;
public class ScaleGestureDetector
{
public static interface OnScaleGestureListener
{
public abstract  boolean onScale(android.view.ScaleGestureDetector detector);
public abstract  boolean onScaleBegin(android.view.ScaleGestureDetector detector);
public abstract  void onScaleEnd(android.view.ScaleGestureDetector detector);
}
public static class SimpleOnScaleGestureListener
  implements android.view.ScaleGestureDetector.OnScaleGestureListener
{
public  SimpleOnScaleGestureListener() { throw new RuntimeException("Stub!"); }
public  boolean onScale(android.view.ScaleGestureDetector detector) { throw new RuntimeException("Stub!"); }
public  boolean onScaleBegin(android.view.ScaleGestureDetector detector) { throw new RuntimeException("Stub!"); }
public  void onScaleEnd(android.view.ScaleGestureDetector detector) { throw new RuntimeException("Stub!"); }
}
public  ScaleGestureDetector(android.content.Context context, android.view.ScaleGestureDetector.OnScaleGestureListener listener) { throw new RuntimeException("Stub!"); }
public  ScaleGestureDetector(android.content.Context context, android.view.ScaleGestureDetector.OnScaleGestureListener listener, android.os.Handler handler) { throw new RuntimeException("Stub!"); }
public  boolean onTouchEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
public  void setQuickScaleEnabled(boolean scales) { throw new RuntimeException("Stub!"); }
public  boolean isQuickScaleEnabled() { throw new RuntimeException("Stub!"); }
public  void setStylusScaleEnabled(boolean scales) { throw new RuntimeException("Stub!"); }
public  boolean isStylusScaleEnabled() { throw new RuntimeException("Stub!"); }
public  boolean isInProgress() { throw new RuntimeException("Stub!"); }
public  float getFocusX() { throw new RuntimeException("Stub!"); }
public  float getFocusY() { throw new RuntimeException("Stub!"); }
public  float getCurrentSpan() { throw new RuntimeException("Stub!"); }
public  float getCurrentSpanX() { throw new RuntimeException("Stub!"); }
public  float getCurrentSpanY() { throw new RuntimeException("Stub!"); }
public  float getPreviousSpan() { throw new RuntimeException("Stub!"); }
public  float getPreviousSpanX() { throw new RuntimeException("Stub!"); }
public  float getPreviousSpanY() { throw new RuntimeException("Stub!"); }
public  float getScaleFactor() { throw new RuntimeException("Stub!"); }
public  long getTimeDelta() { throw new RuntimeException("Stub!"); }
public  long getEventTime() { throw new RuntimeException("Stub!"); }
}
