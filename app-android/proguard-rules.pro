# Parcelable序列化
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
  *;
}

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
# AIDL 服务类
 -keep class com.candyenk.demo.service.IUS { *; }
 
# -keep class de.robv.android.xposed.* { *; }
# -keep class javax.script.*{ *; }
# -keep class org.apache.bcel.classfile.*{ *; }
# -keep class org.apache.bcel.generic.*{ *; }