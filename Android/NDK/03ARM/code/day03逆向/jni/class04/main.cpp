#include "CDerived2.h"

int main(int argc, char* argv[]){
	CDerived2 base;
	base.fun1();
	base.fun2();
	base.fun3();
	CBase* pbase = new CDerived2;
	if(pbase != NULL)
		pbase->fun1();
		pbase->fun2();
	delete pbase;
}