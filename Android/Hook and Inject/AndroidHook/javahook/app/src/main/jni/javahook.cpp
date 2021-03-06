#include "MethodHooker.h"
#include "common.h"
#include "android/log.h"
#include "stdio.h"
#include "stdlib.h"
#include "native.h"
#include <dlfcn.h>
#include <assert.h>
#include <jni.h>

#define ALOG(...) __android_log_print(ANDROID_LOG_VERBOSE, __VA_ARGS__)

static bool g_bAttatedT;
static JavaVM *g_JavaVM;

bool dvmIsStaticMethod(const Method* method) {
    return (method->accessFlags & ACC_STATIC) != 0;
}

bool dvmIsNativeMethod(const Method* method) {
    return (method->accessFlags & ACC_NATIVE) != 0;
}

int dvmComputeMethodArgsSize(const Method* method)
{
    return dexProtoComputeArgsSize(&method->prototype);
}

void init()
{
    g_bAttatedT = false;
    g_JavaVM = android::AndroidRuntime::mJavaVM;
}

static JNIEnv *GetEnv()
{
    int status;
    JNIEnv *envnow = NULL;
    status = g_JavaVM->GetEnv((void **)&envnow, JNI_VERSION_1_4);
    if(status < 0)
    {
        status = g_JavaVM->AttachCurrentThread(&envnow, NULL);
        if(status < 0)
        {
            return NULL;
        }
        g_bAttatedT = true;
    }
    return envnow;
}

static void DetachCurrent()
{
    if(g_bAttatedT)
    {
        g_JavaVM->DetachCurrentThread();
    }
}

static int computeJniArgInfo(const DexProto* proto)
{
    const char* sig = dexProtoGetShorty(proto);
    int returnType, jniArgInfo;
    u4 hints;

    /* The first shorty character is the return type. */
    switch (*(sig++)) {
        case 'V':
            returnType = DALVIK_JNI_RETURN_VOID;
            break;
        case 'F':
            returnType = DALVIK_JNI_RETURN_FLOAT;
            break;
        case 'D':
            returnType = DALVIK_JNI_RETURN_DOUBLE;
            break;
        case 'J':
            returnType = DALVIK_JNI_RETURN_S8;
            break;
        case 'Z':
        case 'B':
            returnType = DALVIK_JNI_RETURN_S1;
            break;
        case 'C':
            returnType = DALVIK_JNI_RETURN_U2;
            break;
        case 'S':
            returnType = DALVIK_JNI_RETURN_S2;
            break;
        default:
            returnType = DALVIK_JNI_RETURN_S4;
            break;
    }

    jniArgInfo = returnType << DALVIK_JNI_RETURN_SHIFT;

    hints = dvmPlatformInvokeHints(proto);

    if (hints & DALVIK_JNI_NO_ARG_INFO) {
        jniArgInfo |= DALVIK_JNI_NO_ARG_INFO;
    } else {
        assert((hints & DALVIK_JNI_RETURN_MASK) == 0);
        jniArgInfo |= hints;
    }

    return jniArgInfo;
}

int ClearException(JNIEnv *jenv){
    jthrowable exception = jenv->ExceptionOccurred();
    if (exception != NULL) {
        jenv->ExceptionDescribe();
        jenv->ExceptionClear();
        return true;
    }
    return false;
}


