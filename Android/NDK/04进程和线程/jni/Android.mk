LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := thread
LOCAL_SRC_FILES := thread.cpp

include $(BUILD_EXECUTABLE)