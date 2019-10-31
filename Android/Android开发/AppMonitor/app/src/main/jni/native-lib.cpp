//
// Created by CKCat on 2019/2/27.
//

#include <jni.h>
#include <string>
#include <string.h>
#include <android/log.h>

# define logDebug(fmt ...) __android_log_print(ANDROID_LOG_DEFAULT, "AppMonitor ", fmt);

jstring ToJString(JNIEnv* env, const std::string& value) {
    return env->NewStringUTF(value.c_str());
}

std::string ToCppString(JNIEnv* env, jstring value) {
    jboolean isCopy;
    const char* c_value = env->GetStringUTFChars(value, &isCopy);
    std::string result(c_value);
    if (isCopy == JNI_TRUE)
        env->ReleaseStringUTFChars(value, c_value);
    return result;
}

bool ToCppBool(jboolean value) {
    return value == JNI_TRUE;
}

jboolean ToJBool(bool value) {
    return value ? JNI_TRUE : JNI_FALSE;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_cat_appmonitor_Hook_Native_strstr(JNIEnv *env, jobject instance, jstring str, jstring str1) {

    std::string hello = ToCppString(env, str);
    std::string hello1 = ToCppString(env, str1);
    char* result = strstr(hello.c_str(), hello1.c_str());
    if (result == NULL){
        return ToJBool(false);
    }
    const char * tostring = hello.c_str();
    logDebug("%s", tostring);
    return ToJBool(true);
}