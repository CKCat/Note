#include "CBed.h"

CBed::CBed(){
    m_nPrice = 3;
    m_nLength = 4;
    m_nWidth = 5;
    printf("CBed()\r\n");
}
CBed::~CBed(){						// 床类的虚析构函数
    printf("virtual ~CBed()\r\n");
}

int CBed::GetArea(){					// 获取床面积
    printf("virtual CBed::GetArea\r\n");
    return m_nLength * m_nWidth;
}

int CBed::Sleep(){						// 床可以用来睡觉
    printf("CBed::Sleep go to sleep\r\n");
    return 0;
}