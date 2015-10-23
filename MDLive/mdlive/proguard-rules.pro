# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\dhiman_da\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keep class org.dom4j.** { *; }
-dontwarn org.dom4j.**

-keep class javassist.util.** { *; }
-dontwarn javassist.util.**

-keep class javassist.tools.** { *; }
-dontwarn javassist.tools.**

-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

-keep class com.vsee.** { *; }
-dontwarn com.vsee.**

-keep class com.google.** { *; }
-dontwarn com.google.**

-keep class io.card.**
-keepclassmembers class io.card.** { *; }
-dontwarn io.card.**

-keep class com.commonsware.** { *; }
-dontwarn com.commonsware.**

-keep class org.reflections.** { *; }
-dontwarn org.reflections.**

-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.facebook.** {
   *;
}

-keepattributes Signature

-keep class com.mdlive.unifiedmiddleware.** { *;}


-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# SQLite

-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }


## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Volley

-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }

# AppCompact

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# Common Module

-keep class !unifiedmiddleware.** {*;}

-keep class !mobile.UILayer.** {*;}

# Maintain visibility of Inner class enums

-keepattributes InnerClasses
-keep public enum com.mdlive.embedkit.global.MDLiveConfig$** {
    **[] $VALUES;
    public *;
}