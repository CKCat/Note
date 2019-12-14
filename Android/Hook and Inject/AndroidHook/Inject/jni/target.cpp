#include <jni.h>
#include <unistd.h>
#include "log.h"

extern "C"
int hook_entry(const char *param){
    LOGI("The process is pid = %d\n", getpid());
    LOGD("Hello, the param is: %s\n", param);
    return 0;
}

void __attribute__((constructor)) bridge_ctor(){
    for (int i = 0; i <10 ; ++i) {
        LOGD("Bridge constructor!\n");
    }
}
