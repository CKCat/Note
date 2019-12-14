LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := so
LOCAL_SRC_FILES := so.cpp MethodHooker.cpp
LOCAL_LDLIBS := -llog $(LOCAL_PATH)\libdvm.so $(LOCAL_PATH)\\libandroid_runtime.so

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE:= Test
LOCAL_SRC_FILES := Test.c
LOCAL_LDLIBS := -llog $(LOCAL_PATH)\libdvm.so $(LOCAL_PATH)\\libandroid_runtime.so

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE:= javahook
LOCAL_SRC_FILES := javahook.cpp
LOCAL_LDLIBS := -llog $(LOCAL_PATH)\libdvm.so $(LOCAL_PATH)\\libandroid_runtime.so

include $(BUILD_SHARED_LIBRARY)
