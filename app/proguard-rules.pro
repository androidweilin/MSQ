#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
-keep class com.wkbp.msq.bean.** { *; }
-keep class com.wkbp.msq.result.bean.** { *; }


#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------
# asyncHttp
-dontwarn com.loopj.android.http.**
-keep class com.loopj.android.http.** { *;}
# mime
-dontwarn org.apache.http.entity.mime.**
-keep class org.apache.http.entity.mime.* { *; }
#apache.http.legacy
-dontwarn android.net.compatibility.**
-keep class android.net.compatibility.** { *;}
-dontwarn android.net.http.**
-keep class android.net.http.** { *;}
-dontwarn com.android.internal.http.multipart.**
-keep class com.android.internal.http.multipart.** { *;}
-dontwarn org.apache.commons.codec.**
-keep class org.apache.commons.codec.** { *;}
-dontwarn org.apache.commons.logging.**
-keep class org.apache.commons.logging.** { *;}
-dontwarn org.apache.http.**
-keep class org.apache.http.** { *;}
# Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }

#imageLoader
-dontwarn com.nostra13.universalimageloader.cache.disc.**
-keep class com.nostra13.universalimageloader.cache.disc.** { *; }
-dontwarn com.nostra13.universalimageloader.cache.memory.**
-keep class com.nostra13.universalimageloader.cache.memory.** { *; }
-dontwarn com.nostra13.universalimageloader.core.**
-keep class com.nostra13.universalimageloader.core.** { *; }
-dontwarn com.nostra13.universalimageloader.utils.**
-keep class com.nostra13.universalimageloader.utils.** { *; }

#hamcrest
-dontwarn org.hamcrest.**
-keep class org.hamcrest.** {*;}

#自定义View
-dontwarn com.wkbp.msq.customView.**
-keep class com.wkbp.msq.customView.** {*;}

#自定义swipe
-dontwarn com.wkbp.msq.swipe.**
-keep class com.wkbp.msq.swipe.** {*;}

#annotation
-dontwarn android.support.annotation.**
-keep class android.support.annotation.** {*;}

#v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** {*;}
#v7
-dontwarn android.support.v7.**
-keep class android.support.v7.** {*;}


#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------



#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
#代码混淆的压缩比例，值在0-7之间
-optimizationpasses 5
#混淆后类名都为小写
-dontusemixedcaseclassnames
#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
#指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
#不做预校验的操作
-dontpreverify
#生成原类名和混淆后的类名的映射文件
-verbose
-printmapping proguardMapping.txt
#指定混淆是采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#不混淆Annotation
-keepattributes *Annotation*,InnerClasses
#不混淆泛型
-keepattributes Signature
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
#----------------------------------------------------------------------------
#