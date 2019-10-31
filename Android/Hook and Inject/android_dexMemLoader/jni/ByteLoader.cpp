/*
*****************************************************************************
* File        : 
* Description : dex_mem_loader dexƒ⁄¥Êº”‘ÿ
* Creation    : 2016.12
* Author      : ll-hack
* History     :
*
******************************************************************************
**/

#include "common.h"
#include <stdlib.h>
#include <dlfcn.h>
#include <stdio.h>
#include "DexLoader.h"
#include <jni.h>
#include <dlfcn.h>
#include "common.h"
#include <vector>
#include <bits/unique_ptr.h>

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

#define JNIREG_CLASS "com/ll/loader/NativeTool"

using namespace std;

JNINativeMethod *dvm_dalvik_system_DexFile;
void (*openDexFile)(const u4* args, union JValue* pResult);

bool isArtMode()
{
    static bool done = false;
    static bool isArt = false;
    if (!done)
    {
        done = true;
        char propValue[PROP_VALUE_MAX] = {0};
        __system_property_get("persist.sys.dalvik.vm.lib.2", propValue);
        if (propValue[0] == '\x0')
            __system_property_get("persist.sys.dalvik.vm.lib", propValue);
        isArt = ( !strcmp(propValue, "libart.so") );
    }

    return isArt;
}

int lookupJniMethod(JNINativeMethod *table, const char *name, const char *sig, void(**fnPtrout)(u4 const *, union JValue *)) {
	int i = 0;
	while (table[i].name != NULL)
	{
		LDBG("lookup %d %s", i, table[i].name);
		if ((strcmp(name, table[i].name) == 0)
			&& (strcmp(sig, table[i].signature) == 0))
		{
			*fnPtrout = (void(*)(const u4 *, JValue *)) table[i].fnPtr;
			return 1;
		}
		i++;
	}
	return 0;
}

jint ArtLoadByte(const char* base, size_t size)
{
	const void* cookie = NULL;
	jint result = 0;
	int sdkVer = sdkVersion();
	unique_ptr<vector<const void*>> dex_files(new vector<const void*>());
    void* libArthandler = getLibartHandler("libart.so");
    if(!libArthandler){
    	return NULL;
    }
    switch (sdkVer)
    {
        case 19:
			LDBG("art19");
			cookie = LoadByteArt19(libArthandler, base, size);
		    if (cookie){
				result = static_cast<jint>(reinterpret_cast<uintptr_t>(cookie));
		    }
            break;
        case 21:
			LDBG("art21");
        	cookie = LoadByteArt21(libArthandler, base, size);
		    if (cookie){
		        dex_files.get()->push_back(cookie);
				result = static_cast<jlong>(reinterpret_cast<uintptr_t>(dex_files.release()));
		    }
            break;
        case 22:
			LDBG("art22");
        	cookie = LoadByteArt22(libArthandler, base, size);
		    if (cookie){
		        dex_files.get()->push_back(cookie);
				result = static_cast<jlong>(reinterpret_cast<uintptr_t>(dex_files.release()));
		    }
            break;
        case 23:
			LDBG("art23");
			cookie = LoadByteArt23(libArthandler, base, size);
		    if (cookie){
		        dex_files.get()->push_back(cookie);
				result = static_cast<jlong>(reinterpret_cast<uintptr_t>(dex_files.release()));
		    }
            break;
        default:
        	LDBG("LoadByteArt sdk:%d not implement!", sdkVer);
            break;
    }
	return result;
}


jint nativeloadDex(JNIEnv* env, jclass jv, jbyteArray dexArray, jlong dexLen)
{
	jint result = 0;
	u1* olddata = (u1*)env->GetByteArrayElements(dexArray, NULL);
	LDBG("dexfile: dexbuf addr:0x%x,len:%d", olddata, (size_t)dexLen);
	if (isArtMode()==true){
		result = ArtLoadByte((const char*)olddata, (size_t)dexLen);
		LDBG("nativeloadDex art cookie:0x%x", result);
		return result;
	}
    char* arr;
    arr = (char*)malloc(16 + dexLen);
    ArrayObject *ao=(ArrayObject*)arr;
    ao->length = dexLen;
    memcpy(arr+16,olddata,dexLen);
    u4 args[] = { (u4) ao };
    union JValue pResult;
    if(openDexFile != NULL) {
        openDexFile(args,&pResult);
    }else{
        result = -1;
    }
    result = (jint) pResult.l;
    LDBG("nativeloadDex cookie:0x%x" , result);
    return result;
}

__attribute__((constructor(101))) void init_GetOpenDexFileFuncPtr()
{
	if (isArtMode() == false){
		LDBG("Jni_init is start search opendexfile func");
		void *ldvm = (void*)dlopen("libdvm.so", RTLD_LAZY);
		dvm_dalvik_system_DexFile = (JNINativeMethod*)dlsym(ldvm, "dvm_dalvik_system_DexFile");
		if (0 == lookupJniMethod(dvm_dalvik_system_DexFile, "openDexFile", "([B)I", &openDexFile)) {
			openDexFile = NULL;
			LDBG("openDexFile method does not found ");
		}
		else{
			LDBG("openDexFile method is found !");
		}
	}
}


JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LDBG("Jni_call is start");

	JNIEnv* env;
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK){
		return -1;
	}
	JNINativeMethod methods[] ={
		{ "loadDex", "([BJ)I", (void *) nativeloadDex },
    };
    jclass clz = NULL;
    if(!(clz = env->FindClass(JNIREG_CLASS))){
    	LDBG("FindClass is errr");
    	return -1;
    }
    if(env->RegisterNatives(clz, methods, NELEM(methods))<0){
    	LDBG("RegisterNatives is errr");
    	return -1;
    }
    return JNI_VERSION_1_4;
}


