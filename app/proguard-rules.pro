# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 不会对代码进行重命名或优化 会增加一定内存
#-dontobfuscate
# 不进行优化，建议使用此选项 会增加一定内存
#-dontoptimize
# 保留注解
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
 # 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify

# 模块专属
-keep class top.sacz.timtool.hook.InjectHook {*;}
-keep class * extends top.sacz.timtool.hook.base.BaseHookItem {*;}
-keep class top.sacz.timtool.entity.** {*;}

# 枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 可序列化类
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 可序列化类
-keep class * implements java.io.Serializable { *; }

# protobuf
-keepclassmembers public class * extends com.google.protobuf.MessageLite {*;}
-keepclassmembers public class * extends com.google.protobuf.MessageOrBuilder {*;}

#动态字节库 不排除可能会 java.lang.ExceptionInInitializerError
-keep class net.bytebuddy.** {*;}

#java.lang.IllegalStateException: Could not resolve dispatcher: j1.b.translate [class h1.a, class [B, class j1.a, class i1.a, class com.android.dx.dex.file.c]
-keep class com.android.dx.** {*;}

#base
-dontwarn javax.**
-dontwarn java.**

#bytebuddy
-dontwarn com.sun.**
-dontwarn edu.umd.**
