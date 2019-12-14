#include "CDerived2.h"

int main(int argc, char* argv[]){
	CDerived2 base;
	base.fun2();
	CBase* pbase = new CDerived2;
	if(pbase != NULL)
		pbase->fun1();
	delete pbase;
}