
# 1. ndk-build编译NDK的几种方法

## 方法1：

1. 创建一个空文件`AndroidManifest.xml`。

2. 创建一个`Application.mk`，内容如下所示：
```
APP_BUILD_SCRIPT := Android.mk
```
3. 创建一个`Android.mk`文件，内容如下所示：
```
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := foo.c
LOCAL_MODULE := foo
include $(BUILD_EXECUTABLE)
```

4. 使用以下命令编译
```
ndk-build NDK_APPLICATION_MK=$(pwd)/Application.mk
```

## 方法2：

直接使用以下命令进行编译：
```
ndk-build NDK_PROJECT_PATH=$(pwd)
或
ndk-build NDK_PROJECT_PATH=$(pwd) APP_BUILD_SCRIPT=$(pwd)/Android.mk
```

## 方法3：
将所有的文件放入jni目录，直接使用ndk-build命令编译即可。
