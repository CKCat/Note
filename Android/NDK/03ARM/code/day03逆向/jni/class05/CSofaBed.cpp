#include "CSofaBed.h"

CSofaBed::CSofaBed(){
    m_nHeight = 6;
}
CSofaBed::~CSofaBed(){					// 沙发床类的虚析构函数
    printf("virtual ~CSofaBed()\r\n");
}
int CSofaBed::SitDown(){					// 沙发可以坐下休息
    printf("CSofaBed::SitDown Sit down on the sofa bed\r\n");
    return 0;
}
int CSofaBed::Sleep(){						// 床可以用来睡觉
    printf("CSofaBed::Sleep go to sleep on the sofa bed\r\n");
    return 0;
}
int CSofaBed::GetHeight(){
    printf("CSofaBed::GetHeight()\r\n");
    return m_nHeight;
}