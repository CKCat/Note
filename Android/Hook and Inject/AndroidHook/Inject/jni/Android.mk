LOCAL_PATH := $(call my-dir)  
  
include $(CLEAR_VARS)  
LOCAL_MODULE := inject   
LOCAL_SRC_FILES := inject.c   
LOCAL_LDLIBS += -llog  
include $(BUILD_EXECUTABLE)  
  
include $(CLEAR_VARS)  
LOCAL_MODULE := target   
LOCAL_SRC_FILES := target.cpp   
LOCAL_LDLIBS += -llog  
include $(BUILD_SHARED_LIBRARY)  


include $(CLEAR_VARS)  
LOCAL_MODULE := Hello   
LOCAL_SRC_FILES := Hello.cpp   
LOCAL_LDLIBS += -llog  
include $(BUILD_EXECUTABLE) 