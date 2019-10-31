#include "CDerived.h"

class CDerived2 :public CDerived{
public:
	CDerived2();
	virtual ~CDerived2();
	virtual void fun1();
	virtual void fun2();
	virtual void fun3();
private:
	int m_nMember;
};