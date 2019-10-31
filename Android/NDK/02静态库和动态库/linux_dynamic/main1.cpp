#include <dlfcn.h>
int main(){
   void* handle =  dlopen("./libd2.so",RTLD_NOW );
   void (*print)();
   print = (void (*)())dlsym(handle, "_Z5printv");
   print();
   dlclose(handle);
}