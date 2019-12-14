#include "CDerived.h"

int main(int argc, char* argv[]){
    CDerived base;
    base.fun2();
    CBase* pbase = new CDerived;
    if(pbase != NULL)
        pbase->fun1();
    delete pbase;
}