static jclass findAppClass(JNIEnv *jenv,const char *apn){
    //??????Loaders
    jclass clazzApplicationLoaders = jenv->FindClass("android/app/ApplicationLoaders");
    jthrowable exception = jenv->ExceptionOccurred();
    if (ClearException(jenv)) {
        ALOG("Exception","No class : %s", "android/app/ApplicationLoaders");
        return NULL;
    }
    jfieldID fieldApplicationLoaders = jenv->GetStaticFieldID(clazzApplicationLoaders,"gApplicationLoaders","Landroid/app/ApplicationLoaders;");
    if (ClearException(jenv)) {
        ALOG("Exception","No Static Field :%s","gApplicationLoaders");
        return NULL;
    }
    jobject objApplicationLoaders = jenv->GetStaticObjectField(clazzApplicationLoaders,fieldApplicationLoaders);
    if (ClearException(jenv)) {
        ALOG("Exception","GetStaticObjectField is failed [%s","gApplicationLoaders");
        return NULL;
    }
    //

    jfieldID fieldLoaders = jenv->GetFieldID(clazzApplicationLoaders,"mLoaders","Ljava/util/Map;");
    if (ClearException(jenv)) {
        fieldLoaders = jenv->GetFieldID(clazzApplicationLoaders,"mLoaders","Landroid/util/ArrayMap;");
        if(ClearException(jenv)){
            ALOG("Exception","No Field :%s","mLoaders");
            return NULL;
        }

    }

    /*
    jfieldID fieldLoaders = jenv->GetFieldID(clazzApplicationLoaders,"mLoaders","Ljava/util/Map;");
    if (ClearException(jenv)) {
          ALOG("Exception","No Field :%s","mLoaders");
          return NULL;


    }
    */
    jobject objLoaders = jenv->GetObjectField(objApplicationLoaders,fieldLoaders);
    if (ClearException(jenv)) {
        ALOG("Exception","No object :%s","mLoaders");
        return NULL;
    }
    //??????map??????values
    jclass clazzHashMap = jenv->GetObjectClass(objLoaders);
    jmethodID methodValues = jenv->GetMethodID(clazzHashMap,"values","()Ljava/util/Collection;");
    jobject values = jenv->CallObjectMethod(objLoaders,methodValues);

    jclass clazzValues = jenv->GetObjectClass(values);
    jmethodID methodToArray = jenv->GetMethodID(clazzValues,"toArray","()[Ljava/lang/Object;");
    if (ClearException(jenv)) {
        ALOG("Exception","No Method:%s","toArray");
        return NULL;
    }

    jobjectArray classLoaders = (jobjectArray)jenv->CallObjectMethod(values,methodToArray);
    if (ClearException(jenv)) {
        ALOG("Exception","CallObjectMethod failed :%s","toArray");
        return NULL;
    }

    int size = jenv->GetArrayLength(classLoaders);

    for(int i = 0 ; i < size ; i ++){
        jobject classLoader = jenv->GetObjectArrayElement(classLoaders,i);
        jclass clazzCL = jenv->GetObjectClass(classLoader);
        jmethodID loadClass = jenv->GetMethodID(clazzCL,"loadClass","(Ljava/lang/String;)Ljava/lang/Class;");
        jstring param = jenv->NewStringUTF(apn);
        jclass tClazz = (jclass)jenv->CallObjectMethod(classLoader,loadClass,param);
        if (ClearException(jenv)) {
            ALOG("Exception","No");
            continue;
        }
        return tClazz;
    }
    ALOG("Exception","No");
    return NULL;
}



bool HookDalvikMethod(jmethodID jmethod){
    Method *method = (Method*)jmethod;
    //??????!!????????????????????????native??????
    SET_METHOD_FLAG(method, ACC_NATIVE);

    int argsSize = dvmComputeMethodArgsSize(method);
    if (!dvmIsStaticMethod(method))
        argsSize++;

    method->registersSize = method->insSize = argsSize;

    if (dvmIsNativeMethod(method)) {
//        method->nativeFunc = dvmResolveNativeMethod;
//        method->jniArgInfo = computeJniArgInfo(&method->prototype);
    }
}

bool ClassMethodHook(HookInfo info){

    JNIEnv *jenv = GetEnv();

    jclass clazzTarget = jenv->FindClass(info.tClazz);
    if (ClearException(jenv)) {
        ALOG("Exception","ClassMethodHook[Can't find class:%s in bootclassloader",info.tClazz);

        clazzTarget = findAppClass(jenv,info.tClazz);
        if(clazzTarget == NULL){
            ALOG("Exception","%s","Error in findAppClass");
            return false;
        }
    }

    ALOG("LOG","Find calss success");
    jmethodID method = jenv->GetMethodID(clazzTarget,info.tMethod,info.tMeihodSig);
    if(method==NULL){
        ALOG("Exception","ClassMethodHook[Can't find method:%s",info.tMethod);
        return false;
    }
    else
        ALOG("LOG","Find Method ID success");

    HookDalvikMethod(method);
    JNINativeMethod gMethod[] = {
            {info.tMethod, info.tMeihodSig, info.handleFunc},
    };

    //func???NULL??????????????????,???????????????
    if(info.handleFunc != NULL){
        //??????!!????????????????????????????????????native??????
        if (jenv->RegisterNatives(clazzTarget, gMethod, 1) < 0) {
            ALOG("RegisterNatives","err");
            return false;
        }
    }

    DetachCurrent();
    return true;
}

JNIEXPORT jstring JNICALL test(JNIEnv *env, jclass clazz)
{
    __android_log_print(ANDROID_LOG_VERBOSE, "Log", "call <native_printf> in java");
    return (*env).NewStringUTF("haha ");;
}

HookInfo hookInfos[] = {
		{"android/net/wifi/WifiInfo","getMacAddress","()Ljava/lang/String;",(void*)test},
		{"com/example/javahook/MainActivity","test","()Ljava/lang/String;",(void*)test},
		//{"android/app/ApplicationLoaders","getText","()Ljava/lang/CharSequence;",(void*)test},
};

int  __attribute__((constructor)) Hook(){
    init();
    ClassMethodHook(hookInfos[2]);
}
