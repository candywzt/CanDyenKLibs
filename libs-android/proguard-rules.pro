#Serializable序列化
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Android XML属性
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}
# Android XML属性
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}
# Android Activity View
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# Enum枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelable序列化
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# R
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.view.View
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep class candyenk.android.** { *; }
