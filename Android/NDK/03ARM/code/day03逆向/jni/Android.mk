LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -fPIE
LOCAL_LDFLAGS += -fPIE -pie
LOCAL_MODULE    := hello
LOCAL_SRC_FILES := ./class05/CBed.cpp ./class05/CSofa.cpp ./class05/CSofaBed.cpp ./class05/CFurniture.cpp ./class05/main.cpp
LOCAL_LDLIBS := -llog 

include $(BUILD_EXECUTABLE)
