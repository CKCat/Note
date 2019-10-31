LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -fPIE
LOCAL_LDFLAGS += -fPIE -pie
LOCAL_MODULE    := hello
LOCAL_SRC_FILES := hello.s
LOCAL_LDLIBS := -llog 

include $(BUILD_EXECUTABLE)
