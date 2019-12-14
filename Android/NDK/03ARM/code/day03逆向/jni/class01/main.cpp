#include <stdio.h>
#include "CBase.h"

int main(int argc, char* argv[]){
    CBase base(2, 3);
    base.fun2(argc);
    CBase* pbase = new CBase(4, 5);
    if(pbase != NULL)
        pbase->fun1(argc);
    delete pbase;
}