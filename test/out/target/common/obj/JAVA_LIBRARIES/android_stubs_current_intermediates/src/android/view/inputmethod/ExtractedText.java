package android.view.inputmethod;
public class ExtractedText
  implements android.os.Parcelable
{
public  ExtractedText() { throw new RuntimeException("Stub!"); }
public  void writeToParcel(android.os.Parcel dest, int flags) { throw new RuntimeException("Stub!"); }
public  int describeContents() { throw new RuntimeException("Stub!"); }
public static final android.os.Parcelable.Creator<android.view.inputmethod.ExtractedText> CREATOR;
public static final int FLAG_SELECTING = 2;
public static final int FLAG_SINGLE_LINE = 1;
public int flags;
public int partialEndOffset;
public int partialStartOffset;
public int selectionEnd;
public int selectionStart;
public int startOffset;
public java.lang.CharSequence text;
static { CREATOR = null; }
}
