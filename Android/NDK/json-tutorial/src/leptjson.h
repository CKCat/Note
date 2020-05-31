/**
 * leptjson 的头文件（header file），含有对外的类型和 API 函数声明。
 * 
 */

#ifndef LEPTJSON_H__
#define LEPTJSON_H__

typedef enum { 
    LEPT_NULL, 
    LEPT_FALSE, 
    LEPT_TRUE, 
    LEPT_NUMBER, 
    LEPT_STRING, 
    LEPT_ARRAY, 
    LEPT_OBJECT 
} lept_type;

enum{
    LEPT_PARSE_OK = 0,
    LEPT_PARSE_EXPECT_VALUE,
    LEPT_PARSE_INVALID_VALUE,
    LEPT_PARSE_ROOT_NOT_SINGULAR,
};

typedef struct 
{
    lept_type tpye;
}lept_value;



int left_parse(lept_value* v, const char* json);


#endif /* LEPTJSON_H__ */