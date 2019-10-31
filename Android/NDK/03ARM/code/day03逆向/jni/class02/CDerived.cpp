#include"CDerived.h"

CDerived::CDerived() {
    m_nMember = 3;
    printf("CDerived()\r\n");
}
CDerived::~CDerived() {
    printf("~CDerived()\r\n");
}
void CDerived::fun1() {
    printf("CDerived::fun1()\r\n");
}
void CDerived::fun3() {
    printf("CDerived::fun3()\r\n");
}