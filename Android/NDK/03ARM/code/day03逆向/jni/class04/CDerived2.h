#include "CDerived.h"

class CDerived2 :public CDerived{
public:
	CDerived2();
	~CDerived2();
	void fun3();
private:
	int m_nMember;
};