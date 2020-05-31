/**
 * 我们使用测试驱动开发（test driven development, TDD）。此文件包含测试程序，需要链接 leptjson 库
 */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"leptjson.h"

static int mian_ret = 0;
static int test_count =0;
static int test_pass = 0;

#define EXCEPT_EQ_BASE(equality, expect, actual, format)\
    do{\
        test_count++;\
        if (equality)\
            test_pass++;\
        else{\
           fprintf(stderr, "%s:%d: expect: " format " actual: " format "\n", __FILE__, __LINE__, expect, actual);\
            main_ret = 1;\
        }\
    }while (0)

#define EXPECT_EQ_INT(expect, actual) EXPECT_EQ_BASE((expect) == (actual), expect, actual, "%d")
