#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : urls
# Created by ckcat on 1/27/20

from django.conf.urls import url
from booktest import views

urlpatterns = [
    url('^$', views.index),
    url('^editor/', views.editor),
    url('^show/', views.show),
    url('^query/', views.query),
    url('^send/', views.send),
]