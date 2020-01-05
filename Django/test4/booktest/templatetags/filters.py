#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : filters
# Created by ckcat on 1/4/20
#导入Library类
from django.template import Library
#创建一个Library类对象
register = Library()
#使用装饰器进行注册,定义求余函数mod，将value对2求余
@register.filter
def mod(value):
    return value % 2 == 0

@register.filter
def mod_num(value, num):
    return value % num