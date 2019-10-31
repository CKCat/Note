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
#ifndef _DEXLOADER_H
#define _DEXLOADER_H

#include "DexHeader.h"
#include "common.h"
#include <stdlib.h>
#include <dlfcn.h>
#include <stdio.h>

#include <jni.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include <fcntl.h>
#include <vector>
#include <bits/unique_ptr.h>

using namespace std;



#define  __TESTER

#ifdef __TESTER
#define LOG_TAG "LL_DexLoader"
#ifdef __BINTEST
#define LDBG(format, ...) printf (format, ##__VA_ARGS__)
#else
#define LDBG(format, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, format, ##__VA_ARGS__)
#endif
#else
#define LDBG(format, ...)
#endif

int sdkVersion();
void* getLibartHandler(const char* libartName);
const void* LoadByteArt19(void* libArthandler, const char* base, size_t size);
const void* LoadByteArt21(void* libArthandler, const char* base, size_t size);
const void* LoadByteArt22(void* libArthandler, const char* base, size_t size);
const void* LoadByteArt23(void* libArthandler, const char* base, size_t size);

#endif

