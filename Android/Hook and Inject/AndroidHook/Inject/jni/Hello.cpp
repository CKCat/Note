#include <jni.h>
#include <unistd.h>
#include "log.h"

int hook_entry(const char *param){
    LOGI("The process is pid = %d\n", getpid());
    LOGD("Hello, the param is: %s\n", param);
    return 0;
}

int main(){
    while(true){
        sleep(100);
        hook_entry("Hello World!");
    }
}
