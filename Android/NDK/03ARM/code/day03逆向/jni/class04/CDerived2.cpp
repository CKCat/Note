#include "CDerived2.h"

CDerived2::CDerived2() {
	m_nMember = 3;
	printf("CDerived2()\r\n");
}
CDerived2::~CDerived2() {
	printf("~CDerived2()\r\n");
}
void CDerived2::fun3() {
	printf("CDerived2::fun3()\r\n");
}